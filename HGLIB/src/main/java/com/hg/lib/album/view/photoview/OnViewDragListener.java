package com.hg.lib.album.view.photoview;

/**
 * 当照片遇到拖动事件时要调用的回调的接口定义
 */
public interface OnViewDragListener {

    /**
     *当照片遇到拖放事件时的回调。属性时不能调用
     * 用户扩展。
     *
     * @param dx 坐标在x方向上的变化
     * @param dy 坐标在y方向上的变化
     */
    void onDrag(float dx, float dy);
}
