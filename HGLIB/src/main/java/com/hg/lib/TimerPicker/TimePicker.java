package com.hg.lib.TimerPicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.hg.lib.tool.DateTool;

public class TimePicker {
    @SuppressLint("StaticFieldLeak")
    private static CustomDatePicker mTimerPicker;

    public final static String START = "开始时间";
    public final static String ENT = "结束时间";
    private static long beginDate = DateTool.strToLong("1989-01-01", "1");
    private static long beginTime = DateTool.strToLong("1989-10-17 18:00", "2");

    /**
     * 选择时间
     *
     * @param context
     * @param textView
     * @param startTime
     * @param endTime
     */
    public static void Time(Context context, final TextView textView, final String titleStr, final String startTime, final String endTime) {
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm

        mTimerPicker = new CustomDatePicker(context, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(Dialog timeialog, long timestamp) {
                switch (titleStr) {
                    case START:
                        if (timestamp < DateTool.strToLong(endTime, "2")) {
                            textView.setText(DateTool.longToStr(timestamp, "2"));
                            timeialog.dismiss();
                        } else {
                            mTimerPicker.setTitle("开始时间不能小于或等于结束时间");
                        }
                        break;
                    case ENT:
                        if (timestamp > DateTool.strToLong(startTime, "2")) {
                            textView.setText(DateTool.longToStr(timestamp, "2"));
                            timeialog.dismiss();
                        } else {
                            mTimerPicker.setTitle("开始时间不能小于或等于结束时间");
                        }
                        break;
                }
            }
        }, beginTime, DateTool.getLongData("2"));
        mTimerPicker.setTitle(titleStr);
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime("2");
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
        if (titleStr.equals(START)) {
            mTimerPicker.show(startTime);
        } else if (titleStr.equals(ENT)) {
            mTimerPicker.show(endTime);
        }
    }

    public static void Time(Context context, final TextView textView, String time) {
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new CustomDatePicker(context, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(Dialog timeialog, long timestamp) {
                textView.setText(DateTool.longToStr(timestamp, "2"));
                timeialog.dismiss();
            }
        }, beginTime, DateTool.getLongData("2"));
        mTimerPicker.setTitle("请选择时间");
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime("2");
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
        mTimerPicker.show(time);
    }


    /**
     * 选择日期
     *
     * @param context
     * @param textView
     * @param startTime
     * @param endTime
     */
    public static void Date(Context context, final TextView textView, final String titleStr, final String startTime, final String endTime) {
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd
        mTimerPicker = new CustomDatePicker(context, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(Dialog timeialog, long timestamp) {
                switch (titleStr) {
                    case START:
                        if (timestamp < DateTool.strToLong(endTime, "1")) {
                            textView.setText(DateTool.longToStr(timestamp, "1"));
                            timeialog.dismiss();
                        } else {
                            mTimerPicker.setTitle("开始时间不能小于或等于结束时间");
                        }
                        break;
                    case ENT:
                        if (timestamp > DateTool.strToLong(startTime, "1")) {
                            textView.setText(DateTool.longToStr(timestamp, "1"));
                            timeialog.dismiss();
                        } else {
                            mTimerPicker.setTitle("开始时间不能小于或等于结束时间");
                        }
                        break;
                }
            }
        }, beginDate, DateTool.getLongData("1"));
        mTimerPicker.setTitle(titleStr);
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime("1");
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
        if (titleStr.equals(START)) {
            mTimerPicker.show(startTime);
        } else if (titleStr.equals(ENT)) {
            mTimerPicker.show(endTime);
        }
    }

    public static void Date(Context context, final TextView textView, String date) {
        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd
        mTimerPicker = new CustomDatePicker(context, new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(Dialog timeialog, long timestamp) {
                textView.setText(DateTool.longToStr(timestamp, "1"));
                timeialog.dismiss();
            }
        }, beginDate, DateTool.getLongData("1"));
        mTimerPicker.setTitle("请选择日期");
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime("1");
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
        mTimerPicker.show(date);
    }
}
