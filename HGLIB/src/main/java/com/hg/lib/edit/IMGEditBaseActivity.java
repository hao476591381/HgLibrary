package com.hg.lib.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.hg.lib.R;
import com.hg.lib.base.BaseActivity;
import com.hg.lib.edit.core.IMGMode;
import com.hg.lib.edit.core.IMGText;
import com.hg.lib.edit.view.IMGColorGroup;
import com.hg.lib.edit.view.IMGTextEditDialog;
import com.hg.lib.edit.view.IMGView;
import com.hg.lib.tool.SoftHideKeyBoardUtil;

public abstract class IMGEditBaseActivity extends BaseActivity implements View.OnClickListener, IMGTextEditDialog.Callback, RadioGroup.OnCheckedChangeListener,
        DialogInterface.OnShowListener, DialogInterface.OnDismissListener {
    public Intent intent;
    /*图片编辑*/
    public FrameLayout imgEditFl;
    protected IMGView mImgView;
    public RadioGroup mModeGroup;
    public IMGColorGroup mColorGroup;
    public IMGTextEditDialog mTextDialog;
    public View mLayoutOpSub;
    public ViewSwitcher mOpSwitcher, mOpSubSwitcher;
    public ImageView tvCancel;
    public static final int OP_HIDE = -1;
    public static final int OP_NORMAL = 0;
    public static final int OP_CLIP = 1;
    public static final int OP_SUB_DOODLE = 0;
    public static final int OP_SUB_MOSAIC = 1;
    /*拍照*/
    public RelativeLayout cameraPreviewRl;
    public FrameLayout mPreviewLayout;
    public RelativeLayout mPhotoLayout;//拍摄按钮视图
    public ImageView mFlashButton;//闪光灯
    public ImageView switchingButton;//切换摄像头
    public ImageView mPhotoButton;//拍照按钮
    public ImageView mCancleButton;//返回按钮
    public EditText imge_describe;//图片描述

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_base_acty);
        SoftHideKeyBoardUtil.assistActivity(this);
        intent = getIntent();
        initView();
        Init();
    }

    public void initView() {
        /*图片编辑*/
        imgEditFl = findViewById(R.id.img_edit_fl);
        mImgView = findViewById(R.id.image_canvas);
        mModeGroup = findViewById(R.id.rg_modes);
        mOpSwitcher = findViewById(R.id.vs_op);
        mOpSubSwitcher = findViewById(R.id.vs_op_sub);
        mColorGroup = findViewById(R.id.cg_colors);
        mLayoutOpSub = findViewById(R.id.layout_op_sub);
        imge_describe = findViewById(R.id.imge_describe);
        tvCancel= findViewById(R.id.tv_cancel);
        mColorGroup.setOnCheckedChangeListener(this);
        /*拍照*/
        cameraPreviewRl = findViewById(R.id.camera_preview_rl);
        mCancleButton = findViewById(R.id.cancle_button);
        mPreviewLayout = findViewById(R.id.camera_preview_layout); // 相机预览
        mPhotoLayout = findViewById(R.id.ll_photo_layout);
        mPhotoButton = findViewById(R.id.take_photo_button);
        switchingButton = findViewById(R.id.switching_button);
        mFlashButton = findViewById(R.id.flash_button);
        mCancleButton.setOnClickListener(this);
        mFlashButton.setOnClickListener(this);
        mPhotoButton.setOnClickListener(this);
        switchingButton.setOnClickListener(this);
        setOpSubDisplay(-1);
    }

    public void setOpSubDisplay(int opSub) {
        if (opSub < 0) {
            mLayoutOpSub.setVisibility(View.GONE);
        } else {
            mOpSubSwitcher.setDisplayedChild(opSub);
            mLayoutOpSub.setVisibility(View.VISIBLE);
        }
    }

    public void updateModeUI() {
        IMGMode mode = mImgView.getMode();
        switch (mode) {
            case DOODLE:
                mModeGroup.check(R.id.rb_doodle);
                setOpSubDisplay(OP_SUB_DOODLE);
                break;
            case MOSAIC:
                mModeGroup.check(R.id.rb_mosaic);
                setOpSubDisplay(OP_SUB_MOSAIC);
                break;
            case NONE:
                mModeGroup.clearCheck();
                setOpSubDisplay(OP_HIDE);
                break;
        }
    }

    public void onTextModeClick() {
        if (mTextDialog == null) {
            mTextDialog = new IMGTextEditDialog(this, this);
            mTextDialog.setOnShowListener(this);
            mTextDialog.setOnDismissListener(this);
        }
        mTextDialog.show();
    }

    @Override
    public final void onCheckedChanged(RadioGroup group, int checkedId) {
        onColorChanged(mColorGroup.getCheckColor());
    }

    public void setOpDisplay(int op) {
        if (op >= 0) {
            mOpSwitcher.setDisplayedChild(op);
        }
    }

    public String getDescribe() {
        return imge_describe.getText().toString();
    }

    public void onShow(DialogInterface dialog) {
        mOpSwitcher.setVisibility(View.GONE);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mOpSwitcher.setVisibility(View.VISIBLE);
    }

    public abstract void Init();

    public abstract void onModeClick(IMGMode mode);

    public abstract void onUndoClick();

    public abstract void onCancelClick();

    public abstract void onDoneClick();

    public abstract void onCancelClipClick();

    public abstract void onDoneClipClick();

    public abstract void onResetClipClick();

    public abstract void onRotateClipClick();

    public abstract void onColorChanged(int checkedColor);

    @Override
    public abstract void onText(IMGText text);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(0, R.anim.album_a3);
    }


}
