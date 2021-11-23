package com.yctc.zhiting.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
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
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.BindCloudContract;
import com.yctc.zhiting.activity.presenter.BindCloudPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.RegisterPost;
import com.yctc.zhiting.utils.AgreementPolicyListener;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 绑定云
 */
public class BindCloudActivity extends MVPBaseActivity<BindCloudContract.View, BindCloudPresenter> implements BindCloudContract.View {

    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.viewLinePhone)
    View viewLinePhone;
    @BindView(R.id.viewLinePassword)
    View viewLinePassword;
    @BindView(R.id.ivVisible)
    ImageView ivVisible;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.viewLineCode)
    View viewLineCode;
    @BindView(R.id.tvCode)
    TextView tvCode;
    @BindView(R.id.llBind)
    LinearLayout llBind;
    @BindView(R.id.rbBind)
    ProgressBar rbBind;
    @BindView(R.id.tvAgreementPolicy)
    TextView tvAgreementPolicy;
    @BindView(R.id.ivSel)
    ImageView ivSel;

    private boolean showPwd;
    private CountDownTimer countDownTimer;

    private String captcha_id = "";


    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_cloud;
    }

    @Override
    protected void initUI() {
        super.initUI();
        llBind.setEnabled(false);
        setBindEnabledStatus();
        tvAgreementPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        tvAgreementPolicy.setText(StringUtil.setAgreementAndPolicyTextStyle(UiUtil.getString(R.string.login_read_and_agree), UiUtil.getColor(R.color.color_2da3f6),
                new AgreementPolicyListener() {
                    @Override
                    public void onHead() {
                        ivSel.setSelected(!ivSel.isSelected());
                    }

                    @Override
                    public void onAgreement() {
                        Bundle bundle = new Bundle();
                        bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.user_agreement));
                        bundle.putString(IntentConstant.URL, Constant.AGREEMENT_URL);
                        switchToActivity(NormalWebActivity.class, bundle);
                    }

                    @Override
                    public void onPolicy() {
                        Bundle bundle = new Bundle();
                        bundle.putString(IntentConstant.TITLE, UiUtil.getString(R.string.privacy_policy));
                        bundle.putString(IntentConstant.URL, Constant.POLICY_URL);
                        switchToActivity(NormalWebActivity.class, bundle);
                    }
                }));
    }

    @Override
    protected void initData() {
        super.initData();
        initDownTimer();
    }

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

    /**
     * 手机号文本改变
     */
    @OnTextChanged(R.id.etPhone)
    void onPhoneChanged() {
        etPhone.setTextSize(TypedValue.COMPLEX_UNIT_SP, !TextUtils.isEmpty(etPhone.getText().toString().trim()) ? 24 : 14);
        viewLinePhone.setBackgroundResource(!TextUtils.isEmpty(etPhone.getText().toString().trim()) ? R.color.color_3f4663 : R.color.color_CCCCCC);
        setBindEnabled();
    }

    /**
     * 密码文本改变
     */
    @OnTextChanged(R.id.etPassword)
    void onChanged() {
        etPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, !TextUtils.isEmpty(etPassword.getText().toString().trim()) ? 24 : 14);
        ivVisible.setVisibility(TextUtils.isEmpty(etPassword.getText().toString().trim()) ? View.GONE : View.VISIBLE);
        viewLinePassword.setBackgroundResource(!TextUtils.isEmpty(etPassword.getText().toString().trim()) ? R.color.color_3f4663 : R.color.color_CCCCCC);
        setBindEnabled();
    }

    /**
     * 验证码文本改变
     */
    @OnTextChanged(R.id.etCode)
    void onCodeChanged() {
        etCode.setTextSize(TypedValue.COMPLEX_UNIT_SP, !TextUtils.isEmpty(etCode.getText().toString().trim()) ? 24 : 14);
        viewLineCode.setBackgroundResource(!TextUtils.isEmpty(etCode.getText().toString().trim()) ? R.color.color_3f4663 : R.color.color_CCCCCC);
        setBindEnabled();
    }

    /**
     * 设置绑定云按钮是否可点击
     */
    private void setBindEnabled() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(code)) {
            llBind.setEnabled(true);
        } else {
            llBind.setEnabled(false);
        }
        setBindEnabledStatus();
    }

    /**
     * 设置按钮状态
     */
    private void setBindEnabledStatus() {
        llBind.setAlpha(llBind.isEnabled() ? 1 : 0.5f);
    }

    @OnClick({R.id.ivVisible, R.id.llBind, R.id.tvLogin, R.id.tvCode, R.id.ivSel})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivVisible:  // 密码是否可见
                showPwd = !showPwd;
                ivVisible.setImageResource(showPwd ? R.drawable.icon_password_visible : R.drawable.icon_password_invisible);
                if (showPwd) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etPassword.setSelection(etPassword.getText().length());
                break;

            case R.id.llBind:  // 绑定
                if (checkPhone() && checkPwd() && checkCode() && checkAgree()) {
                    String phone = etPhone.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String code = etCode.getText().toString().trim();
                    RegisterPost registerPost = new RegisterPost(Constant.CN_CODE, phone, password, code, captcha_id);
                    mPresenter.register(new Gson().toJson(registerPost));
                    setProgressBarVisible(true);
                    llBind.setEnabled(false);
                    setBindEnabledStatus();
                }
                break;

            case R.id.tvLogin:  // 登录
                finish();
                break;

            case R.id.tvCode: // 验证码
                if (checkPhone()) {
                    List<NameValuePair> requestData = new ArrayList<>();
                    requestData.add(new NameValuePair("type", "register"));
                    requestData.add(new NameValuePair("target", etPhone.getText().toString().trim()));
                    requestData.add(new NameValuePair("country_code", Constant.CN_CODE));
                    mPresenter.getCaptcha(requestData);
                }
                break;

            case R.id.ivSel:
                ivSel.setSelected(!ivSel.isSelected());
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
     * 检验密码
     *
     * @return
     */
    private boolean checkPwd() {
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(getResources().getString(R.string.login_please_input_password));
            return false;
        }
        if (etPassword.length() < 6) {
            ToastUtil.show(getResources().getString(R.string.login_password_at_least_6));
            return false;
        }
        return true;
    }

    /**
     * 检验验证码
     *
     * @return
     */
    private boolean checkCode() {
        String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.show(getResources().getString(R.string.login_please_input_verification_code));
            return false;
        }
        if (etCode.length() != 6) {
            ToastUtil.show(getResources().getString(R.string.login_six_verification_code));
            return false;
        }
        return true;
    }

    /**
     * 检查是否勾选用户协议
     *
     * @return
     */
    private boolean checkAgree() {
        if (!ivSel.isSelected()) {
            ToastUtil.show(UiUtil.getString(R.string.please_check_agreement));
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
        ToastUtil.show(msg);
    }

    /**
     * 注册成功
     *
     * @param memberDetailBean
     */
    @Override
    public void registerSuccess(MemberDetailBean memberDetailBean) {
        ToastUtil.show(getResources().getString(R.string.login_register_successfully));
        setProgressBarVisible(false);
        finish();
    }

    /**
     * 注册失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void registerFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        setProgressBarVisible(false);
        setBindEnabled();
    }

    /**
     * 设置loading是否可见
     *
     * @param visible
     */
    private void setProgressBarVisible(boolean visible) {
        rbBind.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}