package com.hg.lib.album;

public class PictureConfig {
    public final static String EXTRA_RESULT_SELECTION = "extra_result_media";
    public final static String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public final static String EXTRA_SELECT_LIST = "selectList";
    public final static String EXTRA_POSITION = "position";
    public final static String EXTRA_BOTTOM_PREVIEW = "bottom_preview";

    public final static int EXTRA_BOTTOM_PREVIEW_0 = 0;//外部图片预览
    public final static int EXTRA_BOTTOM_PREVIEW_1 = 1;//点击相册图片预览
    public final static int EXTRA_BOTTOM_PREVIEW_2 = 2;//点击相册底部预览按钮

    public final static String IMAGE = "image";
    public final static String VIDEO = "video";

    public final static int UPDATE_FLAG = 2774;// 预览界面更新选中数据 标识
    public final static int CLOSE_PREVIEW_FLAG = 2770;// 关闭预览界面 标识
    public final static int PREVIEW_DATA_FLAG = 2771;// 预览界面图片 标识
    public final static int EDIT_DATA_FLAG = 2775;// 编辑界面图片 标识

    public final static int TYPE_ALL = 0;
    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_VIDEO = 2;

    public final static boolean isGif = false;//是否显示gif图片
    public final static int videoMaxSecond = 15;//查询最大视频
    public final static int videoMinSecond = 0;//查询最小视频
    public final static int maxSelectNum = 5;//最大选择数
    public final static boolean isCompress = true;//是否压缩图片
    public final static boolean synOrAsy = true;//是否是异步压缩
    public final static int minimumCompressSize = 100;//小于多少不压缩
    public final static boolean previewEggs = false;//是否显示预览友好体验
    public final static int ALBUM_CHOOSE_REQUEST = 188;
}
