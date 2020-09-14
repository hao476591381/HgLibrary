package com.hg.lib.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hg.lib.R;


public class HgDiaLog extends Dialog {
    public HgDiaLog(@NonNull Context context) {
        super(context);
    }

    public HgDiaLog(@NonNull Context context, int themeResId, int layout, boolean isCanceled, boolean canceLable) {
        super(context, themeResId);
        Window window = getWindow();
        assert window != null;
        window.setWindowAnimations(R.style.DialogWindowStyle);
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View view = inflater.inflate(layout, null);
        setContentView(view);
        setCancelable(canceLable);
        setCanceledOnTouchOutside(isCanceled);
    }

    public interface DisMissListener {
        void DisMissDiaLog();

    }

    public interface DlgViewListener {
        void GetView(View view);
    }

    public interface DlgListener {
        void cancel(HgDiaLog dialog);

        void confirm(HgDiaLog dialog, String str);
    }

    public interface DlgSelectListener {
        void Select(HgDiaLog dialog, String str);
    }

    /**
     * 加载提示
     *
     * @param context
     * @param msg
     * @param listener
     * @param isCanceled
     * @return
     */
    public static HgDiaLog LoadDl(Context context, String msg, final DisMissListener listener, boolean isCanceled) {
        HgDiaLog dialog = new HgDiaLog(context, R.style.hg_dialog, R.layout.dialog_load, isCanceled, true);
        TextView textView = dialog.findViewById(R.id.hg_dl_str);
        if (msg != null) {
            textView.setText(msg);
        } else {
            textView.setVisibility(View.GONE);
        }
        if (listener != null) {
            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    listener.DisMissDiaLog();
                }
            });
        }
        return dialog;
    }

    /**
     * 带输入框的dialog
     * dialogue
     */
    public static HgDiaLog DiaLogUe(Context context, String confirm, String hint, final DlgListener listener) {
        final HgDiaLog dialog = new HgDiaLog(context, R.style.hg_dialog_1, R.layout.dialogue, true, true);
        final EditText dialogue_et = dialog.findViewById(R.id.dialogue_et);
        TextView dialogue_confirm = dialog.findViewById(R.id.dialogue_confirm);
        TextView dialogue_cancel = dialog.findViewById(R.id.dialogue_cancel);
        dialogue_confirm.setText(confirm);
        dialogue_et.setHint(hint);
        dialogue_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.cancel(dialog);
            }
        });
        dialogue_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = dialogue_et.getText().toString();
                listener.confirm(dialog, str);
            }
        });
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                listener.cancel(dialog);
            }
        });
        return dialog;
    }

    /**
     * 带输入框的dialog
     * dialogue
     */
    public static HgDiaLog DiaLogHint(Context context, String hint, final DlgListener listener) {
        final HgDiaLog dialog = new HgDiaLog(context, R.style.hg_dialog_1, R.layout.dialog_hint, true, true);
        TextView dialog_hint = dialog.findViewById(R.id.dialog_hint);
        TextView dialogue_confirm = dialog.findViewById(R.id.dialogue_confirm);
        TextView dialogue_cancel = dialog.findViewById(R.id.dialogue_cancel);
        dialogue_confirm.setText("确认");
        dialog_hint.setText(hint);
        dialogue_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.cancel(dialog);
            }
        });
        dialogue_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.confirm(dialog, null);
            }
        });

        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                listener.cancel(dialog);
            }
        });
        return dialog;
    }

    /**
     * 选择dialog
     * dialogue
     */
    public static HgDiaLog DiaLogSelect(Context context, final String[] hint, final DlgSelectListener listener) {
        final HgDiaLog dialog = new HgDiaLog(context, R.style.hg_dialog_1, R.layout.dialog_select, true, true);
        TextView dialog_select_1 = dialog.findViewById(R.id.dialog_select_1);
        TextView dialog_select_2 = dialog.findViewById(R.id.dialog_select_2);
        dialog_select_1.setText(hint[0]);
        dialog_select_2.setText(hint[1]);
        dialog_select_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.Select(dialog, hint[0]);
            }
        });
        dialog_select_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.Select(dialog, hint[1]);
            }
        });
        return dialog;
    }


    /**
     * 对话
     *
     * @param context
     * @param id
     * @param listener
     * @return
     */
    public static HgDiaLog DiaLogUe(Context context, int id, DlgViewListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(id, null);
        HgDiaLog dialog = new HgDiaLog(context);
        if (listener != null) {
            listener.GetView(view);
        }
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
