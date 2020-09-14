package com.hg.lib.treeview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hg.lib.R;
import com.hg.lib.treeview.base.adapter.TreeAdapter;
import com.hg.lib.treeview.base.model.TreeNode;
import com.hg.lib.treeview.base.util.QueryUtils;
import com.hg.lib.treeview.base.util.TreeDataUtils;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class TreeView extends PopupWindow {
    private LayoutInflater inflater;
    private List<TreeBean> mlist;
    private EditText spiner_search_et;
    private RecyclerView spiner_recycler_view;
    private boolean isSearchEt;
    private boolean isDismiss;
    private static TreeView myPopUpView;

    private TreeView(Context context, List<TreeBean> list, TreeClick treeClick, boolean isSearchEt, boolean isDismiss) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.mlist = list;
        this.isSearchEt = isSearchEt;
        this.isDismiss = isDismiss;
        init(treeClick, context);
    }

    @SuppressLint({"InflateParams", "CutPasteId"})
    private void init(final TreeClick treeClick, final Context context) {
        View view = inflater.inflate(R.layout.tree_layout, null);
        setContentView(view);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        spiner_recycler_view = view.findViewById(R.id.spiner_recycler_view);
        spiner_search_et = view.findViewById(R.id.spiner_search_et);
        spiner_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        TreeShow(mlist, treeClick);
        if (isSearchEt) {
            spiner_search_et.setVisibility(View.VISIBLE);
        } else {
            spiner_search_et.setVisibility(View.GONE);
        }

        spiner_search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String msgstr = s.toString().trim();
                spiner_search_et.setSelection(s.toString().length());
                if (TextUtils.isEmpty(msgstr)) {
                    TreeShow(mlist, treeClick);
                } else {
                    try {
                        List<TreeBean> qlist = QueryUtils.fuzzyQuery(msgstr, mlist);
                        TreeShow(qlist, treeClick);
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }


    //显示下拉列表
    private void TreeShow(List<TreeBean> dataBeans, final TreeClick spinerClick) {
        try {
            List<TreeNode<TreeBean>> dataToBind = new ArrayList<>();
            dataToBind.clear();
            dataToBind.addAll(TreeDataUtils.convertDataToTreeNode(dataBeans));
            TreeAdapter adapter = new TreeAdapter(R.layout.tree_view_level, dataToBind);
            spiner_recycler_view.setAdapter(adapter);
            adapter.setOnTreeClickedListener(new TreeAdapter.OnTreeClickedListener<TreeBean>() {
                @Override
                public void onNodeClicked(View view, TreeNode<TreeBean> node, int position) {
                    ImageView icon = view.findViewById(R.id.level_icon);
                    if (node.isExpand()) {
                        icon.setImageResource(R.drawable.tree_icon_collapse);
                    } else {
                        icon.setImageResource(R.drawable.tree_icon_expand);
                    }
                }

                @Override
                public void onLeafClicked(View view, TreeNode<TreeBean> node, int position) {
                    spinerClick.getData(node.getData().getId(), node.getData().getName());
                    if (isDismiss) {
                        if (myPopUpView != null) {
                            myPopUpView.dismiss();
                        }
                    }
                }
            });
        } catch (Throwable ignored) {
        }
    }

    public static void Show(final Context context, final TextView textView, List<TreeBean> list, TreeClick treeClick, boolean isSearchEt, boolean isDismiss) {
        myPopUpView = new TreeView(context, list, treeClick, isSearchEt, isDismiss);
        myPopUpView.setWidth(textView.getWidth());
        myPopUpView.showAsDropDown(textView);
        setTextImage(textView, R.drawable.jiantou_shang_icon, context);
        myPopUpView.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setTextImage(textView, R.drawable.jiantou_xia_icon, context);
            }
        });
    }

    public static void Show(final Context context, LinearLayout linearLayout, final TextView textView, List<TreeBean> list, TreeClick treeClick, boolean isSearchEt, boolean isDismiss) {
        myPopUpView = new TreeView(context, list, treeClick, isSearchEt, isDismiss);
        myPopUpView.setWidth(linearLayout.getWidth());
        myPopUpView.showAsDropDown(linearLayout);
        setTextImage(textView, R.drawable.jiantou_shang_icon, context);
        myPopUpView.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setTextImage(textView, R.drawable.jiantou_xia_icon, context);
            }
        });
    }

    /**
     * 给TextView右边设置图片
     *
     * @param resId
     */
    private static void setTextImage(TextView view, int resId, Context context) {
        Drawable drawable = context.getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 必须设置图片大小，否则不显示
        view.setCompoundDrawables(null, null, drawable, null);
    }

    //下拉选择回调
    public interface TreeClick {
        void getData(String id, String name);
    }
}
