package com.hg.lib.treeview.base.adapter;


import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.hg.lib.R;
import com.hg.lib.treeview.TreeBean;
import com.hg.lib.treeview.base.model.TreeNode;
import com.hg.lib.treeview.base.util.DpUtil;

import java.util.List;

public class TreeAdapter extends SingleLayoutTreeAdapter<TreeBean> {

    public TreeAdapter(int layoutResId, @Nullable List<TreeNode<TreeBean>> dataToBind) {
        super(layoutResId, dataToBind);
    }

    @Override
    protected void convert(BaseViewHolder helper, TreeNode<TreeBean> item) {
        super.convert(helper,item);
        helper.setText(R.id.textView, item.getData().getName());
        if (item.isLeaf()) {
          helper.setImageResource(R.id.level_icon, R.drawable.dot_icon);
        } else {
            if (item.isExpand()) {
                helper.setImageResource(R.id.level_icon, R.drawable.tree_icon_collapse);
            } else {
                helper.setImageResource(R.id.level_icon, R.drawable.tree_icon_expand);
            }
        }
    }

    @Override
    protected int getTreeNodeMargin() {
        return  DpUtil.dip2px(this.mContext, 30);
    }
}
