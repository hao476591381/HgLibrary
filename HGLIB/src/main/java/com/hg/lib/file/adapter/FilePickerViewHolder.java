package com.hg.lib.file.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hg.lib.R;


class FilePickerViewHolder extends RecyclerView.ViewHolder {
    ImageView ivType, ivChoose;
    TextView tvName;
    TextView tvDetail;
    View file_item_v;
    LinearLayout layout_info;

    FilePickerViewHolder(View itemView) {
        super(itemView);
        ivType = itemView.findViewById(R.id.iv_type);
        tvName = itemView.findViewById(R.id.tv_name);
        tvDetail = itemView.findViewById(R.id.tv_detail);
        ivChoose = itemView.findViewById(R.id.iv_choose);
        file_item_v = itemView.findViewById(R.id.file_item_v);
        layout_info= itemView.findViewById(R.id.layout_info);
    }
}
