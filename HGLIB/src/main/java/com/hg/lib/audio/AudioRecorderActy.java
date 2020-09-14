package com.hg.lib.audio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hg.lib.base.BaseActivity;
import com.hg.lib.R;
import com.hg.lib.tool.DateTool;
import com.hg.lib.tool.FileTool;
import com.hg.lib.view.HgDiaLog;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 录音界面
 */
public class AudioRecorderActy extends BaseActivity implements View.OnClickListener {
    private static LocaleAudioListener mLocaleAudioListener;//照片回调
    private TextView titleBarName;
    private TextView tvTime;
    private ImageView ivVoiceRecorder;
    private TextView tvIsRecorder;
    private int i = 0;//计时
    private Timer timer;
    private AudioManager audioManager;
    private int isRecorder = 0;//0没有录音，1正在录音

    /**
     * 启动拍照界面
     *
     * @param activity
     * @param localeAudioListener
     */
    public static void startMe(Activity activity, LocaleAudioListener localeAudioListener) {
        mLocaleAudioListener = localeAudioListener;
        activity.startActivity(new Intent(activity, AudioRecorderActy.class));
        activity.overridePendingTransition(R.anim.album_a5, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recorder_acty);
        initView();
        init();
    }

    private void initView() {
        titleBarName = findViewById(R.id.title_bar_name);
        tvTime = findViewById(R.id.tv_time);
        ivVoiceRecorder = findViewById(R.id.iv_voiceRecorder);
        ImageView title_bar_return = findViewById(R.id.title_bar_return);
        tvIsRecorder = findViewById(R.id.tv_isRecorder);
        ivVoiceRecorder.setOnClickListener(this);
        title_bar_return.setOnClickListener(this);
    }

    private void init() {
        titleBarName.setText("现场录音");
        tvIsRecorder.setText("录音");
        audioManager = AudioManager.getInstance(FileTool.getAudioPath());
    }


    private void startTask() {
        i = 0;
        timer = new Timer();
        TimerTask task = new Task();
        timer.schedule(task, 1000, 1000);// 开始3秒后执行，之后每隔4秒执行一次
    }

    private void stopTask() {
        if (timer != null) {
            timer.cancel();
        }
    }

    class Task extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    i++;
                    tvTime.setText(DateTool.getTimeFormatByS(i));

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.title_bar_return) {
            finish();
        } else if (id == R.id.iv_voiceRecorder) {
            if (isRecorder == 0) {
                isRecorder = 1;
                tvIsRecorder.setText("完成");
                ivVoiceRecorder.setImageResource(R.drawable.audio_finish_icon);
                audioManager.prepareAudio();
                startTask();
            } else {
                isRecorder = 0;
                tvIsRecorder.setText("录音");
                ivVoiceRecorder.setImageResource(R.drawable.audio_start_icon);
                audioManager.release();
                stopTask();
                HgDiaLog dialog = HgDiaLog.DiaLogUe(AudioRecorderActy.this, "保存", "请输入录音描述！", new HgDiaLog.DlgListener() {
                    @Override
                    public void cancel(HgDiaLog dialog) {
                        audioManager.delete();
                        dialog.dismiss();
                    }

                    @Override
                    public void confirm(HgDiaLog dialog, String str) {
                        if (!str.isEmpty()) {
                            mLocaleAudioListener.getData(new AudioBean(str, DateTool.getSysData("4"), audioManager.getCurrentFilePath(), DateTool.getTimeFormatByS(i)), dialog);
                        } else {
                            Toast.makeText(AudioRecorderActy.this, "录音名称不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTask();
        audioManager.cancel();
    }

    public interface LocaleAudioListener {
        void getData(AudioBean lc, HgDiaLog dialogView);
    }
}
