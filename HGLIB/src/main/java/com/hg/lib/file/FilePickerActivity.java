package com.hg.lib.file;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hg.lib.R;
import com.hg.lib.base.BaseActivity;
import com.hg.lib.base.LocalMedia;
import com.hg.lib.base.MultimediaListener;
import com.hg.lib.file.adapter.OnUpdateDataListener;
import com.hg.lib.file.bean.FileEntity;
import com.hg.lib.file.frg.FileAllFragment;
import com.hg.lib.file.frg.FileCommonFragment;
import com.hg.lib.file.util.PickerManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.hg.lib.file.util.FileUtils.getReadableFileSize;

/**
 * 文件夹
 */
public class FilePickerActivity extends BaseActivity implements View.OnClickListener, OnUpdateDataListener {
    private static MultimediaListener mMultimediaListene;//文件回调
    private Button btn_common, btn_all;
    private TextView tv_size, tv_confirm;
    private Fragment commonFileFragment, allFileFragment;
    private boolean isConfirm = false;


    /**
     * 打开文件夹
     *
     * @param activity
     * @param multimediaListene
     */
    public static void startMe(Activity activity, MultimediaListener multimediaListene) {
        mMultimediaListene = multimediaListene;
        activity.startActivity(new Intent(activity, FilePickerActivity.class));
        activity.overridePendingTransition(R.anim.album_a5, 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_picker_acty);
        initView();
        initEvent();
        setFragment(1);
    }

    private void initEvent() {
        btn_common.setOnClickListener(this);
        btn_all.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }

    private void initView() {
        btn_common = findViewById(R.id.btn_common);
        btn_all = findViewById(R.id.btn_all);
        tv_size = findViewById(R.id.tv_size);
        tv_confirm = findViewById(R.id.tv_confirm);
    }

    private void setFragment(int type) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hideFragment(fragmentTransaction);
        switch (type) {
            case 1:
                if (commonFileFragment == null) {
                    commonFileFragment = FileCommonFragment.newInstance();
                    ((FileCommonFragment) commonFileFragment).setOnUpdateDataListener(this);
                    fragmentTransaction.add(R.id.fl_content, commonFileFragment);
                } else {
                    fragmentTransaction.show(commonFileFragment);
                }
                break;
            case 2:
                if (allFileFragment == null) {
                    allFileFragment = FileAllFragment.newInstance();
                    ((FileAllFragment) allFileFragment).setOnUpdateDataListener(this);
                    fragmentTransaction.add(R.id.fl_content, allFileFragment);
                } else {
                    fragmentTransaction.show(allFileFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (commonFileFragment != null) {
            transaction.hide(commonFileFragment);
        }
        if (allFileFragment != null) {
            transaction.hide(allFileFragment);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_common) {
            setFragment(1);
            btn_common.setBackgroundResource(R.drawable.file_no_read_pressed);
            btn_common.setTextColor(ContextCompat.getColor(this, R.color.white));
            btn_all.setBackgroundResource(R.drawable.file_already_read);
            btn_all.setTextColor(ContextCompat.getColor(this, R.color.title_color));
        } else if (id == R.id.btn_all) {
            setFragment(2);
            btn_common.setBackgroundResource(R.drawable.file_no_read);
            btn_common.setTextColor(ContextCompat.getColor(this, R.color.title_color));
            btn_all.setBackgroundResource(R.drawable.file_already_read_pressed);
            btn_all.setTextColor(ContextCompat.getColor(this, R.color.white));

        } else if (id == R.id.tv_confirm) {
            isConfirm = true;
            List<LocalMedia> mediaList = new ArrayList<>();
            ArrayList<FileEntity> fileList = PickerManager.getInstance().files;
            LocalMedia localMedia = new LocalMedia();

            for (int i = 0; fileList.size() > i; i++) {
                FileEntity entity = fileList.get(i);
                File file = entity.getFile();
                localMedia.setPath(file.getPath());
                localMedia.setFileSize(getReadableFileSize(file.length()));
                localMedia.setPictureType(entity.getFileType().getTitle());
                localMedia.setFileName(file.getName());
                mediaList.add(localMedia);
            }
            mMultimediaListene.getData(mediaList);
            finish();
            overridePendingTransition(0, R.anim.album_a3);
        }
    }

    private long currentSize;

    @Override
    public void update(long size) {
        currentSize += size;
        tv_size.setText(getString(R.string.already_select, getReadableFileSize(currentSize)));
        String res = "(" + PickerManager.getInstance().files.size() + "/" + PickerManager.getInstance().maxCount + ")";
        tv_confirm.setText(getString(R.string.file_select_res, res));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isConfirm) {
            PickerManager.getInstance().files.clear();
        }
    }
}
