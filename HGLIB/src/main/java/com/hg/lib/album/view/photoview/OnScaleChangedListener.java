package com.hg.lib.album.view.photoview;


/**
 * 当附加的ImageView缩放变化时调用的回调的接口定义
 */
public interface OnScaleChangedListener {

    /**
     * 当比例变化时的回调
     *
     * @param scaleFactor 比例因子(缩小小于1，放大大于1)
     * @param focusX     焦点X位置
     * @param focusY      焦点Y位置
     */
    void onScaleChange(float scaleFactor, float focusX, float focusY);
}
