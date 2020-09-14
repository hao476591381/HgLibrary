package com.hg.lib.file.frg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hg.lib.R;
import com.hg.lib.file.adapter.CommonFileAdapter;
import com.hg.lib.file.adapter.OnFileItemClickListener;
import com.hg.lib.file.adapter.OnUpdateDataListener;
import com.hg.lib.file.bean.FileEntity;
import com.hg.lib.file.util.FileScannerTask;
import com.hg.lib.file.util.PickerManager;

import java.util.ArrayList;
import java.util.List;


public class FileCommonFragment extends Fragment implements FileScannerTask.FileScannerListener {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private ProgressBar mProgressBar;
    private CommonFileAdapter mCommonFileAdapter;
    private OnUpdateDataListener mOnUpdateDataListener;

    public void setOnUpdateDataListener(OnUpdateDataListener onUpdateDataListener) {
        mOnUpdateDataListener = onUpdateDataListener;
    }

    public static FileCommonFragment newInstance() {
        return new FileCommonFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_normal_frag, null);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = view.findViewById(R.id.rl_normal_file);
        mRecyclerView.setLayoutManager(layoutManager);
        mEmptyView = view.findViewById(R.id.empty_view);
        mProgressBar = view.findViewById(R.id.progress);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void initData() {
        new FileScannerTask(getContext(), FileCommonFragment.this).execute();
    }

    private void iniEvent(final List<FileEntity> entities) {
        mCommonFileAdapter.setOnItemClickListener(new OnFileItemClickListener() {
            @Override
            public void click(int position) {
                FileEntity entity = entities.get(position);
                ArrayList<FileEntity> files = PickerManager.getInstance().files;
                if (files.contains(entity)) {
                    files.remove(entity);
                    if (mOnUpdateDataListener != null) {
                        mOnUpdateDataListener.update(-Long.parseLong(entity.getSize()));
                    }
                    entity.setSelected(!entity.isSelected());
                    mCommonFileAdapter.notifyDataSetChanged();
                } else {
                    if (PickerManager.getInstance().files.size() < PickerManager.getInstance().maxCount) {
                        files.add(entity);
                        if (mOnUpdateDataListener != null) {
                            mOnUpdateDataListener.update(Long.parseLong(entity.getSize()));
                        }
                        entity.setSelected(!entity.isSelected());
                        mCommonFileAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.file_select_max, PickerManager.getInstance().maxCount), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void scannerResult(List<FileEntity> entities) {
        mProgressBar.setVisibility(View.GONE);
        if (entities.size() > 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        mCommonFileAdapter = new CommonFileAdapter(getContext(), entities);
        mRecyclerView.setAdapter(mCommonFileAdapter);
        iniEvent(entities);
    }
}
