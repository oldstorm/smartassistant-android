package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ChangeIconContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.home.DeviceLogoListBean;

public class ChangeIconModel implements ChangeIconContract.Model {

    @Override
    public void getLogoList(int id, RequestDataCallback<DeviceLogoListBean> callback) {
        HTTPCaller.getInstance().get(DeviceLogoListBean.class, HttpUrlConfig.getLogo(id), callback);
    }

    @Override
    public void updateType(int id, String logoType, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getUpdateDeviceName(String.valueOf(id)), logoType, callback);
    }
}
