package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
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

    @OnClick({R.id.tvSave, R.id.llClose})
    public void onClick(View view) {
        if (view.getId() == R.id.tvSave) {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            hideKeyboard(etPassword);
            if (TextUtils.isEmpty(username)){
                ToastUtil.show(getResources().getString(R.string.mine_input_username));
                etUsername.requestFocus();
                showKeyboard(etUsername);
                return;
            }
            if (username.length()<6){
                ToastUtil.show(getResources().getString(R.string.mine_username_length_must_be_greater_than_6));
                etUsername.requestFocus();
                showKeyboard(etUsername);
                return;
            }
            if (StringUtil.isChinese(username)){
                ToastUtil.show(getResources().getString(R.string.mine_username_chinese_cannot_be_entered));
                etUsername.requestFocus();
                showKeyboard(etUsername);
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
                etPassword.requestFocus();
                showKeyboard(etPassword);
                return;
            }

            if (StringUtil.isChinese(password)){
                ToastUtil.show(getResources().getString(R.string.mine_password_chinese_cannot_be_entered));
                etPassword.requestFocus();
                showKeyboard(etPassword);
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