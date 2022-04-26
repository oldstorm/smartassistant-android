package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AccountCancellationContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.config.HttpUrlParams;
import com.yctc.zhiting.entity.UnregisterAreasBean;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.request.UnregisterUserRequest;

import java.util.List;

public class AccountCancellationModel implements AccountCancellationContract.Model {

    /**
     * 要删除/退出的家庭列表
     * @param userId
     * @param callback
     */
    @Override
    public void getAreaList(int userId, RequestDataCallback<UnregisterAreasBean> callback) {
        HTTPCaller.getInstance().get(UnregisterAreasBean.class, HttpUrlConfig.getUnregisterAreas(userId) + Constant.ONLY_SC, callback);
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

    /**
     * 注销用户
     * @param user_id
     * @param unregisterUserRequest
     * @param callback
     */
    @Override
    public void unregisterUser(int user_id, UnregisterUserRequest unregisterUserRequest, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().delete(Object.class, HttpUrlConfig.getSCUsers()+"/"+user_id + Constant.ONLY_SC, unregisterUserRequest.toString(), callback);
    }
}
