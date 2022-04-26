package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.BindCloudContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.entity.mine.LoginBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

import java.util.List;

/**
 * 绑定云
 */
public class BindCloudModel implements BindCloudContract.Model {

    /**
     * 验证码
     *
     * @param requestData
     * @param callback
     */
    @Override
    public void getCaptcha(List<NameValuePair> requestData, RequestDataCallback<CaptchaBean> callback) {
        HTTPCaller.getInstance().get(CaptchaBean.class, HttpUrlConfig.getCaptcha() + Constant.ONLY_SC, requestData, callback);
    }

    /**
     * 注册
     *
     * @param body
     * @param callback
     */
    @Override
    public void register(String body, RequestDataCallback<LoginBean> callback) {
        HTTPCaller.getInstance().post(LoginBean.class, HttpUrlConfig.getRegister() + Constant.ONLY_SC, body, callback);
    }
}
