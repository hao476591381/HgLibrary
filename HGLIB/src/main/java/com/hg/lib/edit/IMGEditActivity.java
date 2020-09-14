package com.hg.lib.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.hg.lib.R;
import com.hg.lib.base.LocalMedia;
import com.hg.lib.base.MultimediaListener;
import com.hg.lib.edit.core.IMGMode;
import com.hg.lib.edit.core.IMGText;
import com.hg.lib.tool.FileTool;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 图片编辑
 */
public class IMGEditActivity extends IMGEditBaseActivity {
    private static MultimediaListener mMultimediaListene;//照片回调

    public static void startMe(Activity activity, String imgPath, MultimediaListener multimediaListene) {
        mMultimediaListene = multimediaListene;
        activity.startActivity(new Intent(activity, IMGEditActivity.class).putExtra(ConFing.EXTRA_IMAGE_URI, Uri.parse("file://" + imgPath))
                .putExtra(ConFing.EXTRA_IMAGE_SAVE_PATH, FileTool.getEditImagePath()));
    }

    @Override
    public void onText(IMGText text) {
        mImgView.addStickerText(text);
    }

    @Override
    public void Init() {
        imgEditFl.setVisibility(View.VISIBLE);
        cameraPreviewRl.setVisibility(View.GONE);
        Uri uri = intent.getParcelableExtra(ConFing.EXTRA_IMAGE_URI);
        Bitmap bitmap = FileTool.getBitmap(this, uri);
        mImgView.setImageBitmap(bitmap);
    }

    @Override
    public void onModeClick(IMGMode mode) {
        IMGMode cm = mImgView.getMode();
        if (cm == mode) {
            mode = IMGMode.NONE;
        }
        mImgView.setMode(mode);
        updateModeUI();

        if (mode == IMGMode.CLIP) {
            setOpDisplay(OP_CLIP);
        }
    }

    @Override
    public void onUndoClick() {
        tvCancel.setVisibility(View.VISIBLE);
        IMGMode mode = mImgView.getMode();
        if (mode == IMGMode.DOODLE) {
            mImgView.undoDoodle();
        } else if (mode == IMGMode.MOSAIC) {
            mImgView.undoMosaic();
        }
    }

    @Override
    public void onCancelClick() {
        finish();
    }

    @Override
    public void onDoneClick() {
        String path = getIntent().getStringExtra(ConFing.EXTRA_IMAGE_SAVE_PATH);
        if (!TextUtils.isEmpty(path)) {
            Bitmap bitmap = mImgView.saveBitmap();
            if (bitmap != null) {
                FileOutputStream fout = null;
                try {
                    fout = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fout);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (fout != null) {
                        try {
                            fout.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                List<LocalMedia> mediaList = new ArrayList<>();
                LocalMedia localMedia = new LocalMedia();
                localMedia.setPath(path);
                localMedia.setPictureType("jpeg");
                localMedia.setDescribe(getDescribe());
                mediaList.add(localMedia);
                mMultimediaListene.getData(mediaList);

              /*  intent.putExtra(ConFing.EXTRA_IMAGE_SAVE_PATH, path);
                intent.putExtra(ConFing.EXTRA_IMAGE_DESCRIBE, getDescribe());
                setResult(RESULT_OK, intent);*/
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));//通知图库更新
                finish();
                overridePendingTransition(0, R.anim.album_a3);
                return;
            }
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onCancelClipClick() {
        tvCancel.setVisibility(View.VISIBLE);
        mImgView.cancelClip();
        setOpDisplay(mImgView.getMode() == IMGMode.CLIP ? OP_CLIP : OP_NORMAL);
    }

    @Override
    public void onDoneClipClick() {
        tvCancel.setVisibility(View.VISIBLE);
        mImgView.doClip();
        setOpDisplay(mImgView.getMode() == IMGMode.CLIP ? OP_CLIP : OP_NORMAL);
    }

    @Override
    public void onResetClipClick() {
        mImgView.resetClip();
    }

    @Override
    public void onRotateClipClick() {
        mImgView.doRotate();
    }

    @Override
    public void onColorChanged(int checkedColor) {
        mImgView.setPenColor(checkedColor);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.rb_doodle) {
            tvCancel.setVisibility(View.VISIBLE);
            onModeClick(IMGMode.DOODLE);
        } else if (vid == R.id.btn_text) {
            onTextModeClick();
        } else if (vid == R.id.rb_mosaic) {
            tvCancel.setVisibility(View.GONE);
            onModeClick(IMGMode.MOSAIC);
        } else if (vid == R.id.btn_clip) {
            tvCancel.setVisibility(View.GONE);
            onModeClick(IMGMode.CLIP);
        } else if (vid == R.id.btn_undo) {
            onUndoClick();
        } else if (vid == R.id.tv_done) {
            onDoneClick();
        } else if (vid == R.id.tv_cancel) {
            onCancelClick();
        } else if (vid == R.id.ib_clip_cancel) {
            onCancelClipClick();
        } else if (vid == R.id.ib_clip_done) {
            onDoneClipClick();
        } else if (vid == R.id.tv_clip_reset) {
            onResetClipClick();
        } else if (vid == R.id.ib_clip_rotate) {
            onRotateClipClick();
        }
    }
}
