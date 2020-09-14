package com.hg.lib.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.hg.lib.R;
import com.hg.lib.tool.FileTool;


public class AudioRecorderButton extends AppCompatButton {
    private static final int STATE_NORMAL = 1;// 默认的状态
    private static final int STATE_RECORDING = 2;// 正在录音
    private static final int STATE_WANT_TO_CANCEL = 3;// 希望取消

    private int mCurrentState = STATE_NORMAL; // 当前的状态
    private boolean isRecording = false;// 已经开始录音

    private DialogManager mDialogManager;
    private AudioManager mAudioManager;

    // 是否触发longClick
    private boolean mReady;
    android.media.AudioManager audioManager;

    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGED = 0x111;
    private static final int MSG_DIALOG_DIMISS = 0x112;
    private static final int MSG_TIME_OUT = 0x113;
    private static final int UPDATE_TIME = 0x114;

    private boolean mThreadFlag = false;
    private int time = 0;
    private float mTime;

    /*
     * 获取音量大小的线程
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    time++;
                    if (isWantToCancel) {
                    } else {
                        if (time % 10 == 0) {
                            mHandler.sendEmptyMessage(UPDATE_TIME);
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                    if (mTime >= 60.0f) {//如果时间超过60秒，自动结束录音
                        while (!mThreadFlag) {//记录已经结束了录音，不需要再次结束，以免出现问题
                            mDialogManager.dimissDialog();
                            mAudioManager.release();
                            if (audioFinishRecorderListener != null) {
                                //发消息给主线程，告诉他reset（）;
                                mHandler.sendEmptyMessage(MSG_TIME_OUT);
                            }
                            mThreadFlag = !mThreadFlag;
                        }
                        isRecording = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    // 显示對話框在开始录音以后
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    // 开启一个线程
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManager.dimissDialog();
                    break;
                case MSG_TIME_OUT://录音超时
                    reset();
                    audioFinishRecorderListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    break;
                case UPDATE_TIME://更新时间
                    if (time % 10 == 0) {
                        mDialogManager.updateTime(time / 10);
                    }
                    break;
            }
        }
    };

    /**
     * 以下2个方法是构造方法
     */
    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(context);
        audioManager = (android.media.AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager = AudioManager.getInstance(FileTool.getAudioPath());
        mAudioManager.setOnAudioStateListener(new AudioManager.AudioStateListener() {
            @Override
            public void wellPrepared() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);//开启线程
            }
        });
        // 由于这个类是button所以在构造方法中添加监听事件
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // if (!isOverDue) {
                mReady = true;
                mAudioManager.prepareAudio();
                // }
                return true;
            }
        });
    }

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener audioFinishRecorderListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        audioFinishRecorderListener = listener;
    }

    android.media.AudioManager.OnAudioFocusChangeListener afChangeListener = new android.media.AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
            } else if (focusChange == android.media.AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
            } else if (focusChange == android.media.AudioManager.AUDIOFOCUS_LOSS) {
                audioManager.abandonAudioFocus(afChangeListener);
                // Stop playback
            }
        }
    };

    public void myRequestAudioFocus() {
        audioManager.requestAudioFocus(afChangeListener, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    /**
     * 屏幕的触摸事件
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mThreadFlag = false;
                changeState(STATE_RECORDING);
                myRequestAudioFocus();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    // 如果想要取消，根据x,y的坐标看是否需要取消
                    if (event.getY() < 0 && Math.abs(event.getY()) > 120) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime <= 1.0f) {//小于1秒
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessage(MSG_DIALOG_DIMISS);//显示对话框
                } else if (mCurrentState == STATE_RECORDING) { // 正在录音的时候，结束
                    mDialogManager.dimissDialog();
                    mAudioManager.release();
                    if (audioFinishRecorderListener != null) {
                        audioFinishRecorderListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }
                } else if (mCurrentState == STATE_WANT_TO_CANCEL) { // 想要取消
                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }
                reset();
                audioManager.abandonAudioFocus(afChangeListener);
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isRecording = false;
        mTime = 0;
        time = 1;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    boolean isWantToCancel = false;

    /**
     * 改变
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.audio_but_normal);
                    setText("按住说话");
                    break;
                case STATE_RECORDING:
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    setBackgroundResource(R.drawable.audio_but);
                    setText("松开结束");
                    isWantToCancel = false;
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.audio_but);
                    setText("松开手指，取消发送");
                    mDialogManager.wantToCancel();
                    isWantToCancel = true;
                    break;
            }
        }
    }
}
