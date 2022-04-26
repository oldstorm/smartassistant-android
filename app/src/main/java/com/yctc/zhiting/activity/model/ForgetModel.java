package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ForgetPwdContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.request.ForgetPwdRequest;

import java.util.List;

public class ForgetModel implements ForgetPwdContract.Model {

    /**
     * 获取验证码
     * @param requestData
     * @param callback
     */
    @Override
    public void getCaptcha(List<NameValuePair> requestData, RequestDataCallback<CaptchaBean> callback) {
        HTTPCaller.getInstance().get(CaptchaBean.class, HttpUrlConfig.getCaptcha() + Constant.ONLY_SC, requestData, callback);
    }

    /**
     * 忘记密码
     * @param forgetPwdRequest
     * @param callback
     */
    @Override
    public void forgetPwd(ForgetPwdRequest forgetPwdRequest, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getForgetPwd() + Constant.ONLY_SC, forgetPwdRequest, callback);
    }
}
