package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.activity.contract.UpdateDeviceLocationContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.AreasBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.request.AddRoomRequest;

/**
 * 添加设备
 */
public class UpdateLocationModel implements UpdateDeviceLocationContract.Model {
    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions(id), callback);
    }

    @Override
    public void getAreaList(RequestDataCallback<AreasBean> callback) {
        HTTPCaller.getInstance().get(AreasBean.class, Constant.AREA_TYPE == 2 ? HttpUrlConfig.getDepartments() : HttpUrlConfig.getLocations(), callback);
    }

    @Override
    public void addAreaRoom(AddRoomRequest request, RequestDataCallback<AreasBean> callback) {
        HTTPCaller.getInstance().post(AreasBean.class, Constant.AREA_TYPE == 2 ? HttpUrlConfig.getDepartments() : HttpUrlConfig.getLocations(), request.toString(),callback);
    }

    @Override
    public void updateDeviceName(String deviceId, Request request, RequestDataCallback<String> callback) {
        HTTPCaller.getInstance().put(String.class, HttpUrlConfig.getUpdateDeviceName(deviceId), request.toString(),callback);
    }
}
