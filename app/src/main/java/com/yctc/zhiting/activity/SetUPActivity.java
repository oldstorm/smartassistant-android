package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SetUPContract;
import com.yctc.zhiting.activity.presenter.SetUPPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.utils.Md5Util;
import com.yctc.zhiting.utils.StringUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置用户名和密码
 */
public class SetUPActivity extends MVPBaseActivity<SetUPContract.View, SetUPPresenter> implements SetUPContract.View {

    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;

    @Override
    protected void initUI() {
        super.initUI();
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

    @OnClick({R.id.tvSave, R.id.llClose})
    public void onClick(View view) {
        if (view.getId() == R.id.tvSave) {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            hideKeyboard(etPassword);
            if (TextUtils.isEmpty(username)){
                ToastUtil.show(getResources().getString(R.string.mine_input_username));
//                etUsername.requestFocus();
//                showKeyboard(etUsername);
                return;
            }

            if (StringUtil.isChinese(username)){
                ToastUtil.show(getResources().getString(R.string.mine_username_chinese_cannot_be_entered));
//                etUsername.requestFocus();
//                showKeyboard(etUsername);
                return;
            }
            if (TextUtils.isEmpty(password)){
                ToastUtil.show(getResources().getString(R.string.mine_input_password));
                etPassword.requestFocus();
                showKeyboard(etPassword);
                return;
            }

            if (password.length()<6){
                ToastUtil.show(getResources().getString(R.string.mine_password_length_must_be_greater_than_6));
//                etPassword.requestFocus();
//                showKeyboard(etPassword);
                return;
            }

            if (StringUtil.isChinese(password)){
                ToastUtil.show(getResources().getString(R.string.mine_password_chinese_cannot_be_entered));
//                etPassword.requestFocus();
//                showKeyboard(etPassword);
                return;
            }
            UpdateUserPost updateUserPost = new UpdateUserPost();
            updateUserPost.setAccount_name(username);
            updateUserPost.setPassword(password);
            String body = new Gson().toJson(updateUserPost);
            mPresenter.updateMember(Constant.CurrentHome.getUser_id(), body);

        } else if (view.getId() == R.id.llClose) {
            finish();
        }
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected boolean isSetStateBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_upactivity;
    }

    @Override
    public void updateSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_set_successfully));
        finish();
    }

    @Override
    public void requestFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}