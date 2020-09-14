package com.hg.lib.album.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hg.lib.R;
import com.hg.lib.album.PictureConfig;
import com.hg.lib.album.utils.OptAnimationLoader;
import com.hg.lib.album.utils.PictureMimeType;
import com.hg.lib.album.utils.StringUtils;
import com.hg.lib.album.utils.ToastManage;
import com.hg.lib.base.LocalMedia;
import com.hg.lib.tool.DateTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureImageGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int DURATION = 450;
    private Context context;
    private OnPhotoSelectChangedListener imageSelectChangedListener;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();
    private boolean is_checked_num = false;
    private Animation animation;
    private boolean zoomAnim = true;

    public PictureImageGridAdapter(Context context) {
        this.context = context;
        animation = OptAnimationLoader.loadAnimation(context, R.anim.album_modal_in);
    }

    public void bindImagesData(List<LocalMedia> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    public void bindSelectImages(List<LocalMedia> images) {
        // 这里重新构构造一个新集合，不然会产生已选集合一变，结果集合也会添加的问题
        List<LocalMedia> selection = new ArrayList<>();
        for (LocalMedia media : images) {
            selection.add(media);
        }
        this.selectImages = selection;
        subSelectPosition();
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
    }

    public List<LocalMedia> getSelectedImages() {
        if (selectImages == null) {
            selectImages = new ArrayList<>();
        }
        return selectImages;
    }

    public List<LocalMedia> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    @Override
    public int getItemViewType(int position) {
        return 2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_image_grid_item, parent, false);
        return new ViewHolder(view);

    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder contentHolder = (ViewHolder) holder;
        final LocalMedia image = images.get(position);
        image.position = contentHolder.getAdapterPosition();
        final String path = image.getPath();
        final String pictureType = image.getPictureType();
        if (is_checked_num) {
            notifyCheckChanged(contentHolder, image);
        }
        selectImage(contentHolder, isSelected(image), false);
        final int mediaMimeType = PictureMimeType.isPictureType(pictureType);
        boolean gif = PictureMimeType.isGif(pictureType);

        contentHolder.tv_isGif.setVisibility(gif ? View.VISIBLE : View.GONE);

        StringUtils.setTextImage(context, contentHolder.tv_duration, R.drawable.album_video_icon, 0);
        contentHolder.tv_duration.setVisibility(mediaMimeType == PictureConfig.TYPE_VIDEO ? View.VISIBLE : View.GONE);

        boolean eqLongImg = PictureMimeType.isLongImg(image);
        contentHolder.tv_long_chart.setVisibility(eqLongImg ? View.VISIBLE : View.GONE);
        long duration = image.getDuration();
        contentHolder.tv_duration.setText(DateTool.timeParse(duration));
        RequestOptions options = new RequestOptions();
        options.sizeMultiplier(0.5f);
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.centerCrop();
        options.placeholder(R.drawable.album_image_placeholder);
        Glide.with(context)
                .asBitmap()
                .load(path)
                .apply(options)
                .into(contentHolder.iv_picture);
        contentHolder.ll_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如原图路径不存在或者路径存在但文件不存在
                if (!new File(path).exists()) {
                    ToastManage.s(context, PictureMimeType.s(context, mediaMimeType));
                    return;
                }
                changeCheckboxState(contentHolder, image);
            }
        });

        contentHolder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如原图路径不存在或者路径存在但文件不存在
                if (!new File(path).exists()) {
                    ToastManage.s(context, PictureMimeType.s(context, mediaMimeType));
                    return;
                }


                imageSelectChangedListener.onPictureClick(image, position);

            }
        });

    }


    @Override
    public int getItemCount() {
        return images.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_picture;
        TextView check;
        TextView tv_duration, tv_isGif, tv_long_chart;
        View contentView;
        LinearLayout ll_check;

        ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            iv_picture = itemView.findViewById(R.id.iv_picture);
            check = itemView.findViewById(R.id.check);
            ll_check = itemView.findViewById(R.id.ll_check);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            tv_isGif = itemView.findViewById(R.id.tv_isGif);
            tv_long_chart = itemView.findViewById(R.id.tv_long_chart);
        }
    }

    private boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 选择按钮更新
     */
    private void notifyCheckChanged(ViewHolder viewHolder, LocalMedia imageBean) {
        viewHolder.check.setText("");
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(imageBean.getPath())) {
                imageBean.setNum(media.getNum());
                media.setPosition(imageBean.getPosition());
                viewHolder.check.setText(String.valueOf(imageBean.getNum()));
            }
        }
    }

    /**
     * 改变图片选中状态
     *
     * @param contentHolder
     * @param image
     */

    private void changeCheckboxState(ViewHolder contentHolder, LocalMedia image) {
        boolean isChecked = contentHolder.check.isSelected();
        String pictureType = selectImages.size() > 0 ? selectImages.get(0).getPictureType() : "";
        if (!TextUtils.isEmpty(pictureType)) {
            boolean toEqual = PictureMimeType.mimeToEqual(pictureType, image.getPictureType());
            if (!toEqual) {
                ToastManage.s(context, "不能同时选择图片或视频");
                return;
            }
        }
        if (selectImages.size() >= PictureConfig.maxSelectNum && !isChecked) {
            boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
            String str = eqImg ? "最多可选择"+PictureConfig.maxSelectNum+"张图片": "最多可选择"+PictureConfig.maxSelectNum+"个视频";
            ToastManage.s(context, str);
            return;
        }

        if (isChecked) {
            for (LocalMedia media : selectImages) {
                if (media.getPath().equals(image.getPath())) {
                    selectImages.remove(media);
                    subSelectPosition();
                    disZoom(contentHolder.iv_picture);
                    break;
                }
            }
        } else {
            selectImages.add(image);
            image.setNum(selectImages.size());
            zoom(contentHolder.iv_picture);
        }
        //通知点击项发生了改变
        notifyItemChanged(contentHolder.getAdapterPosition());
        selectImage(contentHolder, !isChecked, true);
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener.onChange(selectImages);
        }
    }


    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        if (is_checked_num) {
            int size = selectImages.size();
            for (int index = 0; index < size; index++) {
                LocalMedia media = selectImages.get(index);
                media.setNum(index + 1);
                notifyItemChanged(media.position);
            }
        }
    }

    /**
     * 选中的图片并执行动画
     *
     * @param holder
     * @param isChecked
     * @param isAnim
     */
    private void selectImage(ViewHolder holder, boolean isChecked, boolean isAnim) {
        holder.check.setSelected(isChecked);
        if (isChecked) {
            if (isAnim) {
                if (animation != null) {
                    holder.check.startAnimation(animation);
                }
            }
            holder.iv_picture.setColorFilter(ContextCompat.getColor(context, R.color.image_overlay_true), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.iv_picture.setColorFilter(ContextCompat.getColor(context, R.color.image_overlay_false), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public interface OnPhotoSelectChangedListener {

        /**
         * 已选Media回调
         *
         * @param selectImages
         */
        void onChange(List<LocalMedia> selectImages);

        /**
         * 图片预览回调
         *
         * @param media
         * @param position
         */
        void onPictureClick(LocalMedia media, int position);
    }

    public void setOnPhotoSelectChangedListener(OnPhotoSelectChangedListener imageSelectChangedListener) {
        this.imageSelectChangedListener = imageSelectChangedListener;
    }

    private void zoom(ImageView iv_img) {
        if (zoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(ObjectAnimator.ofFloat(iv_img, "scaleX", 1f, 1.12f), ObjectAnimator.ofFloat(iv_img, "scaleY", 1f, 1.12f));
            set.setDuration(DURATION);
            set.start();
        }
    }

    private void disZoom(ImageView iv_img) {
        if (zoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(ObjectAnimator.ofFloat(iv_img, "scaleX", 1.12f, 1f), ObjectAnimator.ofFloat(iv_img, "scaleY", 1.12f, 1f));
            set.setDuration(DURATION);
            set.start();
        }
    }
}
