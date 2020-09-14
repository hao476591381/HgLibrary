package com.hg.lib.file.frg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hg.lib.R;
import com.hg.lib.file.adapter.AllFileAdapter;
import com.hg.lib.file.adapter.OnFileItemClickListener;
import com.hg.lib.file.adapter.OnOpenFileItemClickListener;
import com.hg.lib.file.adapter.OnUpdateDataListener;
import com.hg.lib.file.bean.FileEntity;
import com.hg.lib.file.util.FileSelectFilter;
import com.hg.lib.file.util.OpenFile;
import com.hg.lib.file.util.PickerManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.hg.lib.file.util.FileUtils.getFileListByDirPath;


public class FileAllFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private TextView mEmptyView, tv_back;
    private String mPath;
    private String rootPath;
    private List<FileEntity> mListFiles;
    private FileSelectFilter mFilter;
    private AllFileAdapter mAllFileAdapter;
    private OnUpdateDataListener mOnUpdateDataListener;

    public void setOnUpdateDataListener(OnUpdateDataListener onUpdateDataListener) {
        mOnUpdateDataListener = onUpdateDataListener;
    }

    public static FileAllFragment newInstance() {
        return new FileAllFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_all_frag, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = view.findViewById(R.id.rl_all_file);
        mRecyclerView.setLayoutManager(layoutManager);
        mEmptyView = view.findViewById(R.id.empty_view);
        tv_back = view.findViewById(R.id.tv_back);
    }

    private void initData() {
        getData();
    }

    private void getData() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getContext(), "sd卡不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilter = new FileSelectFilter(PickerManager.getInstance().queryFileTypes);
        mListFiles = getFileList(mPath);
        mAllFileAdapter = new AllFileAdapter(getContext(), mListFiles);
        mRecyclerView.setAdapter(mAllFileAdapter);
    }

    private void initEvent() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath = new File(mPath).getParent();
                if (tempPath == null || mPath.equals(rootPath)) {
                    Toast.makeText(getContext(), "最外层了", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPath = tempPath;
                mListFiles = getFileList(mPath);
                mAllFileAdapter.updateListData(mListFiles);
                mAllFileAdapter.notifyDataSetChanged();
            }
        });
        mAllFileAdapter.setOnFileItemClickListener(new OnFileItemClickListener() {
            @Override
            public void click(int position) {
                //选择文件
                FileEntity entity = mListFiles.get(position);
                File file = entity.getFile();
                ArrayList<FileEntity> files = PickerManager.getInstance().files;
                if (files.contains(entity)) {
                    files.remove(entity);
                    if (mOnUpdateDataListener != null) {
                        mOnUpdateDataListener.update(-file.length());
                    }
                    entity.setSelected(!entity.isSelected());
                    mAllFileAdapter.notifyDataSetChanged();
                } else {
                    if (PickerManager.getInstance().files.size() < PickerManager.getInstance().maxCount) {
                        files.add(entity);
                        if (mOnUpdateDataListener != null) {
                            mOnUpdateDataListener.update(file.length());
                        }
                        entity.setSelected(!entity.isSelected());
                        mAllFileAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.file_select_max, PickerManager.getInstance().maxCount), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mAllFileAdapter.setOnOpenFileItemClickListener(new OnOpenFileItemClickListener() {
            @Override
            public void OpenFileclick(int position) {
                FileEntity entity = mListFiles.get(position);
                //判断是否是文件夹否则打开文件
                if (entity.getFile().isDirectory()) {
                    getIntoChildFolder(position);
                } else {
                    Toast.makeText(getContext(), "打开文件", Toast.LENGTH_SHORT).show();
                    Intent intent = OpenFile.openFile(entity.getPath(), getContext());
                    startActivity(intent);
                }
            }
        });
    }

    //进入子文件夹
    private void getIntoChildFolder(int position) {
        mPath = mListFiles.get(position).getFile().getAbsolutePath();
        //更新数据源
        mListFiles = getFileList(mPath);
        mAllFileAdapter.updateListData(mListFiles);
        mAllFileAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * 根据地址获取当前地址下的所有目录和文件，并且排序
     *
     * @param path
     * @return List<File>
     */
    private List<FileEntity> getFileList(String path) {
        List<FileEntity> fileListByDirPath = getFileListByDirPath(path, mFilter);
        if (fileListByDirPath.size() > 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        return fileListByDirPath;
    }
}
