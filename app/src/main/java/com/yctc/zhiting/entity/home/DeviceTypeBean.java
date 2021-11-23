package com.yctc.zhiting.entity.home;

import java.util.List;

/**
 * 设备分类
 */
public class DeviceTypeBean {

    private boolean selected;
    private String name;
    private String type;
    private List<DeviceTypeDeviceBean> devices;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DeviceTypeDeviceBean> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceTypeDeviceBean> devices) {
        this.devices = devices;
    }
}
