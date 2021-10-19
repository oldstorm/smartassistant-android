package com.yctc.zhiting.activity;


import android.content.Context;
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
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.LoginContract;
import com.yctc.zhiting.activity.presenter.LoginPresenter;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.entity.mine.LoginBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.RegisterPost;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 登录界面
 */
public class LoginActivity extends MVPBaseActivity<LoginContract.View, LoginPresenter> implements LoginContract.View {

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
    @BindView(R.id.llLogin)
    LinearLayout llLogin;
    @BindView(R.id.rbLogin)
    ProgressBar rbLogin;
    @BindView(R.id.tvBind)
    TextView tvBind;
    @BindView(R.id.tvTips)
    TextView tvTips;

    private boolean showPwd;
    private WeakReference<Context> mContext;
    private DBManager dbManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initUI() {
        super.initUI();
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @OnTextChanged(R.id.etPhone)
    void onPhoneChanged(CharSequence s){
        boolean hint = false;
        etPhone.setTextSize( TypedValue.COMPLEX_UNIT_SP, !TextUtils.isEmpty(etPhone.getText().toString().trim()) ? 24 : 14);
        viewLinePhone.setBackgroundResource(!TextUtils.isEmpty(etPhone.getText().toString().trim()) ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    @OnTextChanged(R.id.etPassword)
    void onChanged(){
        etPassword.setTextSize( TypedValue.COMPLEX_UNIT_SP, !TextUtils.isEmpty(etPassword.getText().toString().trim()) ? 24 : 14);
        ivVisible.setVisibility(TextUtils.isEmpty(etPassword.getText().toString().trim()) ? View.GONE : View.VISIBLE);
        viewLinePassword.setBackgroundResource(!TextUtils.isEmpty(etPassword.getText().toString().trim()) ? R.color.color_3f4663 : R.color.color_CCCCCC);
    }

    @OnClick({R.id.ivVisible, R.id.llLogin, R.id.tvBind})
    void onClick(View view){
        switch (view.getId()){
            case R.id.ivVisible:  // 密码是否可见
                showPwd = !showPwd;
                ivVisible.setImageResource(showPwd ? R.drawable.icon_password_visible : R.drawable.icon_password_invisible);
                if (showPwd){
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                etPassword.setSelection(etPassword.getText().length());
                break;

            case R.id.llLogin:  // 登录
                if (checkPhone() && checkPwd()){
                    String phone = etPhone.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    RegisterPost registerPost = new RegisterPost(phone, password);
                    mPresenter.login(new Gson().toJson(registerPost));
                    setProgressBarVisible(true);
                    setLoginEnabled(false);
                    setTvBindEnabled(false);
                }
                break;

            case R.id.tvBind:  // 绑定云
                switchToActivity(BindCloudActivity.class);
                break;
        }
    }

    /**
     * 检验手机号
     * @return
     */
    private boolean checkPhone(){
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)){
            ToastUtil.show(getResources().getString(R.string.login_please_input_phone));
            return false;
        }
        if (phone.length()!=11){
            ToastUtil.show(getResources().getString(R.string.login_phone_wrong_format));
            return false;
        }
        return true;
    }

    /**
     * 检验密码
     * @return
     */
    private boolean checkPwd(){
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)){
            ToastUtil.show(getResources().getString(R.string.login_please_input_password));
            return false;
        }
//        if (etPassword.length()<6){
//            ToastUtil.show(getResources().getString(R.string.login_password_at_least_6));
//            return false;
//        }
        return true;
    }

    /**
     * 设置登录是否可用
     * @param enabled
     */
    private void setLoginEnabled(boolean enabled){
        llLogin.setEnabled(enabled);
    }


    /**
     * 设置loading是否可见
     * @param visible
     */
    private void setProgressBarVisible(boolean visible){
        rbLogin.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置绑定云是否可用
     * @param enabled
     */
    private void setTvBindEnabled(boolean enabled){
        tvBind.setEnabled(enabled);
    }


    /**
     * 登录成功
     * @param loginBean
     */
    @Override
    public void loginSuccess(LoginBean loginBean) {
        if (loginBean!=null){
            MemberDetailBean memberDetailBean = loginBean.getUser_info();
            if (memberDetailBean!=null) {
                UserUtils.saveUser(memberDetailBean);
                EventBus.getDefault().post(new MineUserInfoEvent(true));
                AllRequestUtil.getCloudArea();
                finish();
            }else {
                fail();
            }
        }else {
            fail();
        }
    }

    /**
     * 用户信息为空
     */
    private void fail(){
        ToastUtil.show(getResources().getString(R.string.login_fail));
        setLoginEnabled(true);
        setTvBindEnabled(true);
        setProgressBarVisible(false);
    }

    /**
     * 登录失败
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


}