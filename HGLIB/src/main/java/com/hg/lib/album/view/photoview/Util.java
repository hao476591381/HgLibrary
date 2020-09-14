package com.hg.lib.album.view.photoview;

import android.view.MotionEvent;
import android.widget.ImageView;

class Util {

    static void checkZoomLevels(float minZoom, float midZoom,
                                float maxZoom) {
        if (minZoom >= midZoom) {
            throw new IllegalArgumentException(  "最小变焦必须小于中等变焦。使用更合适的值调用setMinimumZoom()");
        } else if (midZoom >= maxZoom) {
            throw new IllegalArgumentException(  "中等变焦必须小于最大变焦。使用更合适的值调用setMaximumZoom()");
        }
    }

    static boolean hasDrawable(ImageView imageView) {
        return imageView.getDrawable() != null;
    }

    static boolean isSupportedScaleType(final ImageView.ScaleType scaleType) {
        if (scaleType == null) {
            return false;
        }
        switch (scaleType) {
            case MATRIX:
                throw new IllegalStateException("不支持矩阵缩放类型");
        }
        return true;
    }

    static int getPointerIndex(int action) {
        return (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }
}
