package com.hg.lib.file.util;




import com.hg.lib.R;
import com.hg.lib.file.bean.FileEntity;
import com.hg.lib.file.bean.FileType;

import java.util.ArrayList;


public class PickerManager {
    public static PickerManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final PickerManager INSTANCE = new PickerManager();
    }

    /**
     * 最多能选的文件的个数
     */
    public int maxCount = 3;
    /**
     * 保存结果
     */
    public ArrayList<FileEntity> files;
    /**
     * 筛选条件 类型
     */
    public ArrayList<FileType> mFileTypes;

    public String[] queryFileTypes={"doc", "docx", "dot", "dotx","pdf","ppt", "pptx","xls", "xlt", "xlsx", "xltx","txt","apk","dat"};

    /**
     * 文件夹筛选
     * 这里包括 微信和QQ中的下载的文件和图片
     */
    public String[] mFilterFolder = new String[]{"MicroMsg/Download", "WeiXin", "QQfile_recv", "MobileQQ/photo"};

    private PickerManager() {
        files = new ArrayList<>();
        mFileTypes = new ArrayList<>();
        addDocTypes();
    }

    public void addDocTypes() {
        String[] pdfs = {"pdf"};
        mFileTypes.add(new FileType("PDF", pdfs, R.drawable.file_picker_pdf));

        String[] docs = {"doc", "docx", "dot", "dotx"};
        mFileTypes.add(new FileType("DOC", docs, R.drawable.file_picker_word));
        String[] ppts = {"ppt", "pptx"};
        mFileTypes.add(new FileType("PPT", ppts, R.drawable.file_picker_ppt));

        String[] xlss = {"xls", "xlt", "xlsx", "xltx"};
        mFileTypes.add(new FileType("XLS", xlss, R.drawable.file_picker_excle));

        String[] txts = {"txt"};
        mFileTypes.add(new FileType("TXT", txts, R.drawable.file_picker_txt));

        String[] apk = {"apk"};
        mFileTypes.add(new FileType("APK", apk, R.drawable.file_picker_apk));

        String[] imgs = {"png", "jpg", "jpeg", "gif"};
        mFileTypes.add(new FileType("IMG", imgs, 0));
    }

    public ArrayList<FileType> getFileTypes() {
        return mFileTypes;
    }


    public PickerManager setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }
}
