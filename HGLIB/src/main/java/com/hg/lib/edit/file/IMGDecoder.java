package com.hg.lib.edit.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;


public abstract class IMGDecoder {

    private Uri uri;

    public IMGDecoder(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Bitmap decode() {
        return decode(null);
    }

    public abstract Bitmap decode(BitmapFactory.Options options);

}
