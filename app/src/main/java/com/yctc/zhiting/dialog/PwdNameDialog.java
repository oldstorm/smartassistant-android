package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;
import butterknife.OnClick;

public class PwdNameDialog extends CommonBaseDialog {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.etPwdName)
    EditText etPwdName;

    private String pwdName;
    private String mTitle;
    private String mHint;

    public static PwdNameDialog getInstance(String title) {
        PwdNameDialog pwdNameDialog = new PwdNameDialog();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        pwdNameDialog.setArguments(bundle);
        return pwdNameDialog;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        mTitle = arguments.getString("title");
    }

    @Override
    protected void initView(View view) {
        tvTitle.setText(mTitle);
        tvName.setText(mTitle);
        mHint = String.format(UiUtil.getString(R.string.home_dl_please_input), mTitle);
        etPwdName.setHint(mHint);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_pwd_name;
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

    @OnClick({R.id.tvCancel, R.id.tvConfirm})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvCancel:
                etPwdName.setText("");
                dismiss();
                break;

            case R.id.tvConfirm:
                String un = etPwdName.getText().toString().trim();
                if (TextUtils.isEmpty(un)) {
                    ToastUtil.show(mHint);
                    return;
                }
                etPwdName.setText("");
                if (confirmListener != null) {
                    confirmListener.onConfirm(un);
                }
                break;
        }
    }

    private OnConfirmListener confirmListener;

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener {
        void onConfirm(String pwdName);
    }
}
