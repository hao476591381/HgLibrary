package com.hg.lib.audio;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 播放管理类
 *
 * @author hnjc
 */
public class MediaManager {
    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;
    public static boolean PLAY_BS = true;// 录音播放控制

    public static void playSound(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        if ( PLAY_BS ) {
            if ( mMediaPlayer == null ) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        mMediaPlayer.reset();
                        return false;
                    }
                });
            } else {
                mMediaPlayer.reset();
            }
            try {
                mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnCompletionListener(onCompletionListener);
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void pause() {
        if ( mMediaPlayer != null && mMediaPlayer.isPlaying() ) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static void resume() {
        if ( mMediaPlayer != null && isPause ) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public static void release() {
        PLAY_BS = false;
        if ( mMediaPlayer != null ) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
