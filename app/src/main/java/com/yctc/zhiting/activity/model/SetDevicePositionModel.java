package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.activity.contract.SetDevicePositionContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.AreasBean;
import com.yctc.zhiting.request.AddRoomRequest;

public class SetDevicePositionModel implements SetDevicePositionContract.Model {
    @Override
    public void getAreaList(RequestDataCallback<AreasBean> callback) {
        HTTPCaller.getInstance().get(AreasBean.class, HttpUrlConfig.getLocations(), callback);
    }

    @Override
    public void addAreaRoom(AddRoomRequest request, RequestDataCallback<AreasBean> callback) {
        HTTPCaller.getInstance().post(AreasBean.class, HttpUrlConfig.getLocations(), request.toString(),callback);
    }

    @Override
    public void updateDeviceName(String deviceId,Request request, RequestDataCallback<String> callback) {
        HTTPCaller.getInstance().put(String.class, HttpUrlConfig.getUpdateDeviceName(deviceId), request.toString(),callback);
    }
}
