package com.hg.lib.audio;


import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hg.lib.R;
import com.hg.lib.tool.DateTool;




public class AudioPlayDialog extends Dialog implements View.OnClickListener {
    public Context context;
    private String audio_path;
    private MediaPlayer mediaPlayer;
    private SeekBar musicSeekBar;
    private boolean isPlayAudio = false;
    private TextView tv_PlayPause;
    private TextView tv_musicStatus;
    private TextView tv_musicTotal;
    private TextView tv_musicTime;

    public AudioPlayDialog(Context context, String audio_path) {
        super(context, R.style.audio_dialog);
        this.context = context;
        this.audio_path = audio_path;
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_play_dialog);
        tv_musicStatus = findViewById(R.id.tv_musicStatus);
        tv_musicTime = findViewById(R.id.tv_musicTime);
        musicSeekBar = findViewById(R.id.musicSeekBar);
        tv_musicTotal = findViewById(R.id.tv_musicTotal);
        tv_PlayPause = findViewById(R.id.tv_PlayPause);
        TextView tv_Stop = findViewById(R.id.tv_Stop);
        TextView tv_Quit = findViewById(R.id.tv_Quit);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initPlayer(audio_path);
            }
        }, 30);
        tv_PlayPause.setOnClickListener(this);
        tv_Stop.setOnClickListener(this);
        tv_Quit.setOnClickListener(this);
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * 初始化音频播放组件
     *
     * @param path
     */
    private void initPlayer(String path) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    tv_PlayPause.setText("播放");
                    stop(audio_path);
                }
            });
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(false);
            playAudio();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音频
     */
    private void playAudio() {
        if (mediaPlayer != null) {
            musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            musicSeekBar.setMax(mediaPlayer.getDuration());
        }
        String ppStr = tv_PlayPause.getText().toString();
        if (ppStr.equals(context.getString(R.string.play_audio))) {
            tv_PlayPause.setText(context.getString(R.string.pause_audio));
            tv_musicStatus.setText(context.getString(R.string.play_audio));
            playOrPause();
        } else {
            tv_PlayPause.setText(context.getString(R.string.play_audio));
            tv_musicStatus.setText(context.getString(R.string.pause_audio));
            playOrPause();
        }
        if (!isPlayAudio) {
            handler.post(runnable);
            isPlayAudio = true;
        }
    }

    /**
     * 暂停播放
     */
    private void playOrPause() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放
     *
     * @param path
     */
    private void stop(String path) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //  通过 Handler 更新 UI 上的组件状态
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null) {
                    tv_musicTime.setText(DateTool.timeParse(mediaPlayer.getCurrentPosition()));
                    musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    musicSeekBar.setMax(mediaPlayer.getDuration());
                    tv_musicTotal.setText(DateTool.timeParse(mediaPlayer.getDuration()));
                    handler.postDelayed(runnable, 200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_PlayPause) {
            playAudio();
        }
        if (i == R.id.tv_Stop) {
            tv_musicStatus.setText(context.getString(R.string.stop_audio));
            tv_PlayPause.setText(context.getString(R.string.play_audio));
            stop(audio_path);
        }
        if (i == R.id.tv_Quit) {
            handler.removeCallbacks(runnable);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop(audio_path);
                }
            }, 30);
            try {
                AudioPlayDialog.this.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AudioPlayDialog.this.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null && handler != null) {
            handler.removeCallbacks(runnable);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}