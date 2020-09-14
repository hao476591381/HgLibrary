package com.hg.lib.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.hg.lib.edit.core.util.IMGUtils;
import com.hg.lib.edit.file.IMGAssetFileDecoder;
import com.hg.lib.edit.file.IMGDecoder;
import com.hg.lib.edit.file.IMGFileDecoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

/**
 * 文件操作工具
 */
public class FileTool {
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 1024;
    /**
     * 图片目录
     *
     * @return
     */
    public static String getImageCropPath(String name) {
        String ImagePath = Environment.getExternalStorageDirectory() + "/YD110/picture/"+name+".jpg";
        File ImageFile = new File(ImagePath);
        if (!Objects.requireNonNull(ImageFile.getParentFile()).exists()) {
            ImageFile.getParentFile().mkdirs();
        }
        return ImagePath;
    }

    /**
     *编辑的图片目录
     *
     * @return
     */
    public static String getEditImagePath() {
        String ImagePath = Environment.getExternalStorageDirectory() + "/YD110/picture/edit/"+ DateTool.getStrAllDate()+".jpg";
        File ImageFile = new File(ImagePath);
        if (!Objects.requireNonNull(ImageFile.getParentFile()).exists()) {
            ImageFile.getParentFile().mkdirs();
        }
        return ImagePath;
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
     * 视频目录
     *
     * @return
     */
   public static String getVideoPath(String videoName) {
        String videoPath = Environment.getExternalStorageDirectory() + "/YD110/video/"+videoName+".mp4";
        File videoFile = new File(videoPath);
        if (!Objects.requireNonNull(videoFile.getParentFile()).exists()) {
            videoFile.getParentFile().mkdirs();
        }
        return videoPath;
    }

    /**
     * 语音目录
     *
     * @return
     */
    public static String getAudioPath() {
        String videoPath = Environment.getExternalStorageDirectory() + "/YD110/audio/";
        File videoFile = new File(videoPath);
        if (!Objects.requireNonNull(videoFile.getParentFile()).exists()) {
            videoFile.getParentFile().mkdirs();
        }
        return videoPath;
    }

    /**
     * apk目录
     *
     * @return
     */
    public static String getApkPath(String apkName) {
        String videoPath = Environment.getExternalStorageDirectory() + "/YD110/apk/" + apkName + ".apk";
        File videoFile = new File(videoPath);
        if (!Objects.requireNonNull(videoFile.getParentFile()).exists()) {
            videoFile.getParentFile().mkdirs();
        }
        return videoPath;
    }

    /**
     * 下载目录
     *
     * @return
     */
    public static String getDownload(String fileName, String fileType) {
        String videoPath = Environment.getExternalStorageDirectory() + "/YD110/download/" + fileName + fileType;
        File videoFile = new File(videoPath);
        if (!Objects.requireNonNull(videoFile.getParentFile()).exists()) {
            videoFile.getParentFile().mkdirs();
        }
        return videoPath;
    }
    /**
     * 下载目录
     *
     * @return
     */
    public static String getDownload(String fileName) {
        String videoPath = Environment.getExternalStorageDirectory() + "/YD110/download/" + fileName ;
        File videoFile = new File(videoPath);
        if (!Objects.requireNonNull(videoFile.getParentFile()).exists()) {
            videoFile.getParentFile().mkdirs();
        }
        return videoPath;
    }
    /**
     * 文件缓存
     *
     * @return
     */
    public static String getFileCache(String fileName) {
        String path = Environment.getExternalStorageDirectory() + "/YD110/FileCache/" + fileName;
        File videoFile = new File(path);
        if (!Objects.requireNonNull(videoFile.getParentFile()).exists()) {
            videoFile.getParentFile().mkdirs();
        }
        return path;
    }

    /**
     * 根据url获取图片缓存
     * Glide 4.x请调用此方法
     * 注意：此方法必须在子线程中进行
     *
     * @param context
     * @param url
     * @return
     */
/*    public static File getCacheFileTo4x(Context context, String url) {
        try {
            return Glide.with(context).downloadOnly().load(url).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /**
     * 根据url获取图片缓存
     * Glide 3.x请调用此方法
     * 注意：此方法必须在子线程中进行
     *
     * @param context
     * @param url
     * @return
     */
    public static File getCacheFileTo3x(Context context, String url) {
        try {
            return Glide.with(context).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存数据
     */
    public static void saveDataToFile(String data, String filePath, boolean bs, String x) {
        try {
            File file = new File(filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file, bs);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取数据
     */
    public static String getDataFromFile(String filePath, String x) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                StringBuilder outString = new StringBuilder();
                byte[] arr = new byte[1024];
                int length;
                while ((length = fileInputStream.read(arr)) != -1) {
                    outString.append(new String(arr, 0, length));
                }
                fileInputStream.close();
                return outString.toString();
            } else {
                System.out.println("getDataFromFile----当前文件不存在");
            }
        } catch (Exception ignored) {
            System.out.println(ignored.getMessage());
        }
        return null;
    }

    /*
     * 删除文件
     */
    public static void DeleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                }
                file.delete();
            } else {
                System.out.println("DeleteFile----当前文件不存在");
            }
            System.out.println("DeleteFile----删除成功");
        } catch (Exception ignored) {
        }
    }

    /**
     * 根据文件地址查找文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean IsFileExis(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static  Bitmap getBitmap(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        IMGDecoder decoder = null;
        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            switch (uri.getScheme()) {
                case "asset":
                    decoder = new IMGAssetFileDecoder(context, uri);
                    break;
                case "file":
                    decoder = new IMGFileDecoder(uri);
                    break;
            }
        }

        if (decoder == null) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;

        decoder.decode(options);

        if (options.outWidth > MAX_WIDTH) {
            options.inSampleSize = IMGUtils.inSampleSize(Math.round(1f * options.outWidth / MAX_WIDTH));
        }

        if (options.outHeight > MAX_HEIGHT) {
            options.inSampleSize = Math.max(options.inSampleSize,
                    IMGUtils.inSampleSize(Math.round(1f * options.outHeight / MAX_HEIGHT)));
        }

        options.inJustDecodeBounds = false;

        Bitmap bitmap = decoder.decode(options);
        if (bitmap == null) {
            return null;
        }
        return bitmap;
    }
}