package com.yctc.zhiting.activity;

import static com.yctc.zhiting.config.Constant.CurrentHome;

import android.graphics.Typeface;
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

import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.NameValuePair;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.LoginContract;
import com.yctc.zhiting.activity.presenter.LoginPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.AreaCodeBean;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.entity.mine.LoginBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.RegisterPost;
import com.yctc.zhiting.event.FinishLoginEvent;
import com.yctc.zhiting.event.LogoutEvent;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.event.RefreshHomeEvent;
import com.yctc.zhiting.event.UpdateProfessionStatusEvent;
import com.yctc.zhiting.popup_window.AreaCodePopupWindow;
import com.yctc.zhiting.utils.AgreementPolicyListener;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.AreaCodeConstant;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.websocket.WSocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 登录界面
 */
public class LoginActivity extends MVPBaseActivity<LoginContract.View, LoginPresenter> implements LoginContract.View {

    @BindView(R.id.tvArea)
    TextView tvArea;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.viewLinePhone)
    View viewLinePhone;
    @BindView(R.id.viewLinePassword)
    View viewLinePassword;
    @BindView(R.id.ivVisible)
    ImageView ivVisible;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;
    @BindView(R.id.rbLogin)
    ProgressBar rbLogin;
    @BindView(R.id.tvBind)
    TextView tvBind;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.tvAgreementPolicy)
    TextView tvAgreementPolicy;
    @BindView(R.id.ivSel)
    ImageView ivSel;
    @BindView(R.id.tvLoginWay)
    TextView tvLoginWay;
    @BindView(R.id.tvCode)
    TextView tvCode;
    @BindView(R.id.llVerificate)
    LinearLayout llVerificate;
    @BindView(R.id.llPassword)
    LinearLayout llPassword;

    private boolean showPwd;
    private int mLoginType = 0;//0:密码登录，1：短信登录
    private String mCountryCode = "86";
    private String mCaptchaId;
    private CountDownTimer mCountDownTimer;
    private AreaCodePopupWindow mAreaCodePopupWindow; // 区号选择弹窗

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initUI() {
        super.initUI();
        initDownTimer();
        initAreaCodePopupWindow();
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
    protected void initListener() {
        super.initListener();
        etPhone.setOnTouchListener((v, event) -> {
            tvTips.setVisibility(View.GONE);
            return false;
        });
        etPassword.setOnTouchListener((v, event) -> {
            tvTips.setVisibility(View.GONE);
            return false;
        });
        etCode.setOnTouchListener((v, event) -> {
            tvTips.setVisibility(View.GONE);
            return false;
        });
    }

    /**
     * 计时
     */
    private void initDownTimer() {
        mCountDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCode.setEnabled(false);
                String text = UiUtil.getString(R.string.login_get_it_again_in_sixty_seconds);
                tvCode.setText(String.format(text, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvCode.setEnabled(true);
                tvCode.setText(UiUtil.getString(R.string.login_get_verification_code));
            }
        };
    }

    /**
     * 区号选择弹窗
     */
    private void initAreaCodePopupWindow() {
        List<AreaCodeBean> areaCodeData = new Gson().fromJson(AreaCodeConstant.AREA_CODE, new TypeToken<List<AreaCodeBean>>() {
        }.getType());
        mAreaCodePopupWindow = new AreaCodePopupWindow(this, areaCodeData);
        mAreaCodePopupWindow.setSelectedAreaCodeListener(areaCodeBean -> {
            mCountryCode = areaCodeBean.getCode();
            tvArea.setText("+" + areaCodeBean.getCode());
            mAreaCodePopupWindow.dismiss();
        });

        mAreaCodePopupWindow.setOnDismissListener(() -> tvArea.setSelected(false));
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @OnTextChanged(R.id.etPhone)
    void onPhoneChanged(CharSequence s) {
        boolean phoneEmpty = TextUtils.isEmpty(etPhone.getText().toString().trim());
        etPhone.setTextSize(TypedValue.COMPLEX_UNIT_SP, phoneEmpty ? 14 : 22);
        etPhone.setTypeface(phoneEmpty ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
        viewLinePhone.setBackgroundResource(!phoneEmpty ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    @OnTextChanged(R.id.etPassword)
    void onChanged() {
        boolean passwdEmpty = TextUtils.isEmpty(etPassword.getText().toString().trim());
        etPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, passwdEmpty ? 14 : 22);
        etPassword.setTypeface(passwdEmpty ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);
        ivVisible.setVisibility(TextUtils.isEmpty(etPassword.getText().toString().trim()) ? View.GONE : View.VISIBLE);
        viewLinePassword.setBackgroundResource(!passwdEmpty ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    @OnClick({R.id.tvArea, R.id.ivVisible, R.id.llLogin, R.id.tvBind, R.id.ivSel, R.id.tvForget, R.id.tvCode, R.id.tvLoginWay})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCode:
                getCaptcha();
                break;

            case R.id.tvArea: // 区号选择
                selectArea();
                break;

            case R.id.ivVisible:// 密码是否可见
                passwordVisible();

                break;
            case R.id.llLogin:  // 登录
                login();
                break;

            case R.id.tvBind:  // 绑定云
                switchToActivity(BindCloudActivity.class);
                break;

            case R.id.ivSel:
                ivSel.setSelected(!ivSel.isSelected());
                break;

            case R.id.tvForget:// 忘记密码
                goToForgetPassword();
                break;

            case R.id.tvLoginWay://切换登录方式
                switchLoginWay();
                break;
        }
    }

    public void switchLoginWay() {
        etCode.setText("");
        etPassword.setText("");
        tvTips.setVisibility(View.GONE);
        String text = tvLoginWay.getText().toString();
        if (text.equals(UiUtil.getString(R.string.mine_login_verificate))) {
            mLoginType = 1;
            tvLoginWay.setText(R.string.mine_login_password);
            llPassword.setVisibility(View.GONE);
            llVerificate.setVisibility(View.VISIBLE);
        } else {
            mLoginType = 0;
            llPassword.setVisibility(View.VISIBLE);
            llVerificate.setVisibility(View.GONE);
            tvLoginWay.setText(R.string.mine_login_verificate);
        }
    }

    /**
     * 区号选择
     */
    private void selectArea() {
        if (mAreaCodePopupWindow != null && !mAreaCodePopupWindow.isShowing()) {
            tvArea.setSelected(true);
            mAreaCodePopupWindow.showAsDropDown(viewLinePhone, -15, 0);
        }
    }

    /**
     * 密码是否可见
     */
    private void passwordVisible() {
        showPwd = !showPwd;
        ivVisible.setImageResource(showPwd ? R.drawable.icon_password_invisible : R.drawable.icon_password_visible);
        if (showPwd) {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    /**
     * 跳转忘记密码
     */
    private void goToForgetPassword() {
        String phone = etPhone.getText().toString().trim();
        Bundle bundle = new Bundle();
        bundle.putString(IntentConstant.PHONE, phone);
        bundle.putString(IntentConstant.COUNTRY_CODE, mCountryCode);
        switchToActivity(ForgetPwdActivity.class, bundle);
    }

    /**
     * 登录
     */
    private void login() {
        if (mLoginType == 0) {
            if (!(checkPhone() && checkPwd() && checkAgree())) {
                return;
            }
        } else {
            if (!(checkPhone() && checkCaptcha() && checkAgree())) {
                return;
            }
        }

        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String captcha = etCode.getText().toString().trim();
        RegisterPost registerPost = new RegisterPost(phone, password);
        registerPost.setCountry_code(mCountryCode);
        registerPost.setCaptcha_id(mCaptchaId);
        registerPost.setCaptcha(captcha);
        registerPost.setLogin_type(mLoginType);//login_type 0:密码登录，1：短信登录

        mPresenter.login(GsonConverter.getGson().toJson(registerPost));
        setProgressBarVisible(true);
        setLoginEnabled(false);
        setTvBindEnabled(false);
    }

    /**
     * 获取验证码
     */
    private void getCaptcha() {
        if (checkPhone()) {
            List<NameValuePair> requestData = new ArrayList<>();
            requestData.add(new NameValuePair(Constant.TYPE, Constant.LOGIN));
            requestData.add(new NameValuePair(Constant.TARGET, etPhone.getText().toString().trim()));
            requestData.add(new NameValuePair(Constant.COUNTRY_CODE, mCountryCode));
            mPresenter.getCaptcha(requestData);
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

    private boolean checkCaptcha() {
        String captcha = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(captcha)) {
            ToastUtil.show(getResources().getString(R.string.login_please_input_captcha));
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
     * 设置登录是否可用
     *
     * @param enabled
     */
    private void setLoginEnabled(boolean enabled) {
        tvArea.setEnabled(enabled);
        etPhone.setEnabled(enabled);
        llLogin.setEnabled(enabled);
    }


    /**
     * 设置loading是否可见
     *
     * @param visible
     */
    private void setProgressBarVisible(boolean visible) {
        rbLogin.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置绑定云是否可用
     *
     * @param enabled
     */
    private void setTvBindEnabled(boolean enabled) {
        tvBind.setEnabled(enabled);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FinishLoginEvent event) {
        if (!isDestroyed()) finish();
    }

    /**
     * 登录成功
     *
     * @param loginBean
     */
    @Override
    public void loginSuccess(LoginBean loginBean) {
        if (loginBean != null) {
            startConnectSocket();
            EventBus.getDefault().post(new LogoutEvent());
            SpUtil.put(SpConstant.PHONE_NUM, etPhone.getText().toString());
            SpUtil.put(SpConstant.AREA_CODE, mCountryCode);
            MemberDetailBean memberDetailBean = loginBean.getUser_info();
            if (memberDetailBean != null) {
                UserUtils.saveUser(memberDetailBean);
                EventBus.getDefault().post(new MineUserInfoEvent(false));
                EventBus.getDefault().post(new UpdateProfessionStatusEvent());
                //EventBus.getDefault().post(new RefreshHomeEvent());
                AllRequestUtil.getCloudArea();
                finish();
            } else {
                fail();
            }
        } else {
            fail();
        }
    }

    /**
     * 开始连接socket
     */
    private void startConnectSocket() {
        if (!WSocketManager.isConnecting && CurrentHome != null && CurrentHome.isIs_bind_sa()) {
            WSocketManager.getInstance().start();
            UiUtil.postDelayed(() -> {
                if (!WSocketManager.isConnecting)
                    WSocketManager.getInstance().start();
            }, 2000);
        }
    }

    /**
     * 用户信息为空
     */
    private void fail() {
        ToastUtil.show(getResources().getString(R.string.login_fail));
        setLoginEnabled(true);
        setTvBindEnabled(true);
        setProgressBarVisible(false);
    }

    /**
     * 登录失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void loginFail(int errorCode, String msg) {
        tvTips.setText(msg);
        tvTips.setVisibility(View.VISIBLE);
        setLoginEnabled(true);
        setTvBindEnabled(true);
        setProgressBarVisible(false);
    }

    @Override
    public void getCaptchaSuccess(CaptchaBean captchaBean) {
        mCountDownTimer.start();
        ToastUtil.show(UiUtil.getString(R.string.login_sent_successfully));
        if (captchaBean != null) {
            mCaptchaId = captchaBean.getCaptcha_id();
        }
    }

    @Override
    public void getCaptchaFail(int errorCode, String msg) {
        mCountDownTimer.cancel();
        ToastUtil.show(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }
}