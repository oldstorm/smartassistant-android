package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;

public class CheckUpdateDialog extends CommonBaseDialog implements View.OnClickListener {

    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvContent)
    TextView tvContent;

    private String latestVersion;

    public static CheckUpdateDialog newInstance(String latest_version) {
        Bundle args = new Bundle();
        args.putString("version", latest_version);
        CheckUpdateDialog fragment = new CheckUpdateDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean getCancelable() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_check_update;
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
        latestVersion = arguments.getString("version");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvConfirm && mListener != null) {
            mListener.onConfirm();
        }
        dismiss();
    }

    @Override
    protected void initView(View view) {
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvContent.setText(String.format(UiUtil.getString(R.string.home_check_update_content), latestVersion));
    }

    private OnConfirmListener mListener;

    public interface OnConfirmListener {
        void onConfirm();
    }

    public CheckUpdateDialog setDialogListener(OnConfirmListener mListener) {
        this.mListener = mListener;
        return this;
    }
}
