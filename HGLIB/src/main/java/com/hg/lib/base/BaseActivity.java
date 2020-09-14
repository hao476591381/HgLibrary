package com.hg.lib.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.hg.lib.R;
import com.hg.lib.album.PictureConfig;
import com.hg.lib.album.bean.EventEntity;
import com.hg.lib.album.compress.Luban;
import com.hg.lib.album.compress.OnCompressListener;
import com.hg.lib.album.rxbus2.RxBus;
import com.hg.lib.album.utils.PictureMimeType;
import com.hg.lib.view.HgDiaLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class BaseActivity extends AppCompatActivity {
    public Context context;
    protected HgDiaLog dialog;
    protected HgDiaLog compressDialog;
    protected List<LocalMedia> selectionMedias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        HideActionBar();
        makeStatusBarTransparent(this); //透明状态栏 沉浸效果
    }

    /**
     * 隐藏标题栏
     */
    public void HideActionBar() {
        if (getSupportActionBar()!= null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * 透明状态栏
     *
     * @param activity
     */
    public static void makeStatusBarTransparent(Activity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 压缩加载对话框
     */
    protected void showCompressDialog() {
        if (!isFinishing()) {
            dismissCompressDialog();
            compressDialog =HgDiaLog.LoadDl(this,null,null,false);
            compressDialog.show();
        }
    }

    /**
     * 把压缩对话框
     */
    protected void dismissCompressDialog() {
        try {
            if (!isFinishing() && compressDialog != null && compressDialog.isShowing()) {
                compressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载 dialog
     */
    protected void showPleaseDialog() {
        if (!isFinishing()) {
            dismissDialog();
            dialog =HgDiaLog.LoadDl(this,null,null,false);
            dialog.show();
        }
    }

    /**
     * 关闭加载dialog
     */
  protected void dismissDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩图像
     */
   @SuppressLint("CheckResult")
    protected void compressImage(final List<LocalMedia> result, final MultimediaListener mMultimediaListene) {
        showCompressDialog();
        if (PictureConfig.synOrAsy) {
            Flowable.just(result)
                    .observeOn(Schedulers.io())
                    .map(new Function<List<LocalMedia>, List<File>>() {
                        @Override
                        public List<File> apply(@NonNull List<LocalMedia> list) throws Exception {
                            List<File> files = Luban.with(context)
                                    .setTargetDir(getImageCropPathCache())
                                    .ignoreBy(PictureConfig.minimumCompressSize)
                                    .loadLocalMedia(list).get();
                            if (files == null) {
                                files = new ArrayList<>();
                            }
                            return files;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<File>>() {
                        @Override
                        public void accept(@NonNull List<File> files) {
                            handleCompressCallBack(result, files, mMultimediaListene);
                        }
                    });
        } else {
            Luban.with(this)
                    .loadLocalMedia(result)
                    .ignoreBy(PictureConfig.minimumCompressSize)
                    .setTargetDir(getImageCropPathCache())
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(List<LocalMedia> list) {
                            RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
                            onResult(list, mMultimediaListene);
                        }

                        @Override
                        public void onError(Throwable e) {
                            RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
                            onResult(result, mMultimediaListene);
                        }
                    }).launch();
        }
    }

    /**
     * 重新构造已压缩的图片返回集合
     *
     * @param images
     * @param files
     */
 private void handleCompressCallBack(List<LocalMedia> images, List<File> files, MultimediaListener mMultimediaListene) {
        if (files.size() == images.size()) {
            for (int i = 0, j = images.size(); i < j; i++) {
                // 压缩成功后的地址
                String path = files.get(i).getPath();
                LocalMedia image = images.get(i);
                // 如果是网络图片则不压缩
                boolean http = PictureMimeType.isHttp(path);
                boolean eqTrue = !TextUtils.isEmpty(path) && http;
                image.setCompressed(!eqTrue);
                image.setCompressPath(eqTrue ? "" : path);
            }
        }
        RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
        onResult(images, mMultimediaListene);
    }

    /**
     * 返回图像结果
     *
     * @param images
     */
   protected void onResult(List<LocalMedia> images, MultimediaListener mMultimediaListene) {
        dismissCompressDialog();
        if (selectionMedias != null) {
            images.addAll(selectionMedias);
        }
        mMultimediaListene.getData(images);
        closeActivity();
    }

    /**
     * 图片缓存目录
     *
     * @return
     */
    public static String getImageCropPathCache() {
        String ImagePath = Environment.getExternalStorageDirectory() + "/YD110/picture/Cache/";
        File ImageFile = new File(ImagePath);
        if (!ImageFile.exists()) {
            ImageFile.mkdirs();
        }
        return ImagePath;
    }

    /**
     * 关闭 Activity
     */
    protected void closeActivity() {
        finish();
        overridePendingTransition(0, R.anim.album_a3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissCompressDialog();
        dismissDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        closeActivity();
        return super.onKeyDown(keyCode, event);
    }
}
