package com.yctc.zhiting.event;

import com.yctc.zhiting.entity.home.DeviceMultipleBean;

import java.util.List;

public class DeviceDataEvent {
    List<DeviceMultipleBean> devices;

    public DeviceDataEvent(List<DeviceMultipleBean> devices) {
        this.devices = devices;
    }

    public List<DeviceMultipleBean> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceMultipleBean> devices) {
        this.devices = devices;
    }
}
