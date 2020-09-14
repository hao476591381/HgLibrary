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


import java.io.File;
import java.util.List;


public class CommonFileAdapter extends RecyclerView.Adapter<FilePickerViewHolder> {
    private Context mContext;
    private List<FileEntity> mData;
    private OnFileItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnFileItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CommonFileAdapter(Context context, List<FileEntity> data) {
        this.mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public FilePickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.file_picker_item, parent, false);
        return new FilePickerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FilePickerViewHolder holder, int position) {
        final FileEntity entity = mData.get(position);
        holder.tvName.setText(entity.getName());
        holder.tvDetail.setText(entity.getMimeType());
        String title = entity.getFileType().getTitle();
        if (entity.isSelected()) {
            holder.ivChoose.setImageResource(R.drawable.album_sel);
        } else {
            holder.ivChoose.setImageResource(R.drawable.file_no_selection);
        }
        if (title.equals("IMG")) {
            Glide.with(mContext).load(new File(entity.getPath())).into(holder.ivType);
        } else {
            holder.ivType.setImageResource(entity.getFileType().getIconStyle());
        }
        holder.ivChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.click(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}
