package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneDeviceConditionAttrContract;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加设备
 */
public class SceneDeviceConditionAttrModel implements SceneDeviceConditionAttrContract.Model {
    @Override
    public void getDeviceDetail(int id, int type, RequestDataCallback<DeviceDetailResponseEntity> callback) {
        List<NameValuePair> requestData = new ArrayList<>();
        requestData.add(new NameValuePair("type", String.valueOf(type)));
        HTTPCaller.getInstance().get(DeviceDetailResponseEntity.class, HttpUrlConfig.getAddDeviceUrl()+"/"+id, requestData, callback);
    }
}
