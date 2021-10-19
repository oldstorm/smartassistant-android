package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneDeviceTaskAttrContract;
import com.yctc.zhiting.activity.contract.TaskDeviceControlContract;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;

/**
 * 任务设备控制
 */
public class SceneDeviceTaskAttrModel implements SceneDeviceTaskAttrContract.Model {
    @Override
    public void getDeviceDetail(int id, RequestDataCallback<DeviceDetailResponseEntity> callback) {
        HTTPCaller.getInstance().get(DeviceDetailResponseEntity.class, HttpUrlConfig.getAddDeviceUrl()+"/"+id, callback);
    }
}
