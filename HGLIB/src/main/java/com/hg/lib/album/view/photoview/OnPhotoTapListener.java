package com.hg.lib.album.view.photoview;

import android.widget.ImageView;

/**
 * 当用单个键点击照片时调用的回调
 * 点击
 */
public interface OnPhotoTapListener {

    /**
     * 用于接收用户点击照片的回调。只有在以下情况才会收到回调
     * *用户点击实际的照片，点击“空格”将被忽略。
     *
     * @param view 用户点击了。
     * @param x    用户从哪里抽取的可绘制的，作为百分比可拉的宽度。
     *
     * @param y    用户在哪里点击从顶部绘制的，作为百分比可拉的高度。
     */
    void onPhotoTap(ImageView view, float x, float y);
}
