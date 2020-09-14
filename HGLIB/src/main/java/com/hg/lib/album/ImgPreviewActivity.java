package com.hg.lib.album;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;


import com.hg.lib.R;
import com.hg.lib.album.adapter.ImgPreviewAdapter;
import com.hg.lib.album.bean.EventEntity;
import com.hg.lib.album.observable.ImagesObservable;
import com.hg.lib.album.rxbus2.RxBus;
import com.hg.lib.album.rxbus2.Subscribe;
import com.hg.lib.album.rxbus2.ThreadMode;
import com.hg.lib.album.utils.OptAnimationLoader;
import com.hg.lib.album.utils.PictureMimeType;
import com.hg.lib.album.utils.ScreenUtils;
import com.hg.lib.album.utils.ToastManage;
import com.hg.lib.album.view.PreviewViewPager;
import com.hg.lib.base.BaseActivity;
import com.hg.lib.base.LocalMedia;
import com.hg.lib.base.MultimediaListener;
import com.hg.lib.tool.DoubleUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片预览
 */
@SuppressLint("SetTextI18n")
public class ImgPreviewActivity extends BaseActivity implements ImgPreviewAdapter.OnCallBackActivity, View.OnClickListener, Animation.AnimationListener {
    private static MultimediaListener mMultimediaListene;//照片回调
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();
    private int position;
    private boolean refresh;
    private int index;
    private int screenWidth;
    private Animation animation;
    private TextView tv_title, tv_ok, tv_img_num, check;
    private RelativeLayout select_bar_layout, rl_title;
    private View picture_v;
    private PreviewViewPager viewPager;
    private Handler mHandler;

