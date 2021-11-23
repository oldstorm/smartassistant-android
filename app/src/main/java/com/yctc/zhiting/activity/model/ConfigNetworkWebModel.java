package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ConfigNetworkWebContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.home.AccessTokenBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

public class ConfigNetworkWebModel implements ConfigNetworkWebContract.Model {

    @Override
    public void getAccessToken(RequestDataCallback<AccessTokenBean> callback) {
        HTTPCaller.getInstance().post(AccessTokenBean.class, HttpUrlConfig.getDeviceAccessToken(), "", callback);
    }
}
