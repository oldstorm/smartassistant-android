package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.activity.contract.SupportBrandContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.BrandDetailBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.BrandsBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;

/**
 * 添加设备
 */
public class BrandDetailModel implements BrandDetailContract.Model {
    @Override
    public void getDetail(String name, RequestDataCallback<BrandDetailBean> callback) {
        HTTPCaller.getInstance().get(BrandDetailBean.class, HttpUrlConfig.getBrandListUrl()+"/"+name, callback);
    }
}
