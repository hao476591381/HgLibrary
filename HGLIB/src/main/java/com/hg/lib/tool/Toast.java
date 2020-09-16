package com.hg.lib.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hg.lib.R;

public final class Toast {
    public Toast() {
    }

    /**
     * [自定义Toast]
     */
    public void Show(Activity activity, String msg, boolean id) {
        android.widget.Toast toast = new android.widget.Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LayoutInflater inflater = activity.getLayoutInflater();
        LinearLayout toastLayout = (LinearLayout) inflater.inflate(R.layout.toast, null);
        TextView toast_tv = toastLayout.findViewById(R.id.toast_tv);
        ImageView toast_iv = toastLayout.findViewById(R.id.toast_iv);
        if (id) {
            toast_iv.setImageResource(R.drawable.right_icon);
        } else {
            toast_iv.setImageResource(R.drawable.plaint_img);
        }
        toast_tv.setText(msg);
        toast.setView(toastLayout);
        toast.show();
    }
}
