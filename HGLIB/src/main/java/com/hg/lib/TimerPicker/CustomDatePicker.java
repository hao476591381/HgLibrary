package com.hg.lib.TimerPicker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hg.lib.R;
import com.hg.lib.tool.DateTool;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 说明：自定义时间选择器
 */
public class CustomDatePicker implements TimePickerView.OnSelectListener, View.OnClickListener {

    private TextView hgTimeTitle;
    private TimePickerView hgTimeYear;
    private TimePickerView hgTimeMonth;
    private TimePickerView hgTimeDay;
    private TimePickerView hgTimeHour;
    private TextView hgTimeHourTv;
    private TimePickerView hgTimeMinute;
    private TextView hgTimeMinuteTv;
    private Context mContext;
    private Callback mCallback;
    private Calendar mBeginTime, mEndTime, mSelectedTime;
    private boolean mCanDialogShow;
    private Dialog mPickerDialog;

    private int mBeginYear, mBeginMonth, mBeginDay, mBeginHour, mBeginMinute,
            mEndYear, mEndMonth, mEndDay, mEndHour, mEndMinute;
    private List<String> mYearUnits = new ArrayList<>(),
            mMonthUnits = new ArrayList<>(),
            mDayUnits = new ArrayList<>(),
            mHourUnits = new ArrayList<>(),
            mMinuteUnits = new ArrayList<>();
    private DecimalFormat mDecimalFormat = new DecimalFormat("00");

    private String mCanShowPreciseTime;
    private int mScrollUnits = SCROLL_UNIT_HOUR + SCROLL_UNIT_MINUTE;

    /**
     * 时间单位：时、分
     */
    private static final int SCROLL_UNIT_HOUR = 0b1;
    private static final int SCROLL_UNIT_MINUTE = 0b10;

    /**
     * 时间单位的最大显示值
     */
    private static final int MAX_MINUTE_UNIT = 59;
    private static final int MAX_HOUR_UNIT = 23;
    private static final int MAX_MONTH_UNIT = 12;

    /**
     * 级联滚动延迟时间
     */
    private static final long LINKAGE_DELAY_DEFAULT = 100L;

    /**
     * 时间选择结果回调接口
     */
    public interface Callback {
        void onTimeSelected(Dialog timeialog, long timestamp);
    }


    /**
     * 通过时间戳初始换时间选择器，毫秒级别
     *
     * @param context        Activity Context
     * @param callback       选择结果回调
     * @param beginTimestamp 毫秒级时间戳
     * @param endTimestamp   毫秒级时间戳
     */
    public CustomDatePicker(Context context, Callback callback, long beginTimestamp, long endTimestamp) {
        if (context == null || callback == null) {
            mCanDialogShow = false;
            return;
        }
        mContext = context;
        mCallback = callback;
        mBeginTime = Calendar.getInstance();
        mBeginTime.setTimeInMillis(beginTimestamp);
        mEndTime = Calendar.getInstance();
        mEndTime.setTimeInMillis(endTimestamp);
        mSelectedTime = Calendar.getInstance();
        initView();
        initData();
        mCanDialogShow = true;
    }

