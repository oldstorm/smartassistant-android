package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.LoginContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.entity.mine.LoginBean;

import java.util.List;

/**
 * 登录
 */
public class LoginModel implements LoginContract.Model {

    /**
     * 登录
     *
     * @param body
     * @param callback
     */
    @Override
    public void login(String body, RequestDataCallback<LoginBean> callback) {
        HTTPCaller.getInstance().post(LoginBean.class, HttpUrlConfig.getLogin() + Constant.ONLY_SC, body, callback);
    }

    /**
     * 获取验证码
     * @param requestData
     * @param callback
     */
    @Override
    public void getCaptcha(List<NameValuePair> requestData, RequestDataCallback<CaptchaBean> callback) {
        HTTPCaller.getInstance().get(CaptchaBean.class, HttpUrlConfig.getCaptcha() + Constant.ONLY_SC, requestData, callback);
    }
}
