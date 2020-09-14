package com.hg.lib.album.view.photoview;

import android.view.MotionEvent;

/**
 * 一个回调函数，当ImageView被抛出时调用
 * *联系
 */
public interface OnSingleFlingListener {

    /**
     * 接收用户在ImageView上投放的位置的回调。如果，您将收到一个回调
     * 用户将视图中的任意位置抛入。
     *
     * @param e1        MotionEvent用户第一次触摸。
     * @param e2        MotionEvent用户最后一次触摸。
     * @param velocityX 用户水平投掷的距离。
     * @param velocityY 用户垂直抛出的距离。
     */
    boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
}
