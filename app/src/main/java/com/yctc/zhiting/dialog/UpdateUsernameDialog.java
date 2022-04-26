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
 * 修改用户名
 */
public class UpdateUsernameDialog extends CommonBaseDialog {

    @BindView(R.id.etUsername)
    EditText etUsername;

    private String username;

    @Override
    protected void initArgs(Bundle arguments) {
        username = arguments.getString("username");
    }

    @Override
    protected void initView(View view) {
        etUsername.setText(username);
        etUsername.addTextChangedListener(new TextWatcher() {
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
    protected int getLayoutResource() {
        return R.layout.dialog_update_username;
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

    @OnClick({R.id.tvCancel, R.id.tvSave})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvCancel:
                dismiss();
                break;

            case R.id.tvSave:
                String un = etUsername.getText().toString().trim();
                if (TextUtils.isEmpty(un)) {
                    ToastUtil.show(UiUtil.getString(R.string.mine_username_colon_hint));
                    return;
                }
                if (saveListener != null) {
                    saveListener.onSave(un);
                }
                break;
        }
    }

    private OnSaveListener saveListener;

    public void setSaveListener(OnSaveListener saveListener) {
        this.saveListener = saveListener;
    }

    public interface OnSaveListener {
        void onSave(String username);
    }
}
