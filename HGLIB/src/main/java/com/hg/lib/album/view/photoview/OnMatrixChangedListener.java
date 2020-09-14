package com.hg.lib.album.view.photoview;

import android.graphics.RectF;

/**
 * 当内部矩阵更改时调用的回调函数的接口定义
 * 这一观点。
 */
public interface OnMatrixChangedListener {

    /**
     * 当显示可绘制图形的矩阵发生变化时的回调。这可能是因为
     * 视图的边界已经改变，或者用户已经缩放。
     *
     * @param rect -显示可绘制对象的新边界的矩形。
     */
    void onMatrixChanged(RectF rect);
}
