package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.DeviceSettingContract;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.mine.PermissionBean;

/**
 * 添加设备
 */
public class DeviceSettingModel implements DeviceSettingContract.Model {
    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions(id), callback);
    }

    /**
     * 设备详情
     * @param id
     * @param callback
     */
    @Override
    public void getDeviceDetail(int id, RequestDataCallback<DeviceDetailBean> callback) {
        HTTPCaller.getInstance().get(DeviceDetailBean.class, HttpUrlConfig.getAddDeviceUrl()+"/"+id, callback);
    }

    /**
     * 修改设备名称
     * @param id
     * @param name
     * @param callback
     */
    @Override
    public void updateName(int id, String name, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getUpdateDeviceName(String.valueOf(id)), name, callback);
    }

    /**
     * 删除设备
     * @param id
     * @param callback
     */
    @Override
    public void delDevice(int id, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().delete(Object.class, HttpUrlConfig.getUpdateDeviceName(String.valueOf(id)), "", callback);
    }

    /**
     * 设备详情  重构
     * @param id
     * @param callback
     */
    @Override
    public void getDeviceDetailRestructure(int id, RequestDataCallback<DeviceDetailResponseEntity> callback) {
        HTTPCaller.getInstance().get(DeviceDetailResponseEntity.class, HttpUrlConfig.getAddDeviceUrl()+"/"+id, callback);
    }
}
