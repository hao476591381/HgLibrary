package com.hg.lib.file.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hg.lib.R;
import com.hg.lib.file.bean.FileEntity;
import com.hg.lib.file.util.FileUtils;


import java.io.File;
import java.util.List;


public class AllFileAdapter extends RecyclerView.Adapter<FilePickerViewHolder> {
    private List<FileEntity> mListData;
    private Context mContext;
    private OnFileItemClickListener onItemClickListener;
    private OnOpenFileItemClickListener onOpenFileItemClickListener;

    public void setOnFileItemClickListener(OnFileItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnOpenFileItemClickListener(OnOpenFileItemClickListener onOpenFileItemClickListener) {
        this.onOpenFileItemClickListener = onOpenFileItemClickListener;
    }

    public AllFileAdapter(Context context, List<FileEntity> listData) {
        mListData = listData;
        mContext = context;
    }

    @NonNull
    @Override
    public FilePickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.file_picker_item, parent, false);
        return new FilePickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FilePickerViewHolder holder, int positon) {
        final FileEntity entity = mListData.get(positon);
        final File file = entity.getFile();
        holder.tvName.setText(file.getName());
        if (file.isDirectory()) {
            holder.ivType.setImageResource(R.drawable.file_picker_folder);
            holder.ivChoose.setVisibility(View.GONE);
            holder.tvDetail.setVisibility(View.GONE);
            holder.file_item_v.setVisibility(View.GONE);
        } else {
            if (entity.getFileType() != null) {
                String title = entity.getFileType().getTitle();
                if (title.equals("IMG")) {
                    Glide.with(mContext).load(new File(entity.getPath())).into(holder.ivType);
                } else {
                    holder.ivType.setImageResource(entity.getFileType().getIconStyle());
                }
            } else {
                holder.ivType.setImageResource(R.drawable.file_picker_def);
            }
            holder.ivChoose.setVisibility(View.VISIBLE);
            holder.tvDetail.setVisibility(View.VISIBLE);
            holder.file_item_v.setVisibility(View.VISIBLE);
            holder.tvDetail.setText(FileUtils.getReadableFileSize(file.length()));
            if (entity.isSelected()) {
                holder.ivChoose.setImageResource(R.drawable.album_sel);
            } else {
                holder.ivChoose.setImageResource(R.drawable.file_no_selection);
            }
        }
        holder.ivChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择文件
                if (onItemClickListener != null) {
                    onItemClickListener.click(holder.getAdapterPosition());
                }
            }
        });

        holder.layout_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开文件
                if (onOpenFileItemClickListener != null) {
                    onOpenFileItemClickListener.OpenFileclick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    /**
     * 更新数据源
     *
     * @param mListData
     */
    public void updateListData(List<FileEntity> mListData) {
        this.mListData = mListData;
    }
}
