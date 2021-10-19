package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.OnClick;

public class ScanFailDialog extends CommonBaseDialog {
    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_scan_fail;
    }

    @Override
    protected int obtainWidth() {
        return dp2px(120);
    }

    @Override
    protected int obtainHeight() {
        return dp2px(70);
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        setCancelable(false);
    }

    @OnClick(R.id.ivFailed)
    void onClickClose(){
        if (clickCloseListener!=null){
            clickCloseListener.onClickClose();
        }
    }


    private OnClickCloseListener clickCloseListener;

    public OnClickCloseListener getClickCloseListener() {
        return clickCloseListener;
    }

    public void setClickCloseListener(OnClickCloseListener clickCloseListener) {
        this.clickCloseListener = clickCloseListener;
    }

    public interface OnClickCloseListener {
        void onClickClose();
    }
}
