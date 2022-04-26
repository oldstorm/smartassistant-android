package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ThirdPartyContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.ThirdPartyBean;

import java.util.List;

public class ThirdPartyModel implements ThirdPartyContract.Model {
    @Override
    public void getThirdPartyList(boolean sc, List<NameValuePair> requestData, RequestDataCallback<ThirdPartyBean> callback) {
        HTTPCaller.getInstance().get(ThirdPartyBean.class, HttpUrlConfig.getThirdParty(sc), requestData, callback);
    }
}
