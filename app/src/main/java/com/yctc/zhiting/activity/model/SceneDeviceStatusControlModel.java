package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.activity.contract.SceneDeviceStatusControlContract;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.HttpUrlConfig;

/**
 * 添加设备
 */
public class SceneDeviceStatusControlModel implements SceneDeviceStatusControlContract.Model {
    @Override
    public void getDeviceDetail(int id, RequestDataCallback<DeviceDetailBean> callback) {
        HTTPCaller.getInstance().get(DeviceDetailBean.class, HttpUrlConfig.getAddDeviceUrl()+"/"+id, callback);
    }
}
