package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.OnClick;

public class AddHomeOrCompanyDialog extends CommonBaseDialog {

    public static AddHomeOrCompanyDialog getInstance() {
        return new AddHomeOrCompanyDialog();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_add_home_or_company;
    }

    @Override
    protected int obtainWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {

    }

    @OnClick({R.id.ivClose, R.id.tvAddHome, R.id.tvAddCompany})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivClose:
                dismiss();
                break;

            case R.id.tvAddHome:
                if (clickListener != null) {
                    clickListener.onHome();
                }
                break;

            case R.id.tvAddCompany:
                if (clickListener != null) {
                    clickListener.onCompany();
                }
                break;
        }
    }

    private OnClickListener clickListener;

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnClickListener {
        void onHome();

        void onCompany();
    }
}
