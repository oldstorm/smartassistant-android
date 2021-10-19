package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SupportBrandContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;

import java.util.List;

/**
 * 添加设备
 */
public class SupportBrandModel implements SupportBrandContract.Model {
    @Override
    public void getBrandList(List<NameValuePair> requestData, RequestDataCallback<BrandListBean> callback) {
        HTTPCaller.getInstance().get(BrandListBean.class, HttpUrlConfig.getBrandListUrl(), requestData, callback);
    }
}
