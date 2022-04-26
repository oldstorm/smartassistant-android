package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneDeviceTaskAttrContract;
import com.yctc.zhiting.activity.contract.TaskDeviceControlContract;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务设备控制
 */
public class SceneDeviceTaskAttrModel implements SceneDeviceTaskAttrContract.Model {
    @Override
    public void getDeviceDetail(int id, int type, RequestDataCallback<DeviceDetailResponseEntity> callback) {
        List<NameValuePair> requestData = new ArrayList<>();
        requestData.add(new NameValuePair("type", String.valueOf(type)));
        HTTPCaller.getInstance().get(DeviceDetailResponseEntity.class, HttpUrlConfig.getAddDeviceUrl()+"/"+id, requestData, callback);
    }
}
