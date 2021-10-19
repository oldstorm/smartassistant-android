package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;
import butterknife.OnClick;

public class CenterAlertDialog extends CommonBaseDialog {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvTip)
    TextView tvTip;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.rbConfirm)
    ProgressBar rbConfirm;

    private String title;
    private String tip;
    private String cancelStr;
    private String confirmStr;
    private boolean showLoading;

    public static CenterAlertDialog newInstance(String title, String tip, String cancelStr, String confirmStr) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tip", tip);
        args.putString("cancelStr", cancelStr);
        args.putString("confirmStr", confirmStr);

        CenterAlertDialog fragment = new CenterAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static CenterAlertDialog newInstance(String title, String tip) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tip", tip);
        CenterAlertDialog fragment = new CenterAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static CenterAlertDialog newInstance(String title, String tip, boolean showLoading){
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("tip", tip);
        args.putBoolean("showLoading", showLoading);
        CenterAlertDialog fragment = new CenterAlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_center_alert;
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

    @Override
    protected void initArgs(Bundle arguments) {
        title = arguments.getString("title");
        tip = arguments.getString("tip");
        showLoading = arguments.getBoolean("showLoading", false);
        cancelStr = arguments.getString("cancelStr");
        confirmStr = arguments.getString("confirmStr");
    }

    @Override
    protected void initView(View view) {
        tvTitle.setText(title);
        tvTip.setText(tip);
        if (!TextUtils.isEmpty(cancelStr))
        tvCancel.setText(cancelStr);
        if (!TextUtils.isEmpty(confirmStr))
        tvConfirm.setText(confirmStr);

    }

    @OnClick(R.id.tvCancel)
    void onClickCancel(){
        dismiss();
    }
    @OnClick(R.id.tvConfirm)
    void onClickConfirm(){
        if (confirmListener!=null){
            if (showLoading){
                tvConfirm.setVisibility(View.GONE);
                rbConfirm.setVisibility(View.VISIBLE);
            }
            confirmListener.onConfirm();
        }
    }


    private OnConfirmListener confirmListener;

    public OnConfirmListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener{
        void onConfirm();
    }
}
