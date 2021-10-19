package com.yctc.zhiting.entity.scene;

import com.yctc.zhiting.entity.home.DeviceMultipleBean;

import java.util.List;

public class SceneDevicesBean {

    private int id;
    private String location;
    private List<DeviceMultipleBean> deviceMultipleBean;

    public SceneDevicesBean(int id, String location) {
        this.id = id;
        this.location = location;
    }

    public SceneDevicesBean(int id, String location, List<DeviceMultipleBean> deviceMultipleBean) {
        this.id = id;
        this.location = location;
        this.deviceMultipleBean = deviceMultipleBean;
    }

    public SceneDevicesBean(String location, List<DeviceMultipleBean> deviceMultipleBean) {
        this.location = location;
        this.deviceMultipleBean = deviceMultipleBean;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<DeviceMultipleBean> getDeviceMultipleBean() {
        return deviceMultipleBean;
    }

    public void setDeviceMultipleBean(List<DeviceMultipleBean> deviceMultipleBean) {
        this.deviceMultipleBean = deviceMultipleBean;
    }
}
