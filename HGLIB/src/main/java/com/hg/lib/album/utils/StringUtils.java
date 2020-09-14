package com.hg.lib.album.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

public class StringUtils {

    public static void tempTextFont(TextView tv) {
        String text = tv.getText().toString().trim();
        String str = "无视频或照片";
        String sumText = str + text;
        Spannable placeSpan = new SpannableString(sumText);
        placeSpan.setSpan(new RelativeSizeSpan(0.8f), str.length(), sumText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(placeSpan);
    }

    /**
     * 给TextView右边设置图片
     *
     * @param resId
     */
    public static void setTextImage(Context context, TextView textView, int resId,int index) {
        Drawable drawable = context.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        if (index == 0) {
            textView.setCompoundDrawables(drawable, null, null, null);
        } else if (index == 1) {
            textView.setCompoundDrawables(null, drawable, null, null);
        } else if (index == 2) {
            textView.setCompoundDrawables(null, null, drawable, null);
        } else {
            textView.setCompoundDrawables(null, null, null, drawable);
        }
    }
}
