package com.hg.lib.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.hg.lib.R;
import com.hg.lib.base.LocalMedia;
import com.hg.lib.base.MultimediaListener;
import com.hg.lib.edit.IMGEditBaseActivity;
import com.hg.lib.edit.core.IMGMode;
import com.hg.lib.edit.core.IMGText;
import com.hg.lib.tool.DateTool;
import com.hg.lib.tool.FileTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 拍照
 */
public class CameraActivity extends IMGEditBaseActivity {
    private static MultimediaListener mMultimediaListene;//照片回调
    private OverCameraView mOverCameraView;//聚焦视图
    private CameraPreview preview;//预览视图
    private Camera mCamera;//相机类
    private Handler mHandler = new Handler();//Handle
    private Runnable mRunnable;
    private boolean isFlashing;//是否开启闪光灯
    private byte[] imageData;//图片流暂存
    private String imagePath;
    private boolean isTakePhoto;//拍照标记
    private boolean isFoucing;//是否正在聚焦
    private static final int FRONT = 1;//前置摄像头标记
    private static final int BACK = 2;//后置摄像头标记
    private int currentCameraType = -1;//当前打开的摄像头标记
    private InputMethodManager imm;

    @Override
    public void Init() {
        mCamera = openCamera(BACK);
        preview = new CameraPreview(this, mCamera);
        mOverCameraView = new OverCameraView(this);
        mPreviewLayout.addView(preview);
        mPreviewLayout.addView(mOverCameraView);
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancle_button) {
            finish();
            overridePendingTransition(0, R.anim.album_a3);
        } else if (id == R.id.take_photo_button) {
            if (!isTakePhoto) {
                takePhoto();
            }
        } else if (id == R.id.flash_button) {
            switchFlash();
        } else if (id == R.id.switching_button) {
            try {
                changeCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.rb_doodle) {
            tvCancel.setVisibility(View.VISIBLE);
            onModeClick(IMGMode.DOODLE);
        } else if (id == R.id.btn_text) {
            onTextModeClick();
        } else if (id == R.id.rb_mosaic) {
            tvCancel.setVisibility(View.GONE);
            onModeClick(IMGMode.MOSAIC);
        } else if (id == R.id.btn_clip) {
            tvCancel.setVisibility(View.GONE);
            onModeClick(IMGMode.CLIP);
        } else if (id == R.id.btn_undo) {
            onUndoClick();
        } else if (id == R.id.tv_done) {
            onDoneClick();
        } else if (id == R.id.tv_cancel) {
            onCancelClick();
            cancleSavePhoto();
        } else if (id == R.id.ib_clip_cancel) {
            onCancelClipClick();
        } else if (id == R.id.ib_clip_done) {
            onDoneClipClick();
        } else if (id == R.id.tv_clip_reset) {
            onResetClipClick();
        } else if (id == R.id.ib_clip_rotate) {
            onRotateClipClick();
        }
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
        mModeGroup.clearCheck();
        setOpSubDisplay(OP_HIDE);
        mImgView.undoAll();
    }

