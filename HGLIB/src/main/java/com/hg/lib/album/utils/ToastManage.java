package com.hg.lib.album.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastManage {
    public static void s(Context mContext, String s) {
        Toast.makeText(mContext.getApplicationContext(), s, Toast.LENGTH_LONG) .show();
    }
}
