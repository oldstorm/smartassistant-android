package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;

public class DevicePostBean extends BaseEntity {
    private DeviceBean device;

    public DevicePostBean(DeviceBean bean) {
        device = bean;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }
}
