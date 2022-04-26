package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.OnClick;

public class AgreementTipDialog extends CommonBaseDialog {

    public static AgreementTipDialog getInstance() {
        AgreementTipDialog agreementTipDialog = new AgreementTipDialog();
        return agreementTipDialog;
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        setCancelable(false);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_agreement_tip;
    }

    @Override
    protected int obtainWidth() {
        return dp2px(300);
    }

    @Override
    protected int obtainHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @OnClick({R.id.tvDisagree, R.id.tvRead})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvDisagree:
                if (onOperateListener != null) {
                    onOperateListener.onDisagree();
                }
                break;

            case R.id.tvRead:
                if (onOperateListener != null) {
                    onOperateListener.onRead();
                }
                break;
        }
    }

    private OnOperateListener onOperateListener;

    public OnOperateListener getOnOperateListener() {
        return onOperateListener;
    }

    public void setOnOperateListener(OnOperateListener onOperateListener) {
        this.onOperateListener = onOperateListener;
    }

    public interface OnOperateListener {
        void onDisagree();

        void onRead();
    }
}