    private void initView() {
        mPickerDialog = new Dialog(mContext, R.style.time_picker_dlg);
        mPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPickerDialog.setContentView(R.layout.time_picker_dlg);
        Window window = mPickerDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        hgTimeTitle = mPickerDialog.findViewById(R.id.hg_time_title);
        hgTimeYear = mPickerDialog.findViewById(R.id.hg_time_year);
        hgTimeMonth = mPickerDialog.findViewById(R.id.hg_time_month);
        hgTimeDay = mPickerDialog.findViewById(R.id.hg_time_day);
        hgTimeHour = mPickerDialog.findViewById(R.id.hg_time_hour);
        hgTimeHourTv = mPickerDialog.findViewById(R.id.hg_time_hour_tv);
        hgTimeMinute = mPickerDialog.findViewById(R.id.hg_time_minute);
        hgTimeMinuteTv = mPickerDialog.findViewById(R.id.hg_time_minute_tv);
        TextView hgTimeConfirm = mPickerDialog.findViewById(R.id.hg_time_confirm);
        TextView hgTimeCancel = mPickerDialog.findViewById(R.id.hg_time_cancel);

        hgTimeConfirm.setOnClickListener(this);
        hgTimeCancel.setOnClickListener(this);

        hgTimeYear.setOnSelectListener(this);
        hgTimeMonth.setOnSelectListener(this);
        hgTimeDay.setOnSelectListener(this);
        hgTimeHour.setOnSelectListener(this);
        hgTimeMinute.setOnSelectListener(this);
        mPickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onDestroy();
            }
        });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.hg_time_cancel) {
            mPickerDialog.dismiss();
        } else if (id == R.id.hg_time_confirm) {
            if (mCallback != null) {
                mCallback.onTimeSelected(mPickerDialog, mSelectedTime.getTimeInMillis());
            }
        }
    }

    @Override
    public void onSelect(View view, String selected) {
        if (view == null || TextUtils.isEmpty(selected)) return;
        int timeUnit;
        try {
            timeUnit = Integer.parseInt(selected);
        } catch (Throwable ignored) {
            return;
        }

        int id = view.getId();
        if (id == R.id.hg_time_year) {
            mSelectedTime.set(Calendar.YEAR, timeUnit);
            linkageMonthUnit(true, LINKAGE_DELAY_DEFAULT);
        } else if (id == R.id.hg_time_month) {
            // 防止类似 2018/12/31 滚动到11月时因溢出变成 2018/12/01
            int lastSelectedMonth = mSelectedTime.get(Calendar.MONTH) + 1;
            mSelectedTime.add(Calendar.MONTH, timeUnit - lastSelectedMonth);
            linkageDayUnit(true, LINKAGE_DELAY_DEFAULT);
        } else if (id == R.id.hg_time_day) {
            mSelectedTime.set(Calendar.DAY_OF_MONTH, timeUnit);
            linkageHourUnit(true, LINKAGE_DELAY_DEFAULT);
        } else if (id == R.id.hg_time_hour) {
            mSelectedTime.set(Calendar.HOUR_OF_DAY, timeUnit);
            linkageMinuteUnit(true);
        } else if (id == R.id.hg_time_minute) {
            mSelectedTime.set(Calendar.MINUTE, timeUnit);
        }
    }

    private void initData() {
        mSelectedTime.setTimeInMillis(mBeginTime.getTimeInMillis());
        mBeginYear = mBeginTime.get(Calendar.YEAR);
        // Calendar.MONTH 值为 0-11
        mBeginMonth = mBeginTime.get(Calendar.MONTH) + 1;
        mBeginDay = mBeginTime.get(Calendar.DAY_OF_MONTH);
        mBeginHour = mBeginTime.get(Calendar.HOUR_OF_DAY);
        mBeginMinute = mBeginTime.get(Calendar.MINUTE);
        mEndYear = mEndTime.get(Calendar.YEAR);
        mEndMonth = mEndTime.get(Calendar.MONTH) + 1;
        mEndDay = mEndTime.get(Calendar.DAY_OF_MONTH);
        mEndHour = mEndTime.get(Calendar.HOUR_OF_DAY);
        mEndMinute = mEndTime.get(Calendar.MINUTE);
        boolean canSpanYear = mBeginYear != mEndYear;
        boolean canSpanMon = !canSpanYear && mBeginMonth != mEndMonth;
        boolean canSpanDay = !canSpanMon && mBeginDay != mEndDay;
        boolean canSpanHour = !canSpanDay && mBeginHour != mEndHour;
        boolean canSpanMinute = !canSpanHour && mBeginMinute != mEndMinute;
        if (canSpanYear) {
            initDateUnits(MAX_MONTH_UNIT, mBeginTime.getActualMaximum(Calendar.DAY_OF_MONTH), MAX_HOUR_UNIT, MAX_MINUTE_UNIT);
        } else if (canSpanMon) {
            initDateUnits(mEndMonth, mBeginTime.getActualMaximum(Calendar.DAY_OF_MONTH), MAX_HOUR_UNIT, MAX_MINUTE_UNIT);
        } else if (canSpanDay) {
            initDateUnits(mEndMonth, mEndDay, MAX_HOUR_UNIT, MAX_MINUTE_UNIT);
        } else if (canSpanHour) {
            initDateUnits(mEndMonth, mEndDay, mEndHour, MAX_MINUTE_UNIT);
        } else if (canSpanMinute) {
            initDateUnits(mEndMonth, mEndDay, mEndHour, mEndMinute);
        }
    }

    private void initDateUnits(int endMonth, int endDay, int endHour, int endMinute) {
        for (int i = mBeginYear; i <= mEndYear; i++) {
            mYearUnits.add(String.valueOf(i));
        }

        for (int i = mBeginMonth; i <= endMonth; i++) {
            mMonthUnits.add(mDecimalFormat.format(i));
        }

        for (int i = mBeginDay; i <= endDay; i++) {
            mDayUnits.add(mDecimalFormat.format(i));
        }

        if ((mScrollUnits & SCROLL_UNIT_HOUR) != SCROLL_UNIT_HOUR) {
            mHourUnits.add(mDecimalFormat.format(mBeginHour));
        } else {
            for (int i = mBeginHour; i <= endHour; i++) {
                mHourUnits.add(mDecimalFormat.format(i));
            }
        }

        if ((mScrollUnits & SCROLL_UNIT_MINUTE) != SCROLL_UNIT_MINUTE) {
            mMinuteUnits.add(mDecimalFormat.format(mBeginMinute));
        } else {
            for (int i = mBeginMinute; i <= endMinute; i++) {
                mMinuteUnits.add(mDecimalFormat.format(i));
            }
        }

        hgTimeYear.setDataList(mYearUnits);
        hgTimeYear.setSelected(0);
        hgTimeMonth.setDataList(mMonthUnits);
        hgTimeMonth.setSelected(0);
        hgTimeDay.setDataList(mDayUnits);
        hgTimeDay.setSelected(0);
        hgTimeHour.setDataList(mHourUnits);
        hgTimeHour.setSelected(0);
        hgTimeMinute.setDataList(mMinuteUnits);
        hgTimeMinute.setSelected(0);

        setCanScroll();
    }

    private void setCanScroll() {
        hgTimeYear.setCanScroll(mYearUnits.size() > 1);
        hgTimeMonth.setCanScroll(mMonthUnits.size() > 1);
        hgTimeDay.setCanScroll(mDayUnits.size() > 1);
        hgTimeHour.setCanScroll(mHourUnits.size() > 1 && (mScrollUnits & SCROLL_UNIT_HOUR) == SCROLL_UNIT_HOUR);
        hgTimeMinute.setCanScroll(mMinuteUnits.size() > 1 && (mScrollUnits & SCROLL_UNIT_MINUTE) == SCROLL_UNIT_MINUTE);
    }

    /**
     * 联动“月”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageMonthUnit(final boolean showAnim, final long delay) {
        int minMonth;
        int maxMonth;
        int selectedYear = mSelectedTime.get(Calendar.YEAR);
        if (mBeginYear == mEndYear) {
            minMonth = mBeginMonth;
            maxMonth = mEndMonth;
        } else if (selectedYear == mBeginYear) {
            minMonth = mBeginMonth;
            maxMonth = MAX_MONTH_UNIT;
        } else if (selectedYear == mEndYear) {
            minMonth = 1;
            maxMonth = mEndMonth;
        } else {
            minMonth = 1;
            maxMonth = MAX_MONTH_UNIT;
        }

        // 重新初始化时间单元容器
        mMonthUnits.clear();
        for (int i = minMonth; i <= maxMonth; i++) {
            mMonthUnits.add(mDecimalFormat.format(i));
        }
        hgTimeMonth.setDataList(mMonthUnits);

        // 确保联动时不会溢出或改变关联选中值
        int selectedMonth = getValueInRange(mSelectedTime.get(Calendar.MONTH) + 1, minMonth, maxMonth);
        mSelectedTime.set(Calendar.MONTH, selectedMonth - 1);
        hgTimeMonth.setSelected(selectedMonth - minMonth);
        if (showAnim) {
            hgTimeMonth.startAnim();
        }
        // 联动“日”变化
        hgTimeMonth.postDelayed(new Runnable() {
            @Override
            public void run() {
                linkageDayUnit(showAnim, delay);
            }
        }, delay);
    }

    /**
     * 联动“日”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageDayUnit(final boolean showAnim, final long delay) {
        int minDay;
        int maxDay;
        int selectedYear = mSelectedTime.get(Calendar.YEAR);
        int selectedMonth = mSelectedTime.get(Calendar.MONTH) + 1;
        if (mBeginYear == mEndYear && mBeginMonth == mEndMonth) {
            minDay = mBeginDay;
            maxDay = mEndDay;
        } else if (selectedYear == mBeginYear && selectedMonth == mBeginMonth) {
            minDay = mBeginDay;
            maxDay = mSelectedTime.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else if (selectedYear == mEndYear && selectedMonth == mEndMonth) {
            minDay = 1;
            maxDay = mEndDay;
        } else {
            minDay = 1;
            maxDay = mSelectedTime.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        mDayUnits.clear();
        for (int i = minDay; i <= maxDay; i++) {
            mDayUnits.add(mDecimalFormat.format(i));
        }
        hgTimeDay.setDataList(mDayUnits);

        int selectedDay = getValueInRange(mSelectedTime.get(Calendar.DAY_OF_MONTH), minDay, maxDay);
        mSelectedTime.set(Calendar.DAY_OF_MONTH, selectedDay);
        hgTimeDay.setSelected(selectedDay - minDay);
        if (showAnim) {
            hgTimeDay.startAnim();
        }

        hgTimeDay.postDelayed(new Runnable() {
            @Override
            public void run() {
                linkageHourUnit(showAnim, delay);
            }
        }, delay);
    }

    /**
     * 联动“时”变化
     *
     * @param showAnim 是否展示滚动动画
     * @param delay    联动下一级延迟时间
     */
    private void linkageHourUnit(final boolean showAnim, final long delay) {
        if ((mScrollUnits & SCROLL_UNIT_HOUR) == SCROLL_UNIT_HOUR) {
            int minHour;
            int maxHour;
            int selectedYear = mSelectedTime.get(Calendar.YEAR);
            int selectedMonth = mSelectedTime.get(Calendar.MONTH) + 1;
            int selectedDay = mSelectedTime.get(Calendar.DAY_OF_MONTH);
            if (mBeginYear == mEndYear && mBeginMonth == mEndMonth && mBeginDay == mEndDay) {
                minHour = mBeginHour;
                maxHour = mEndHour;
            } else if (selectedYear == mBeginYear && selectedMonth == mBeginMonth && selectedDay == mBeginDay) {
                minHour = mBeginHour;
                maxHour = MAX_HOUR_UNIT;
            } else if (selectedYear == mEndYear && selectedMonth == mEndMonth && selectedDay == mEndDay) {
                minHour = 0;
                maxHour = mEndHour;
            } else {
                minHour = 0;
                maxHour = MAX_HOUR_UNIT;
            }

            mHourUnits.clear();
            for (int i = minHour; i <= maxHour; i++) {
                mHourUnits.add(mDecimalFormat.format(i));
            }
            hgTimeHour.setDataList(mHourUnits);

            int selectedHour = getValueInRange(mSelectedTime.get(Calendar.HOUR_OF_DAY), minHour, maxHour);
            mSelectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
            hgTimeHour.setSelected(selectedHour - minHour);
            if (showAnim) {
                hgTimeHour.startAnim();
            }
        }
        hgTimeHour.postDelayed(new Runnable() {
            @Override
            public void run() {
                linkageMinuteUnit(showAnim);
            }
        }, delay);
    }

    /**
     * 联动“分”变化
     *
     * @param showAnim 是否展示滚动动画
     */
    private void linkageMinuteUnit(final boolean showAnim) {
        if ((mScrollUnits & SCROLL_UNIT_MINUTE) == SCROLL_UNIT_MINUTE) {
            int minMinute;
            int maxMinute;
            int selectedYear = mSelectedTime.get(Calendar.YEAR);
            int selectedMonth = mSelectedTime.get(Calendar.MONTH) + 1;
            int selectedDay = mSelectedTime.get(Calendar.DAY_OF_MONTH);
            int selectedHour = mSelectedTime.get(Calendar.HOUR_OF_DAY);
            if (mBeginYear == mEndYear && mBeginMonth == mEndMonth && mBeginDay == mEndDay && mBeginHour == mEndHour) {
                minMinute = mBeginMinute;
                maxMinute = mEndMinute;
            } else if (selectedYear == mBeginYear && selectedMonth == mBeginMonth && selectedDay == mBeginDay && selectedHour == mBeginHour) {
                minMinute = mBeginMinute;
                maxMinute = MAX_MINUTE_UNIT;
            } else if (selectedYear == mEndYear && selectedMonth == mEndMonth && selectedDay == mEndDay && selectedHour == mEndHour) {
                minMinute = 0;
                maxMinute = mEndMinute;
            } else {
                minMinute = 0;
                maxMinute = MAX_MINUTE_UNIT;
            }
            mMinuteUnits.clear();
            for (int i = minMinute; i <= maxMinute; i++) {
                mMinuteUnits.add(mDecimalFormat.format(i));
            }
            hgTimeMinute.setDataList(mMinuteUnits);

            int selectedMinute = getValueInRange(mSelectedTime.get(Calendar.MINUTE), minMinute, maxMinute);
            mSelectedTime.set(Calendar.MINUTE, selectedMinute);
            hgTimeMinute.setSelected(selectedMinute - minMinute);
            if (showAnim) {
                hgTimeMinute.startAnim();
            }
        }
        setCanScroll();
    }

    private int getValueInRange(int value, int minValue, int maxValue) {
        if (value < minValue) {
            return minValue;
        } else return Math.min(value, maxValue);
    }

    private boolean canShow() {
        return mCanDialogShow && mPickerDialog != null;
    }

    /**
     * 设置日期选择器的选中时间
     *
     * @param dateStr 日期字符串
     * @return 是否设置成功
     */
    private boolean setSelectedTime(String dateStr) {
        return canShow() && !TextUtils.isEmpty(dateStr) && setSelectedTime(DateTool.strToLong(dateStr, mCanShowPreciseTime));
    }

    /**
     * 展示时间选择器
     *
     * @param timestamp 时间戳，毫秒级别
     */
    public void show(long timestamp) {
        if (!canShow()) return;
        if (setSelectedTime(timestamp)) {
            mPickerDialog.show();
        }
    }

    /**
     * 展示时间选择器
     *
     * @param dateStr 日期字符串，格式为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm
     */
    void show(String dateStr) {
        if (!canShow() || TextUtils.isEmpty(dateStr)) return;
        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedTime(dateStr)) {
            mPickerDialog.show();
        }
    }

    /**
     * 设置选择器标题
     *
     * @param titleStr
     */
    void setTitle(String titleStr) {
        if (titleStr != null) {
            hgTimeTitle.setText(titleStr);
        }
    }


    /**
     * 设置日期选择器的选中时间
     *
     * @param timestamp 毫秒级时间戳
     * @return 是否设置成功
     */
    private boolean setSelectedTime(long timestamp) {
        if (!canShow()) return false;
        if (timestamp < mBeginTime.getTimeInMillis()) {
            timestamp = mBeginTime.getTimeInMillis();
        } else if (timestamp > mEndTime.getTimeInMillis()) {
            timestamp = mEndTime.getTimeInMillis();
        }
        mSelectedTime.setTimeInMillis(timestamp);

        mYearUnits.clear();
        for (int i = mBeginYear; i <= mEndYear; i++) {
            mYearUnits.add(String.valueOf(i));
        }
        hgTimeYear.setDataList(mYearUnits);
        hgTimeYear.setSelected(mSelectedTime.get(Calendar.YEAR) - mBeginYear);
        linkageMonthUnit(false, 0);
        return true;
    }

    /**
     * 设置是否允许点击屏幕或物理返回键关闭
     */
    void setCancelable(boolean cancelable) {
        if (!canShow()) return;

        mPickerDialog.setCancelable(cancelable);
    }

    /**
     * 设置日期控件是否显示时和分 1 不显示 2显示
     */
    void setCanShowPreciseTime(String canShowPreciseTime) {
        if (!canShow()) return;
        if (canShowPreciseTime.equals("2")) {
            initScrollUnit();
            hgTimeHour.setVisibility(View.VISIBLE);
            hgTimeHourTv.setVisibility(View.VISIBLE);
            hgTimeMinute.setVisibility(View.VISIBLE);
            hgTimeMinuteTv.setVisibility(View.VISIBLE);
        } else if (canShowPreciseTime.equals("1")) {
            initScrollUnit(SCROLL_UNIT_HOUR, SCROLL_UNIT_MINUTE);
            hgTimeHour.setVisibility(View.GONE);
            hgTimeHourTv.setVisibility(View.GONE);
            hgTimeMinute.setVisibility(View.GONE);
            hgTimeMinuteTv.setVisibility(View.GONE);
        }
        mCanShowPreciseTime = canShowPreciseTime;
    }

    private void initScrollUnit(Integer... units) {
        if (units == null || units.length == 0) {
            mScrollUnits = SCROLL_UNIT_HOUR + SCROLL_UNIT_MINUTE;
        } else {
            for (int unit : units) {
                mScrollUnits ^= unit;
            }
        }
    }

    /**
     * 设置日期控件是否可以循环滚动
     */
    void setScrollLoop(boolean canLoop) {
        if (!canShow()) return;

        hgTimeYear.setCanScrollLoop(canLoop);
        hgTimeMonth.setCanScrollLoop(canLoop);
        hgTimeDay.setCanScrollLoop(canLoop);
        hgTimeHour.setCanScrollLoop(canLoop);
        hgTimeMinute.setCanScrollLoop(canLoop);
    }

    /**
     * 设置日期控件是否展示滚动动画
     */
    void setCanShowAnim(boolean canShowAnim) {
        if (!canShow()) return;

        hgTimeYear.setCanShowAnim(canShowAnim);
        hgTimeMonth.setCanShowAnim(canShowAnim);
        hgTimeDay.setCanShowAnim(canShowAnim);
        hgTimeHour.setCanShowAnim(canShowAnim);
        hgTimeMinute.setCanShowAnim(canShowAnim);
    }

    /**
     * 销毁弹窗
     */
    private void onDestroy() {
        if (mPickerDialog != null) {
            mPickerDialog.dismiss();
            mPickerDialog = null;
            hgTimeYear.onDestroy();
            hgTimeMonth.onDestroy();
            hgTimeDay.onDestroy();
            hgTimeHour.onDestroy();
            hgTimeMinute.onDestroy();
        }
    }
}
