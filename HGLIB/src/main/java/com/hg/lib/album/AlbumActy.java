package com.hg.lib.album;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.hg.lib.R;
import com.hg.lib.album.adapter.PictureAlbumDirectoryAdapter;
import com.hg.lib.album.adapter.PictureImageGridAdapter;
import com.hg.lib.album.bean.EventEntity;
import com.hg.lib.album.bean.LocalMediaFolder;
import com.hg.lib.album.bean.LocalMediaLoader;
import com.hg.lib.album.observable.ImagesObservable;
import com.hg.lib.album.rxbus2.RxBus;
import com.hg.lib.album.rxbus2.Subscribe;
import com.hg.lib.album.rxbus2.ThreadMode;
import com.hg.lib.album.utils.PictureMimeType;
import com.hg.lib.album.utils.ScreenUtils;
import com.hg.lib.album.utils.StringUtils;
import com.hg.lib.album.view.FolderPopWindow;
import com.hg.lib.album.view.GridSpacingItemDecoration;
import com.hg.lib.base.BaseActivity;
import com.hg.lib.base.LocalMedia;
import com.hg.lib.base.MultimediaListener;
import com.hg.lib.edit.IMGEditActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 相册
 */
public class AlbumActy extends BaseActivity implements PictureImageGridAdapter.OnPhotoSelectChangedListener
        , View.OnClickListener, PictureAlbumDirectoryAdapter.OnItemClickListener {
    private static MultimediaListener mMultimediaListene;//照片回调
    private static int mType;//显示类型
    private RecyclerView picture_recycler;
    private RelativeLayout rl_picture_title;
    private TextView picture_title, picture_id_preview, picture_tv_ok, picture_tv_img_num, tv_empty, picture_tv_edit;
    private FolderPopWindow folderWindow;
    private LocalMediaLoader mediaLoader;
    private PictureImageGridAdapter adapter;
    private Animation animation = null;
    private boolean anim = false;
    private List<LocalMedia> images = new ArrayList<>();
    private static final int SHOW_DIALOG = 0;
    private static final int DISMISS_DIALOG = 1;

    /**
     * EventBus 3.0 回调
     *
     * @param obj
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        switch (obj.what) {
            case PictureConfig.UPDATE_FLAG:
                // 预览时勾选图片更新回调
                List<LocalMedia> selectImages = obj.medias;
                anim = selectImages.size() > 0;
                int position = obj.position;
                Log.i("刷新下标:", String.valueOf(position));
                adapter.bindSelectImages(selectImages);
                adapter.notifyItemChanged(position);
                break;
            case PictureConfig.PREVIEW_DATA_FLAG:
                List<LocalMedia> medias = obj.medias;
                if (medias.size() > 0) {
                    // 取出第1个判断是否是图片，视频和图片只能二选一，不必考虑图片和视频混合
                    String pictureType = medias.get(0).getPictureType();
                    if (PictureConfig.isCompress && pictureType.startsWith(PictureConfig.IMAGE)) {
                        compressImage(medias, mMultimediaListene);
                    } else {
                        onResult(medias, mMultimediaListene);
                    }
                }
                break;
        }
    }

    /**
     * 打开相册
     *
     * @param activity
     * @param multimediaListene
     */
    public static void startMe(Activity activity, int type, MultimediaListener multimediaListene) {
        mMultimediaListene = multimediaListene;
        mType = type;
        activity.startActivity(new Intent(activity, AlbumActy.class));
        activity.overridePendingTransition(R.anim.album_a5, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_acty);
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }
        InitView();
        picture_recycler.addItemDecoration(new GridSpacingItemDecoration(4, ScreenUtils.dip2px(this, 2), false));
        picture_recycler.setLayoutManager(new GridLayoutManager(this, 4));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) Objects.requireNonNull(picture_recycler.getItemAnimator())).setSupportsChangeAnimations(false);
        mediaLoader = new LocalMediaLoader(this, mType, PictureConfig.isGif, PictureConfig.videoMaxSecond, PictureConfig.videoMinSecond);
        adapter = new PictureImageGridAdapter(this);
        adapter.setOnPhotoSelectChangedListener(this);
        adapter.bindSelectImages(selectionMedias);
        picture_recycler.setAdapter(adapter);
        mHandler.sendEmptyMessage(SHOW_DIALOG);
        picture_tv_ok.setText("请选择");
        animation = AnimationUtils.loadAnimation(this, R.anim.album_modal_in);
        StringUtils.tempTextFont(tv_empty);
        readLocalMedia();
    }

    private void InitView() {
        ImageView picture_left_back = findViewById(R.id.picture_left_back);
        picture_title = findViewById(R.id.picture_title);
        rl_picture_title = findViewById(R.id.rl_picture_title);
        picture_id_preview = findViewById(R.id.picture_id_preview);
        picture_recycler = findViewById(R.id.picture_recycler);
        picture_tv_ok = findViewById(R.id.picture_tv_ok);
        picture_tv_img_num = findViewById(R.id.picture_tv_img_num);
        tv_empty = findViewById(R.id.tv_empty);
        picture_tv_edit = findViewById(R.id.picture_tv_edit);
        folderWindow = new FolderPopWindow(this);
        folderWindow.setPictureTitleView(picture_title);
        picture_id_preview.setVisibility(View.VISIBLE);
        //绑定控件监听
        folderWindow.setOnItemClickListener(this);
        picture_left_back.setOnClickListener(this);
        picture_title.setOnClickListener(this);
        picture_id_preview.setOnClickListener(this);
        picture_tv_ok.setOnClickListener(this);
        picture_tv_edit.setOnClickListener(this);
    }

    @Override
    public void onChange(List<LocalMedia> selectImages) {
        changeImageNumber(selectImages);
    }

    @Override
    public void onPictureClick(LocalMedia media, int position) {
        List<LocalMedia> images = adapter.getImages();
        startPreview(images, position);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();//返回
        if (id == R.id.picture_left_back) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                closeActivity();
            }
        } else if (id == R.id.picture_title) {//类型下拉列表
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                if (images != null && images.size() > 0) {
                    folderWindow.showAsDropDown(rl_picture_title);
                    List<LocalMedia> selectedImages = adapter.getSelectedImages();
                    folderWindow.notifyDataCheckedStatus(selectedImages);
                }
            }
        } else if (id == R.id.picture_id_preview) {//预览
            List<LocalMedia> selectedImages = adapter.getSelectedImages();
            List<LocalMedia> medias = new ArrayList<>(selectedImages);
            ImgPreviewActivity.startMe(this, selectedImages, medias, 0, PictureConfig.EXTRA_BOTTOM_PREVIEW_2, mMultimediaListene);
        } else if (id == R.id.picture_tv_ok) {//发送图片
            List<LocalMedia> images = adapter.getSelectedImages();
            String pictureType = images.get(0).getPictureType();
            //是否压缩图片，视频压缩
            if (PictureConfig.isCompress && pictureType.startsWith(PictureConfig.IMAGE)) {
                compressImage(images, mMultimediaListene);
            } else {
                onResult(images, mMultimediaListene);
            }
        } else if (id == R.id.picture_tv_edit) {//编辑
            List<LocalMedia> imageList = adapter.getSelectedImages();
           // IMGEditUtils.ImgEdit(this, imageList.get(0).getPath(), PictureConfig.EDIT_DATA_FLAG);
            IMGEditActivity.startMe(this, imageList.get(0).getPath(), new MultimediaListener() {
                @Override
                public void getData(List<LocalMedia> mediaList) {
                    onResult(mediaList, mMultimediaListene);
                }
            });
        }
    }

    @Override
    public void onItemClick(String folderName, List<LocalMedia> images) {
        picture_title.setText(folderName);
        adapter.bindImagesData(images);
        folderWindow.dismiss();
    }

    /**
     * 改变图像选择器状态
     *
     * @param selectImages
     */
    public void changeImageNumber(List<LocalMedia> selectImages) {
        // 如果选择的视频没有预览功能
        String pictureType = selectImages.size() > 0 ? selectImages.get(0).getPictureType() : "";
        boolean isVideo = PictureMimeType.isVideo(pictureType);
        if (isVideo) {
            picture_id_preview.setVisibility(View.GONE);
            picture_tv_edit.setVisibility(View.GONE);
        } else {
            picture_id_preview.setVisibility(View.VISIBLE);
            picture_tv_edit.setVisibility(View.VISIBLE);
        }
        boolean enable = selectImages.size() != 0;
        if (enable) {
            picture_tv_ok.setEnabled(true);
            picture_id_preview.setEnabled(true);
            picture_id_preview.setSelected(true);
            picture_tv_ok.setSelected(true);
            if (!anim) {
                picture_tv_img_num.startAnimation(animation);
            }
            picture_tv_img_num.setVisibility(View.VISIBLE);
            picture_tv_img_num.setText(String.valueOf(selectImages.size()));
            picture_tv_ok.setText("发送");
            anim = false;

        } else {
            picture_tv_ok.setEnabled(false);
            picture_id_preview.setEnabled(false);
            picture_id_preview.setSelected(false);
            picture_tv_ok.setSelected(false);
            picture_tv_img_num.setVisibility(View.INVISIBLE);
            picture_tv_ok.setText("请选择");
        }
        boolean isEdit = selectImages.size() == 1;
        picture_tv_edit.setEnabled(isEdit);
        picture_tv_edit.setSelected(isEdit);
    }

    /**
     * 预览图像和视频
     *
     * @param previewImages
     * @param position
     */
    public void startPreview(List<LocalMedia> previewImages, int position) {
        LocalMedia media = previewImages.get(position);
        String pictureType = media.getPictureType();
        int mediaType = PictureMimeType.isPictureType(pictureType);
        switch (mediaType) {
            case PictureConfig.TYPE_IMAGE:
                // 图片
                List<LocalMedia> selectedImages = adapter.getSelectedImages();
                ImagesObservable.getInstance().saveLocalMedia(previewImages);
                ImgPreviewActivity.startMe(this, selectedImages, null, position, PictureConfig.EXTRA_BOTTOM_PREVIEW_1, mMultimediaListene);
                break;
            case PictureConfig.TYPE_VIDEO:
                // 视频
                VideoPlayActivity.startMe(this, media.getPath());
                break;
        }
    }

    /**
     * 得到LocalMedia
     */
    protected void readLocalMedia() {
        mediaLoader.loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                if (folders.size() > 0) {
                    LocalMediaFolder folder = folders.get(0);
                    folder.setChecked(true);
                    List<LocalMedia> localImg = folder.getImages();
                    // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                    // 因为onActivityResult里手动添加拍照后的照片，
                    // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                    if (localImg.size() >= images.size()) {
                        images = localImg;
                        folderWindow.bindFolder(folders);
                    }
                }
                if (adapter != null) {
                    if (images == null) {
                        images = new ArrayList<>();
                    }
                    adapter.bindImagesData(images);
                    tv_empty.setVisibility(images.size() > 0 ? View.INVISIBLE : View.VISIBLE);
                }
                mHandler.sendEmptyMessage(DISMISS_DIALOG);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DIALOG:
                    showPleaseDialog();
                    break;
                case DISMISS_DIALOG:
                    dismissDialog();
                    break;
            }
        }
    };


  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.EDIT_DATA_FLAG) {
                if (data != null) {
                    String path = data.getStringExtra(ConFing.EXTRA_IMAGE_SAVE_PATH);
                    String describe = data.getStringExtra(ConFing.EXTRA_IMAGE_DESCRIBE);
                    List<LocalMedia> medias = new ArrayList<>();
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setPath(path);
                    localMedia.setDescribe(describe);
                    medias.add(localMedia);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));//通知图库更新
                    onResult(medias, mMultimediaListene);
                }
            }
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
        ImagesObservable.getInstance().clearLocalMedia();
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }
}
