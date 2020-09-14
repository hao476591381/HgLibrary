package com.hg.lib.audio;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hg.lib.R;


public class DialogManager {
    private Dialog mDialog;
    private ImageView mIcon;
    private TextView mLable;
    private ImageView mVoice;
    private Context context;

    /**
     * 构造方法 传入上下文
     */
    public DialogManager(Context context) {
        this.context = context;
    }

    // 显示录音的对话框
    public void showRecordingDialog() {
        try {
            mDialog = new Dialog(context, R.style.hg_dialog_1);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.audio_dlg_pop, null);
            mDialog.setContentView(view);
            mIcon = mDialog.findViewById(R.id.dlg_recorder_icon);
            mLable = mDialog.findViewById(R.id.dlg_recorder_label);
            mVoice = mDialog.findViewById(R.id.dlg_recorder_voice);
            mDialog.setCanceledOnTouchOutside(false);//设置点击外部不消失
            mDialog.show();
        } catch (Throwable ignored) {
        }
    }

    public void recording() {
        if (mDialog != null && mDialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.audio_recorder_icon);
            mLable.setText("手指上滑，取消发送");
        }
    }

    // 显示想取消的对话框
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.audio_cancel_icon);
            mLable.setText("松开手指，取消发送");
        }
    }

    public void updateTime(int time) {
        if (mDialog != null && mDialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
        }
    }

    // 显示时间过短的对话框
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.audio_to_short);
            mLable.setText("录音时间过短");
        }
    }

    // 显示取消的对话框
    public void dimissDialog() {
        if (mDialog != null && mDialog.isShowing()) { //显示状态
            mDialog.dismiss();
            mDialog = null;
        }
    }

    // 显示更新音量级别的对话框
    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            //可以再这里根据音量大小，设置图片，实现效果；
            int resId = context.getResources().getIdentifier("audio_v" + level, "drawable", context.getPackageName());
            mVoice.setImageResource(resId);
        }
    }
}
