package com.hg.lib;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.hg.lib.album.AlbumActy;
import com.hg.lib.album.ImgPreviewActivity;
import com.hg.lib.album.PictureConfig;
import com.hg.lib.album.VideoPlayActivity;
import com.hg.lib.audio.AudioPlayDialog;
import com.hg.lib.audio.AudioRecorderActy;
import com.hg.lib.base.LocalMedia;
import com.hg.lib.base.MultimediaListener;
import com.hg.lib.camera.CameraActivity;
import com.hg.lib.file.FilePickerActivity;
import com.hg.lib.file.util.OpenFile;
import com.hg.lib.tool.FileTool;
import com.hg.lib.view.CompletedView;
import com.hg.lib.view.HgDiaLog;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HgLib {
    private static HgDiaLog dialogView;
    private static CompletedView completedView;
    private static Callback.Cancelable http;

    /**
     * 打开录音界面
     *
     * @param activity
     * @param localeAudioListener
     */
    public static void Audio(Activity activity, AudioRecorderActy.LocaleAudioListener localeAudioListener) {
        AudioRecorderActy.startMe(activity, localeAudioListener);
    }

    /**
     * 打开文件
     *
     * @param activity
     * @param multimediaListener
     */
    public static void File(Activity activity, MultimediaListener multimediaListener) {
        FilePickerActivity.startMe(activity, multimediaListener);
    }

    /**
     * 拍照
     *
     * @param activity
     * @param multimediaListener
     */
    public static void Camera(Activity activity, MultimediaListener multimediaListener) {
        CameraActivity.startMe(activity, multimediaListener);
    }

    /**
     * 相册(显示所有)
     *
     * @param activity
     * @param multimediaListener
     */
    public static void Album(Activity activity, MultimediaListener multimediaListener) {
        AlbumActy.startMe(activity, PictureConfig.TYPE_ALL, multimediaListener);
    }

    /**
     * 相册(只显示图片)
     *
     * @param activity
     * @param multimediaListener
     */
    public static void ImagAlbum(Activity activity, MultimediaListener multimediaListener) {
        AlbumActy.startMe(activity, PictureConfig.TYPE_IMAGE, multimediaListener);
    }

    /**
     * 预览文件
     *
     * @param activity
     */
    public static void previewFile(final Activity activity, final String path, String fileName) {
        ShowLoadDlg(activity, path, fileName, new PreviewCallBack() {
            @Override
            public void onSuccess(String filePath) {
                Intent intent = OpenFile.openFile(path, activity.getApplicationContext());
                activity.startActivity(intent);
            }
        });
    }
    /**
     * 预览图片
     *
     * @param activity
     */
    public static void previewImg(final Activity activity, final String path, String fileName) {
        ShowLoadDlg(activity, path, fileName, new PreviewCallBack() {
            @Override
            public void onSuccess(String filePath) {
                LocalMedia localMedia = new LocalMedia();
                List<LocalMedia> localMediaList = new ArrayList<>();
                localMedia.setPath(filePath);
                localMediaList.add(localMedia);
                ImgPreviewActivity.startMe(activity, localMediaList, localMediaList, 0, PictureConfig.EXTRA_BOTTOM_PREVIEW_0, null);
            }
        });
    }

    /**
     * 预览视频
     *
     * @param activity
     */
    public static void previewVideo(final Activity activity, final String path, String fileName) {
        ShowLoadDlg(activity, path, fileName, new PreviewCallBack() {
            @Override
            public void onSuccess(String filePath) {
                VideoPlayActivity.startMe(activity, filePath);
            }
        });
    }

    /**
     * 录音播放
     *
     * @param activity
     */
    public static void palyAudio(final Activity activity, final String path, String fileName) {
        ShowLoadDlg(activity, path, fileName, new PreviewCallBack() {
            @Override
            public void onSuccess(String filePath) {
                new AudioPlayDialog(activity,filePath).show();
            }
        });
    }

    private static void ShowLoadDlg(Activity activity, final String path, String fileName, final PreviewCallBack fileCallBack) {
        String urlHttp = path.substring(0, 4);
        if (urlHttp.equals("http")) {
            final String downloadPath = FileTool.getDownload(fileName);
            boolean IsFileExis = FileTool.IsFileExis(downloadPath);
            if (IsFileExis) {
                fileCallBack.onSuccess(downloadPath);
            } else {
                dialogView = HgDiaLog.DiaLogUe(activity, R.layout.presenter_load_dlg, new HgDiaLog.DlgViewListener() {
                    @Override
                    public void GetView(View view) {
                        completedView = view.findViewById(R.id.presenter_progress);
                        DownloadFileRequest(path, downloadPath, fileCallBack);
                    }
                });
                dialogView.show();
                dialogView.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (http != null) {
                            http.cancel();
                        }
                    }
                });
            }
        } else {
            fileCallBack.onSuccess(path);
        }
    }

    private static void DownloadFileRequest(String url, String name, final PreviewCallBack fileCallBack) {
        // 文件下载
        RequestParams requestParams = new RequestParams(url);
        // 文件下载后的保存路径及文件名
        requestParams.setSaveFilePath(FileTool.getDownload(name));
        // 下载完成后自动为文件命名
        requestParams.setAutoRename(false);

        http = x.http().get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                fileCallBack.onSuccess(result.getPath());
                DismissDlg();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                completedView.update(100 * 360 / 100, "加载失败", 60);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long arg0, long arg1, boolean isDownloading) {
                int progress = (int) (arg1 * 100 / arg0);
                completedView.update(progress * 360 / 100, progress + "%", 14);
            }
        });
    }

    private static void DismissDlg() {
        if (dialogView != null) {
            dialogView.dismiss();
        }
    }

    private interface PreviewCallBack {
        void onSuccess(String filePath);
    }
}
