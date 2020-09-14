package com.hg.lib.album.view.photoview;

import android.widget.ImageView;

/**
 * 当用户在照片外部轻击时回调
 */
public interface OnOutsidePhotoTapListener {

    /**
     *这张照片的外面已经被点击了
     */
    void onOutsidePhotoTap(ImageView imageView);
}