    /**
     * EventBus 3.0 回调
     *
     * @param obj
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        if (obj.what == PictureConfig.CLOSE_PREVIEW_FLAG) {// 压缩完后关闭预览界面
            dismissDialog();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            }, 150);
        }
    }

    /**
     * 预览
     *
     * @param activity
     */
    public static void startMe(Activity activity, List<LocalMedia> selectedImages, List<LocalMedia> medias, int position, int bottom_preview, MultimediaListener multimediaListene) {
        if (!DoubleUtils.isFastDoubleClick()) {
            mMultimediaListene = multimediaListene;
            Bundle bundle = new Bundle();
            bundle.putSerializable(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) medias);
            bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
            bundle.putInt(PictureConfig.EXTRA_BOTTOM_PREVIEW, bottom_preview);
            bundle.putInt(PictureConfig.EXTRA_POSITION, position);
            Intent intent = new Intent();
            intent.setClass(activity, ImgPreviewActivity.class);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, 609);
            activity.overridePendingTransition(R.anim.album_a5, 0);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_img_preview);
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }
        InitView();
        InitData();
        doBusiness();
    }

    private void InitData() {
        int is_bottom_preview = getIntent().getIntExtra(PictureConfig.EXTRA_BOTTOM_PREVIEW, 0);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        selectImages = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_SELECT_LIST);
        if (is_bottom_preview == PictureConfig.EXTRA_BOTTOM_PREVIEW_1) {
            //点击相册图片预览
            select_bar_layout.setVisibility(View.VISIBLE);
            rl_title.setVisibility(View.VISIBLE);
            picture_v.setVisibility(View.VISIBLE);
            images = ImagesObservable.getInstance().readLocalMedias();
        } else if (is_bottom_preview == PictureConfig.EXTRA_BOTTOM_PREVIEW_2) {
            /* 点击底部预览按钮过来 */
            select_bar_layout.setVisibility(View.VISIBLE);
            rl_title.setVisibility(View.VISIBLE);
            picture_v.setVisibility(View.VISIBLE);
            images = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        } else {
            //外部图片预览
            select_bar_layout.setVisibility(View.GONE);
            rl_title.setVisibility(View.GONE);
            picture_v.setVisibility(View.GONE);
            images = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        }
        animation = OptAnimationLoader.loadAnimation(this, R.anim.album_modal_in);
        animation.setAnimationListener(this);
        mHandler = new Handler();
        screenWidth = ScreenUtils.getScreenWidth(this);
    }

    private void InitView() {
        select_bar_layout = findViewById(R.id.select_bar_layout);
        rl_title = findViewById(R.id.rl_title);
        tv_title = findViewById(R.id.picture_title);
        viewPager = findViewById(R.id.preview_pager);
        tv_ok = findViewById(R.id.tv_ok);
        tv_img_num = findViewById(R.id.tv_img_num);
        check = findViewById(R.id.check);
        picture_v=findViewById(R.id.picture_v);
        LinearLayout ll_check = findViewById(R.id.ll_check);
        ImageView picture_left_back = findViewById(R.id.picture_left_back);
        //绑定监听
        tv_ok.setOnClickListener(this);
        ll_check.setOnClickListener(this);
        picture_left_back.setOnClickListener(this);
    }

    private void doBusiness() {
        tv_ok.setText("请选择");
        tv_img_num.setSelected(false);
        initViewPageAdapterData();
        //viewPager监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isPreviewEggs(position, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                tv_title.setText(position + 1 + "/" + images.size());
                LocalMedia media = images.get(position);
                index = media.getPosition();
                if (!PictureConfig.previewEggs) {
                    onImageChecked(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 初始化ViewPage数据
     */
    private void initViewPageAdapterData() {
        tv_title.setText(position + 1 + "/" + images.size());
        ImgPreviewAdapter adapter = new ImgPreviewAdapter(images, this, this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        onSelectNumChange(false);
        onImageChecked(position);
        if (images.size() > 0) {
            LocalMedia media = images.get(position);
            index = media.getPosition();
        }
    }

    /**
     * 这里没实际意义，好处是预览图片时 滑动到屏幕一半以上可看到下一张图片是否选中了
     *
     * @param positionOffsetPixels 滑动偏移量
     */
    private void isPreviewEggs(int position, int positionOffsetPixels) {
        if (PictureConfig.previewEggs) {
            if (images.size() > 0) {
                LocalMedia media;
                if (positionOffsetPixels < screenWidth / 2) {
                    media = images.get(position);
                    check.setSelected(isSelected(media));
                } else {
                    media = images.get(position + 1);
                    check.setSelected(isSelected(media));
                }
            }
        }
    }

    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        for (int index = 0, len = selectImages.size(); index < len; index++) {
            LocalMedia media = selectImages.get(index);
            media.setNum(index + 1);
        }
    }

    /**
     * 判断当前图片是否选中
     *
     * @param position
     */
    public void onImageChecked(int position) {
        if (images != null && images.size() > 0) {
            LocalMedia media = images.get(position);
            check.setSelected(isSelected(media));
        } else {
            check.setSelected(false);
        }
    }

    /**
     * 当前图片是否选中
     *
     * @param image
     * @return
     */
    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新图片选择数量
     */

    public void onSelectNumChange(boolean isRefresh) {
        this.refresh = isRefresh;
        boolean enable = selectImages.size() != 0;
        if (enable) {
            tv_ok.setSelected(true);
            tv_ok.setEnabled(true);
            if (refresh) {
                tv_img_num.startAnimation(animation);
            }
            tv_img_num.setVisibility(View.VISIBLE);
            tv_img_num.setText(String.valueOf(selectImages.size()));
            tv_ok.setText("发送");

        } else {
            tv_ok.setEnabled(false);
            tv_ok.setSelected(false);
            tv_img_num.setVisibility(View.INVISIBLE);
            tv_ok.setText("请选择");

        }
        updateSelector(refresh);
    }

    /**
     * 更新图片列表选中效果
     *
     * @param isRefresh
     */
    private void updateSelector(boolean isRefresh) {
        if (isRefresh) {
            EventEntity obj = new EventEntity(PictureConfig.UPDATE_FLAG, selectImages, index);
            RxBus.getDefault().post(obj);
        }
    }

    @Override
    public void onActivityBackPressed() {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_left_back) {//返回
            onBackPressed();
        } else if (id == R.id.tv_ok) {//发送 todo
            onResult(selectImages, mMultimediaListene);
        } else if (id == R.id.ll_check) {//选择图片
            SelectImg();
        }
    }

    @Override
    protected void onResult(List<LocalMedia> images, MultimediaListener multimediaListener) {
        RxBus.getDefault().post(new EventEntity(PictureConfig.PREVIEW_DATA_FLAG, images));
        // 如果开启了压缩，先不关闭此页面，PictureImageGridActivity压缩完在通知关闭
        String pictureType = images != null ? images.get(0).getPictureType() : "";
        boolean isVideo = pictureType.startsWith(PictureConfig.IMAGE);
        if (PictureConfig.isCompress && isVideo) {
            showPleaseDialog();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        updateSelector(refresh);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    /**
     * 选择图片
     */
    private void SelectImg() {
        if (images != null && images.size() > 0) {
            LocalMedia image = images.get(viewPager.getCurrentItem());
            String pictureType = selectImages.size() > 0 ? selectImages.get(0).getPictureType() : "";
            if (!TextUtils.isEmpty(pictureType)) {
                boolean toEqual = PictureMimeType.mimeToEqual(pictureType, image.getPictureType());
                if (!toEqual) {
                    ToastManage.s(context, "不能同时选择图片或视频");
                    return;
                }
            }
            // 刷新图片列表中图片状态
            boolean isChecked;
            if (!check.isSelected()) {
                isChecked = true;
                check.setSelected(true);
                check.startAnimation(animation);
            } else {
                isChecked = false;
                check.setSelected(false);
            }
            if (selectImages.size() >= PictureConfig.maxSelectNum && isChecked) {
                ToastManage.s(context, "你最多可以选择" + PictureConfig.maxSelectNum + "张图片");
                check.setSelected(false);
                return;
            }
            if (isChecked) {
                // 如果是单选，则清空已选中的并刷新列表(作单一选择)
                selectImages.add(image);
                image.setNum(selectImages.size());
            } else {
                for (LocalMedia media : selectImages) {
                    if (media.getPath().equals(image.getPath())) {
                        selectImages.remove(media);
                        subSelectPosition();
                        break;
                    }
                }
            }
            onSelectNumChange(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }
}
