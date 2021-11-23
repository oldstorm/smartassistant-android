package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.LoginContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.LoginBean;

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
}
