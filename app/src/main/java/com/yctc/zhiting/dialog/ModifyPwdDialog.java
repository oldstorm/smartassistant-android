package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 修改密码弹窗
 */
public class ModifyPwdDialog extends CommonBaseDialog {

    @BindView(R.id.etOldPwd)
    EditText etOldPwd;
    @BindView(R.id.etNewPwd)
    EditText etNewPwd;
    @BindView(R.id.etConfirmPwd)
    EditText etConfirmPwd;

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        setCancelable(false);
        addEditListener(etOldPwd);
        addEditListener(etNewPwd);
        addEditListener(etConfirmPwd);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_modify_pwd;
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

    private void addEditListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    for (int i = 0; i < s.length(); i++) {
                        char c = s.charAt(i);
                        if (c >= 0x4e00 && c <= 0x9fff) {
                            s.delete(i, i + 1);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * 置空
     */
    public void resetText() {
        if (etOldPwd != null) {
            etOldPwd.setText("");
        }

        if (etNewPwd != null) {
            etNewPwd.setText("");
        }

        if (etConfirmPwd != null) {
            etConfirmPwd.setText("");
        }
    }

    @OnClick({R.id.tvCancel, R.id.tvConfirm})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvCancel:
                resetText();
                dismiss();
                break;

            case R.id.tvConfirm:
                String oldPwd = etOldPwd.getText().toString().trim();
                String newPwd = etNewPwd.getText().toString().trim();
                String confirmPwd = etConfirmPwd.getText().toString().trim();
                if (TextUtils.isEmpty(oldPwd)) {
                    ToastUtil.show(UiUtil.getString(R.string.mine_old_pwd_hint));
                    return;
                }

                if (oldPwd.length() < 6) {
                    ToastUtil.show(getResources().getString(R.string.mine_password_length_must_be_greater_than_6));
                    return;
                }

                if (TextUtils.isEmpty(newPwd)) {
                    ToastUtil.show(UiUtil.getString(R.string.mine_new_pwd_hint));
                    return;
                }

                if (newPwd.length() < 6) {
                    ToastUtil.show(getResources().getString(R.string.mine_password_length_must_be_greater_than_6));
                    return;
                }


                if (TextUtils.isEmpty(confirmPwd)) {
                    ToastUtil.show(UiUtil.getString(R.string.mine_confirm_pwd_hint));
                    return;
                }

                if (confirmPwd.length() < 6) {
                    ToastUtil.show(getResources().getString(R.string.mine_password_length_must_be_greater_than_6));
                    return;
                }

                if (!newPwd.equals(confirmPwd)) {
                    ToastUtil.show(UiUtil.getString(R.string.mine_confirm_new_pwd_different));
                    return;
                }
                if (confirmListener != null) {
                    confirmListener.onConfirm(oldPwd, newPwd);
                }
                break;
        }
    }

    private OnConfirmListener confirmListener;

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface OnConfirmListener {
        void onConfirm(String oldPwd, String newPwd);
    }
}
