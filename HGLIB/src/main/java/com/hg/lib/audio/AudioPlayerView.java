package com.hg.lib.audio;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.hg.lib.R;
import com.hg.lib.tool.FileTool;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

public class AudioPlayerView extends View {
    private String MSG_TO = "0";//收到的消息

    public AudioPlayerView(Context context) {
        super(context);
    }

    public AudioPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setTag(Object tag) {
        super.setTag(tag);
    }

    @Override
    public Object getTag() {
        return super.getTag();
    }

    public void PlayAudio(String path, String isComMeg) {
        MediaManager.PLAY_BS = true;
        setAnim(isComMeg);
        String audioUrl = path.substring(0, 4);
        if (audioUrl.equals("http")) {
            download(path, isComMeg);
        } else {
            playAudio(path, isComMeg);
        }
    }

    private void setAnim(String isComMeg) {
        // 播放动画
        if (isComMeg.equals(MSG_TO)) {
            this.setBackgroundResource(R.drawable.msg_audio_play_js);
        } else {
            this.setBackgroundResource(R.drawable.msg_audio_play_fs);
        }
        AnimationDrawable anim = (AnimationDrawable) this.getBackground();
        anim.start();
    }


    private void download(String path, final String isComMeg) {
        String pathHttp = path.substring(0, 4);
        if (pathHttp.equals("http")) {
            RequestParams requestParams = new RequestParams(path);
            // 文件下载后的保存路径及文件名
            requestParams.setSaveFilePath(FileTool.getAudioPath());
            // 下载完成后自动为文件命名
            requestParams.setAutoRename(false);
            x.http().get(requestParams, new Callback.CommonCallback<File>() {
                @Override
                public void onCancelled(CancelledException arg0) {
                }

                @Override
                public void onError(Throwable arg0, boolean arg1) {
                    // 下载失败取消动画
                    if (isComMeg.equals(MSG_TO)) {
                        AudioPlayerView.this.setBackgroundResource(R.drawable.msg_audio_js_icon);
                    } else {
                        AudioPlayerView.this.setBackgroundResource(R.drawable.msg_audio_fs_icon);
                    }
                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onSuccess(File arg0) {
                    String audioPath = arg0.getPath();
                    playAudio(audioPath, isComMeg);
                }
            });
        }
    }

    /**
     * 播放语音
     *
     * @param audioPath
     * @param isComMeg
     */
    private void playAudio(final String audioPath, final String isComMeg) {
        MediaManager.playSound(audioPath, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 播放结束取消动画
                if (isComMeg.equals(MSG_TO)) {
                    AudioPlayerView.this.setBackgroundResource(R.drawable.msg_audio_js_icon);
                } else {
                    AudioPlayerView.this.setBackgroundResource(R.drawable.msg_audio_fs_icon);
                }

            }
        });
    }
}