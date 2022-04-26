package com.yctc.zhiting.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.httputil.NameValuePair;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.ForgetPwdContract;
import com.yctc.zhiting.activity.presenter.ForgetPwdPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.RemovedTipsDialog;
import com.yctc.zhiting.entity.AreaCodeBean;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.popup_window.AreaCodePopupWindow;
import com.yctc.zhiting.request.ForgetPwdRequest;
import com.yctc.zhiting.utils.AreaCodeConstant;
import com.yctc.zhiting.utils.IntentConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 忘记密码
 */
public class ForgetPwdActivity extends MVPBaseActivity<ForgetPwdContract.View, ForgetPwdPresenter> implements ForgetPwdContract.View {

    @BindView(R.id.tvArea)
    TextView tvArea;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirmPwd)
    EditText etConfirmPwd;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.tvCode)
    TextView tvCode;
    @BindView(R.id.llConfirm)
    LinearLayout llConfirm;
    @BindView(R.id.rbConfirm)
    ProgressBar rbConfirm;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.viewLinePhone)
    View viewLinePhone;
    @BindView(R.id.ivVisible)
    ImageView ivVisible;
    @BindView(R.id.ivConfirmVisible)
    ImageView ivConfirmVisible;
    @BindView(R.id.viewLinePassword)
    View viewLinePassword;
    @BindView(R.id.viewLineConfirmPwd)
    View viewLineConfirmPwd;
    @BindView(R.id.viewLineCode)
    View viewLineCode;

    private CountDownTimer countDownTimer; // 验证码计时
    private AreaCodePopupWindow mAreaCodePopupWindow; // 区号选择弹窗

    private String captcha_id = "";
    private String countryCode = "86"; // 地区代码
    private String phone; // 从登录界面带过的手机号

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    protected void initUI() {
        super.initUI();
        initDownTimer();
        initAreaCodePopupWindow();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        phone = intent.getStringExtra(IntentConstant.PHONE);
        countryCode = intent.getStringExtra(IntentConstant.COUNTRY_CODE);
        if (!TextUtils.isEmpty(phone)) {
            etPhone.setText(phone);
        }
        if (TextUtils.isEmpty(countryCode)) {
            countryCode = "86";
        }
        tvArea.setText("+" + countryCode);
    }

    /**
     * 区号选择弹窗
     */
    private void initAreaCodePopupWindow() {
        List<AreaCodeBean> areaCodeData = new Gson().fromJson(AreaCodeConstant.AREA_CODE, new TypeToken<List<AreaCodeBean>>() {
        }.getType());
        mAreaCodePopupWindow = new AreaCodePopupWindow(this, areaCodeData);
        mAreaCodePopupWindow.setSelectedAreaCodeListener(areaCodeBean -> {
            tvArea.setText("+" + areaCodeBean.getCode());
            mAreaCodePopupWindow.dismiss();
        });
        mAreaCodePopupWindow.setOnDismissListener(() -> tvArea.setSelected(false));
    }

    /**
     * 计时
     */
    private void initDownTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCode.setEnabled(false);
                tvCode.setText(String.format(getResources().getString(R.string.login_get_it_again_in_sixty_seconds), String.valueOf(millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                tvCode.setEnabled(true);
                tvCode.setText(getResources().getString(R.string.login_get_verification_code));
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @OnTextChanged(R.id.etPhone)
    void onPhoneChanged(CharSequence s) {
        boolean phoneEmpty = TextUtils.isEmpty(etPhone.getText().toString().trim());
        etPhone.setTextSize(TypedValue.COMPLEX_UNIT_SP, phoneEmpty ? 14 : 22);
        etPhone.setTypeface(phoneEmpty ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
        viewLinePhone.setBackgroundResource(!phoneEmpty ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    /**
     * 密码文本改变
     */
    @OnTextChanged(R.id.etPassword)
    void onChanged() {
        boolean passwdEmpty = TextUtils.isEmpty(etPassword.getText().toString().trim());
        etPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, passwdEmpty ? 14 : 22);
        etPassword.setTypeface(passwdEmpty ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
        ivVisible.setVisibility(passwdEmpty ? View.GONE : View.VISIBLE);
        viewLinePassword.setBackgroundResource(!passwdEmpty ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    /**
     * 确认密码密码文本改变
     */
    @OnTextChanged(R.id.etConfirmPwd)
    void onConfirmPwdChanged() {
        boolean confirmPasswdEmpty = TextUtils.isEmpty(etConfirmPwd.getText().toString().trim());
        etConfirmPwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, confirmPasswdEmpty ? 14 : 22);
        etConfirmPwd.setTypeface(confirmPasswdEmpty ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
        ivConfirmVisible.setVisibility(confirmPasswdEmpty ? View.GONE : View.VISIBLE);
        viewLineConfirmPwd.setBackgroundResource(!confirmPasswdEmpty ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    /**
     * 验证码文本改变
     */
    @OnTextChanged(R.id.etCode)
    void onCodeChanged() {
        boolean codeEmpty = TextUtils.isEmpty(etCode.getText().toString().trim());
        etCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, codeEmpty ? 14 : 22);
        etCode.setTypeface(codeEmpty ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
        viewLineCode.setBackgroundResource(!codeEmpty ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    @OnClick({R.id.tvArea, R.id.tvCode, R.id.llConfirm, R.id.ivVisible, R.id.ivConfirmVisible})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvArea:  // 区号
                if (mAreaCodePopupWindow != null && !mAreaCodePopupWindow.isShowing()) {
                    tvArea.setSelected(true);
                    mAreaCodePopupWindow.showAsDropDown(viewLinePhone, -15, 0);
                }
                break;

            case R.id.tvCode:  // 验证码
                if (checkPhone()) {
                    List<NameValuePair> requestData = new ArrayList<>();
                    requestData.add(new NameValuePair(Constant.TYPE, Constant.FORGET_PWD));
                    requestData.add(new NameValuePair(Constant.TARGET, etPhone.getText().toString().trim()));
                    requestData.add(new NameValuePair(Constant.COUNTRY_CODE, countryCode));
                    mPresenter.getCaptcha(requestData);
                }
                break;

            case R.id.llConfirm: // 确定
                if (checkPhone() && checkPwd() && checkVerificationCode()) {
                    String phone = etPhone.getText().toString().trim();
                    String new_password = etPassword.getText().toString().trim();
                    String captcha = etCode.getText().toString().trim();
                    ForgetPwdRequest forgetPwdRequest = new ForgetPwdRequest(countryCode, phone, new_password, captcha, captcha_id);
                    mPresenter.forgetPwdFail(forgetPwdRequest);
                }
                break;

            case R.id.ivVisible: // 密码是否可见
                ivVisible.setSelected(!ivVisible.isSelected());
                ivVisible.setImageResource(ivVisible.isSelected() ? R.drawable.icon_password_invisible : R.drawable.icon_password_visible);
                if (ivVisible.isSelected()) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etPassword.setSelection(etPassword.getText().length());
                break;

            case R.id.ivConfirmVisible: // 确认密码是否可见
                ivConfirmVisible.setSelected(!ivConfirmVisible.isSelected());
                ivConfirmVisible.setImageResource(ivVisible.isSelected() ? R.drawable.icon_password_invisible : R.drawable.icon_password_visible);
                if (ivConfirmVisible.isSelected()) {
                    etConfirmPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etConfirmPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etConfirmPwd.setSelection(etConfirmPwd.getText().length());
                break;
        }
    }

    /**
     * 检验手机号
     *
     * @return
     */
    private boolean checkPhone() {
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(getResources().getString(R.string.login_please_input_phone));
            return false;
        }
        if (phone.length() != 11) {
            ToastUtil.show(getResources().getString(R.string.login_phone_wrong_format));
            return false;
        }
        return true;
    }

    /**
     * 检查密码
     *
     * @return
     */
    private boolean checkPwd() {
        String pwd = etPassword.getText().toString().trim();
        String confirmPwd = etConfirmPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.show(UiUtil.getString(R.string.mine_new_pwd_hint));
            return false;
        }

        if (pwd.length() < 6 || pwd.length() > 20) {
            ToastUtil.show(UiUtil.getString(R.string.login_password_wrong_format));
            return false;
        }

        if (TextUtils.isEmpty(confirmPwd)) {
            ToastUtil.show(UiUtil.getString(R.string.mine_new_pwd_hint));
            return false;
        }

        if (confirmPwd.length() < 6 || confirmPwd.length() > 20) {
            ToastUtil.show(UiUtil.getString(R.string.login_password_wrong_format));
            return false;
        }

        if (!confirmPwd.equals(pwd)) {
            ToastUtil.show(UiUtil.getString(R.string.mine_two_pwd_is_different));
            return false;
        }
        return true;
    }

    /**
     * 是否输入验证码
     *
     * @return
     */
    private boolean checkVerificationCode() {
        String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.show(UiUtil.getString(R.string.login_please_input_verification_code));
            return false;
        }
        return true;
    }

    /**
     * 获取验证码成功
     *
     * @param captchaBean
     */
    @Override
    public void getCaptchaSuccess(CaptchaBean captchaBean) {
        ToastUtil.show(getResources().getString(R.string.login_sent_successfully));
        if (captchaBean != null) {
            captcha_id = captchaBean.getCaptcha_id();
        }
        countDownTimer.start();
    }

    /**
     * 获取验证码失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getCaptchaFail(int errorCode, String msg) {
        countDownTimer.cancel();
        ToastUtil.show(msg);
    }

    /**
     * 忘记密码成功
     */
    @Override
    public void forgetPwdSuccess() {
        RemovedTipsDialog removedTipsDialog = new RemovedTipsDialog(UiUtil.getString(R.string.mine_pwd_reset_success));
        Bundle bundle = new Bundle();
        bundle.putString("confirmStr", UiUtil.getString(R.string.confirm));
        removedTipsDialog.setArguments(bundle);
        removedTipsDialog.setKnowListener(() -> finish());
        removedTipsDialog.show(this);
    }

    /**
     * 忘记密码失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void forgetPwdFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}