    @Override
    public void onDoneClick() {
        String path = FileTool.getEditImagePath();
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
                FileTool.DeleteFile(imagePath);
                List<LocalMedia> mediaList = new ArrayList<>();
                LocalMedia localMedia = new LocalMedia();
                localMedia.setPath(path);
                localMedia.setPictureType("jpeg");
                localMedia.setDescribe(getDescribe());
                mediaList.add(localMedia);
                mMultimediaListene.getData(mediaList);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));//通知图库更新
                finish();
                overridePendingTransition(0, R.anim.album_a3);
                return;
            }
        }
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
    public void onText(IMGText text) {
        mImgView.addStickerText(text);
    }

    /**
     * 启动拍照界面
     *
     * @param activity
     * @param multimediaListene
     */
    public static void startMe(Activity activity, MultimediaListener multimediaListene) {
        mMultimediaListene = multimediaListene;
        activity.startActivity(new Intent(activity, CameraActivity.class));
        activity.overridePendingTransition(R.anim.album_a5, 0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isFoucing) {
                float x = event.getX();
                float y = event.getY();
                isFoucing = true;
                if (mCamera != null && !isTakePhoto) {
                    mOverCameraView.setTouchFoucusRect(mCamera, autoFocusCallback, x, y);
                }
                mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, "自动聚焦超时,请调整合适的位置拍摄！", Toast.LENGTH_LONG).show();
                        isFoucing = false;
                        mOverCameraView.setFoucuing(false);
                        mOverCameraView.disDrawTouchFocusRect();
                    }
                };
                mHandler.postDelayed(mRunnable, 3000);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 注释：自动对焦回调
     */
    private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            isFoucing = false;
            mOverCameraView.setFoucuing(false);
            mOverCameraView.disDrawTouchFocusRect();
            //停止聚焦超时回调
            mHandler.removeCallbacks(mRunnable);
        }
    };

    /**
     * 注释：拍照并保存图片到相册
     */
    private void takePhoto() {
        isTakePhoto = true;
        //调用相机拍照
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        //视图动画
                        switchingButton.setVisibility(View.GONE);
                        mFlashButton.setVisibility(View.GONE);
                        mPhotoLayout.setVisibility(View.GONE);
                        imageData = data;
                        //停止预览
                        mCamera.stopPreview();
                        cameraPreviewRl.setVisibility(View.GONE);
                        imgEditFl.setVisibility(View.VISIBLE);
                        imagePath = savePhoto();
                        Bitmap bitmap = FileTool.getBitmap(CameraActivity.this, Uri.parse("file://" + imagePath));
                        mImgView.setImageBitmap(bitmap);
                    }
                }
        );
    }

    /**
     * 注释：切换闪光灯
     */
    private void switchFlash() {
        isFlashing = !isFlashing;
        mFlashButton.setImageResource(isFlashing ? R.drawable.camera_light_up : R.drawable.camera_light_off);
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(isFlashing ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            Toast.makeText(CameraActivity.this, "该设备不支持闪光灯！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 注释：取消保存 todo
     */
    private void cancleSavePhoto() {
        imge_describe.setText("");
        imm.hideSoftInputFromWindow(imge_describe.getWindowToken(), 0);//关闭键盘
        cameraPreviewRl.setVisibility(View.VISIBLE);
        imgEditFl.setVisibility(View.GONE);
        mPhotoLayout.setVisibility(View.VISIBLE);
        mFlashButton.setVisibility(View.GONE);
        switchingButton.setVisibility(View.GONE);
        FileTool.DeleteFile(imagePath);
        mCamera.startPreview();  //开始预览
        imageData = null;
        imagePath = null;
        isTakePhoto = false;
    }

    /**
     * @return 摄像头是否存在
     */
    private boolean checkCamera() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    @SuppressLint("NewApi")
    private Camera openCamera(int type) {
        if (!checkCamera()) {
            finish();
        } else {
            int frontIndex = -1;
            int backIndex = -1;
            int cameraCount = Camera.getNumberOfCameras();
            Camera.CameraInfo info = new Camera.CameraInfo();
            for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
                Camera.getCameraInfo(cameraIndex, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    frontIndex = cameraIndex;
                } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    backIndex = cameraIndex;
                }
            }
            currentCameraType = type;
            if (type == FRONT && frontIndex != -1) {
                return Camera.open(frontIndex);
            } else if (type == BACK && backIndex != -1) {
                return Camera.open(backIndex);
            }
        }
        return null;
    }

    private void changeCamera() throws IOException {
        mCamera.stopPreview();
        mCamera.release();
        if (currentCameraType == FRONT) {
            mCamera = openCamera(BACK);
        } else if (currentCameraType == BACK) {
            mCamera = openCamera(FRONT);
        }
        assert mCamera != null;
        mCamera.setPreviewDisplay(preview.getHolder());
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    /**
     * 注释：保存图片
     */
    private String savePhoto() {
        FileOutputStream fos = null;
        imagePath = FileTool.getImageCropPath(DateTool.getStrAllDate());
        //保存的图片文件
        File imageFile = new File(imagePath);
        try {
            fos = new FileOutputStream(imageFile);
            fos.write(imageData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imagePath;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);
    }
}
