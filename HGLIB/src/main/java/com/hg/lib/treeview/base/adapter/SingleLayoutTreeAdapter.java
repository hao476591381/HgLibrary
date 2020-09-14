package com.hg.lib.treeview.base.adapter;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hg.lib.treeview.base.model.NodeId;
import com.hg.lib.treeview.base.model.TreeNode;
import com.hg.lib.treeview.base.util.DpUtil;
import com.hg.lib.treeview.base.util.TreeDataUtils;

import java.util.List;


public class SingleLayoutTreeAdapter<T extends NodeId> extends BaseQuickAdapter<TreeNode<T>, BaseViewHolder> {

    public interface OnTreeClickedListener<T extends NodeId> {

        void onNodeClicked(View view, TreeNode<T> node, int position);

        void onLeafClicked(View view, TreeNode<T> node, int position);
    }

    private OnTreeClickedListener onTreeClickedListener;

    public SingleLayoutTreeAdapter(int layoutResId, @Nullable final List<TreeNode<T>> dataToBind) {
        super(layoutResId, dataToBind);
        //点击展开
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TreeNode<T> node = dataToBind.get(position);
                if (!node.isLeaf()) {
                    List<TreeNode<T>> children = TreeDataUtils.getNodeChildren(node);
                    if (node.isExpand()) {
                        dataToBind.removeAll(children);
                        node.setExpand(false);
                        notifyItemRangeRemoved(position + 1, children.size());
                    } else {
                        dataToBind.addAll(position + 1, children);
                        node.setExpand(true);
                        notifyItemRangeInserted(position + 1, children.size());
                    }

                    if (onTreeClickedListener != null) {
                        onTreeClickedListener.onNodeClicked(view, node, position);
                    }
                } else {
                    if (onTreeClickedListener != null) {
                        onTreeClickedListener.onLeafClicked(view, node, position);
                    }
                }

            }
        });
        //长按选择
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                TreeNode<T> node = dataToBind.get(position);
                if (onTreeClickedListener != null) {
                    onTreeClickedListener.onLeafClicked(view, node, position);
                }
                return true;
            }
        });
    }

    @Override
    protected void convert(BaseViewHolder helper, TreeNode<T> item) {
        int level = item.getLevel();
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) helper.itemView.getLayoutParams();
        layoutParams.leftMargin = getTreeNodeMargin() * level;
    }

    public void setOnTreeClickedListener(OnTreeClickedListener onTreeClickedListener) {
        this.onTreeClickedListener = onTreeClickedListener;

    }

    protected int getTreeNodeMargin() {
        return DpUtil.dip2px(this.mContext, 10);
    }
}
