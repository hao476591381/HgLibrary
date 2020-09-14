package com.hg.lib.album.view.photoview;

import android.view.View;

public interface OnViewTapListener {

    /**
     * 用于接收用户点击ImageView的回调。如果，您将收到一个回调
     * 用户点击视图的任何地方，点击“空格”将不会被忽略。
     *
     * @param view - 查看用户点击的情况。
     * @param x    - 用户从视图的左侧点击此处。
     * @param y    - 用户从视图的顶部点击。
     */
    void onViewTap(View view, float x, float y);
}
