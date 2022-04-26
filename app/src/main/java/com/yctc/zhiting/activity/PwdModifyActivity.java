package com.yctc.zhiting.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.httputil.cookie.PersistentCookieStore;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.PwdModifyContract;
import com.yctc.zhiting.activity.presenter.PwdModifyPresenter;
import com.yctc.zhiting.dialog.RemovedTipsDialog;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.event.UpdateProfessionStatusEvent;
import com.yctc.zhiting.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 密码修改
 */
public class PwdModifyActivity extends MVPBaseActivity<PwdModifyContract.View, PwdModifyPresenter> implements PwdModifyContract.View {

    @BindView(R.id.etOldPwd)
    EditText etOldPwd;
    @BindView(R.id.ivOldPwdVisible)
    ImageView ivOldPwdVisible;
    @BindView(R.id.viewLineOldPwd)
    View viewLineOldPwd;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.ivPwdVisible)
    ImageView ivPwdVisible;
    @BindView(R.id.viewLinePassword)
    View viewLinePassword;
    @BindView(R.id.etConfirmPwd)
    EditText etConfirmPwd;
    @BindView(R.id.ivConfirmPwdVisible)
    ImageView ivConfirmPwdVisible;
    @BindView(R.id.viewLineConfirmPwd)
    View viewLineConfirmPwd;
    @BindView(R.id.rbConfirm)
    ProgressBar rbConfirm;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.llConfirm)
    LinearLayout llConfirm;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pwd_modify;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.mine_pwd_modify));
    }

    @OnTextChanged(R.id.etOldPwd)
    void onOldPwdChanged(CharSequence s) {
        String oldPwd = etOldPwd.getText().toString().trim();
        ivOldPwdVisible.setVisibility(TextUtils.isEmpty(oldPwd) ? View.GONE : View.VISIBLE);
        viewLineOldPwd.setBackgroundResource(!TextUtils.isEmpty(oldPwd) ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    @OnTextChanged(R.id.etPassword)
    void onPasswordChanged(CharSequence s) {
        String oldPwd = etPassword.getText().toString().trim();
        ivPwdVisible.setVisibility(TextUtils.isEmpty(oldPwd) ? View.GONE : View.VISIBLE);
        viewLinePassword.setBackgroundResource(!TextUtils.isEmpty(oldPwd) ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    @OnTextChanged(R.id.etConfirmPwd)
    void onConfirmPwdChanged(CharSequence s) {
        String oldPwd = etConfirmPwd.getText().toString().trim();
        ivConfirmPwdVisible.setVisibility(TextUtils.isEmpty(oldPwd) ? View.GONE : View.VISIBLE);
        viewLineConfirmPwd.setBackgroundResource(!TextUtils.isEmpty(oldPwd) ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    @OnClick({R.id.ivOldPwdVisible, R.id.ivPwdVisible, R.id.ivConfirmPwdVisible, R.id.llConfirm})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivOldPwdVisible:  // 旧密码是否可见
                ivOldPwdVisible.setSelected(!ivOldPwdVisible.isSelected());
                ivOldPwdVisible.setImageResource(ivOldPwdVisible.isSelected() ? R.drawable.icon_password_invisible : R.drawable.icon_password_visible);
                etOldPwd.setTransformationMethod(ivOldPwdVisible.isSelected() ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
                etOldPwd.setSelection(etOldPwd.getText().length());
                break;

            case R.id.ivPwdVisible:  // 新密码是否可见
                ivPwdVisible.setSelected(!ivPwdVisible.isSelected());
                ivPwdVisible.setImageResource(ivPwdVisible.isSelected() ? R.drawable.icon_password_invisible : R.drawable.icon_password_visible);
                etPassword.setTransformationMethod(ivPwdVisible.isSelected() ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
                etPassword.setSelection(etPassword.getText().length());
                break;

            case R.id.ivConfirmPwdVisible:  // 确认密码是否可见
                ivConfirmPwdVisible.setSelected(!ivConfirmPwdVisible.isSelected());
                ivConfirmPwdVisible.setImageResource(ivConfirmPwdVisible.isSelected() ? R.drawable.icon_password_invisible : R.drawable.icon_password_visible);
                etConfirmPwd.setTransformationMethod(ivConfirmPwdVisible.isSelected() ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
                etConfirmPwd.setSelection(etConfirmPwd.getText().length());
                break;

            case R.id.llConfirm:  // 确定
                confirm();
                break;
        }
    }

    private void confirm() {
        String oldPwd = etOldPwd.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPwd = etConfirmPwd.getText().toString().trim();
        if (TextUtils.isEmpty(oldPwd)) {
            ToastUtil.show(UiUtil.getString(R.string.mine_please_input_old_pwd));
            return;
        }

        if (oldPwd.length() < 6) {
            ToastUtil.show(UiUtil.getString(R.string.mine_pwd_at_least_6));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(UiUtil.getString(R.string.mine_please_input_new_pwd_2));
            return;
        }

        if (password.length() < 6) {
            ToastUtil.show(UiUtil.getString(R.string.mine_pwd_at_least_6));
            return;
        }

        if (TextUtils.isEmpty(confirmPwd)) {
            ToastUtil.show(UiUtil.getString(R.string.mine_please_input_confirm_new_pwd));
            return;
        }

        if (confirmPwd.length() < 6) {
            ToastUtil.show(UiUtil.getString(R.string.mine_pwd_at_least_6));
            return;
        }

        if (!password.equals(confirmPwd)) {
            ToastUtil.show(UiUtil.getString(R.string.mine_two_pwd_is_different));
            return;
        }


        UpdateUserPost updateUserPost = new UpdateUserPost(oldPwd, password);
        String body = new Gson().toJson(updateUserPost);
        mPresenter.updatePwd(UserUtils.getCloudUserId(), body);
        setProgressBarVisible(true);
        llConfirm.setEnabled(false);
        setBindEnabledStatus();
    }

    /**
     * 设置按钮状态
     */
    private void setBindEnabledStatus() {
        llConfirm.setAlpha(llConfirm.isEnabled() ? 1 : 0.5f);
    }

    /**
     * 设置loading是否可见
     *
     * @param visible
     */
    private void setProgressBarVisible(boolean visible) {
        rbConfirm.setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    /**
     * 修改密码是否成功提示
     * @param success
     */
    private void showTipDialog(boolean success) {
        RemovedTipsDialog removedTipsDialog = new RemovedTipsDialog(success ? UiUtil.getString(R.string.mine_pwd_modified_login_again) :  UiUtil.getString(R.string.mine_pwd_modified_fail));
        if (success){
            Bundle bundle = new Bundle();
            bundle.putString("confirmStr", UiUtil.getString(R.string.confirm));
            removedTipsDialog.setArguments(bundle);
        }
        removedTipsDialog.setKnowListener(new RemovedTipsDialog.OnKnowListener() {
            @Override
            public void onKnow() {
                if (success) { // 修改成功
                    LibLoader.finishAllActivityExcludeCertain(MainActivity.class);
                    switchToActivity(LoginActivity.class);
                }
                removedTipsDialog.dismiss();
            }
        });
        removedTipsDialog.show(this);
    }

    /**
     * 修改密码成功
     */
    @Override
    public void updatePwdSuccess() {
        UserUtils.saveUser(null);
        PersistentCookieStore.getInstance().removeAll();
        EventBus.getDefault().post(new UpdateProfessionStatusEvent());
        EventBus.getDefault().post(new MineUserInfoEvent(false));
        showTipDialog(true);
    }

    /**
     * 修改密码失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void updatePwdFail(int errorCode, String msg) {
        setProgressBarVisible(false);
        llConfirm.setEnabled(true);
        setBindEnabledStatus();
        if (errorCode == 2012) {  // 密码错误
            ToastUtil.show(msg);
        }else {
            showTipDialog(false);
        }
    }
}