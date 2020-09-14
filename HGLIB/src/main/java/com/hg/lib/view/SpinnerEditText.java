package com.hg.lib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;

@SuppressLint("AppCompatCustomView")
public class SpinnerEditText extends EditText {
    String[] list = {"1", "2", "3", "4", "5"};
    ListPopupWindow lpw;

    public SpinnerEditText(Context context) {
        super(context);

    }

    @SuppressLint("ClickableViewAccessibility")
    public SpinnerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        lpw = new ListPopupWindow(context);
        lpw.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list));
        lpw.setAnchorView(this);//设置参照控件
        lpw.setModal(true);//模态框，设置为true响应物理键
        lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = list[i];
                SpinnerEditText.this.setText(item);
                lpw.dismiss();
            }
        });

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // 检查触摸点是否在正确按钮的区域
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getX() >= (SpinnerEditText.this.getWidth() - SpinnerEditText.this.getCompoundDrawables()[2].getBounds().width())) {
                        lpw.show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public SpinnerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        super.setOnTouchListener(l);
    }
}
