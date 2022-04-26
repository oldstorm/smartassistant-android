package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AboutContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.AndroidAppVersionBean;

import java.util.ArrayList;
import java.util.List;

public class AboutModel implements AboutContract.Model {
    /**
     * 获取更新app版本信息
     *
     * @param callback
     */
    @Override
    public void getAppVersionInfo(RequestDataCallback<AndroidAppVersionBean> callback) {
        List<NameValuePair> requestData = new ArrayList<>();
        requestData.add(new NameValuePair(Constant.CLIENT, Constant.ANDROID));
        HTTPCaller.getInstance().get(AndroidAppVersionBean.class, HttpUrlConfig.getAppVersionInfo() + Constant.ONLY_SC, requestData, callback);
    }
}
