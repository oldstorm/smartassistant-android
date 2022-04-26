package com.yctc.zhiting.entity.ws_response;

import java.io.Serializable;
import java.util.List;


/**
 * 连接设备/添加设备
 */
public class WSDeviceResponseBean implements Serializable {

    private boolean ota_support;
    private WSDeviceBean device;
    private List<InstanceBean> instances;

    public boolean isOta_support() {
        return ota_support;
    }

    public void setOta_support(boolean ota_support) {
        this.ota_support = ota_support;
    }

    public WSDeviceBean getDevice() {
        return device;
    }

    public void setDevice(WSDeviceBean device) {
        this.device = device;
    }

    public List<InstanceBean> getInstances() {
        return instances;
    }

    public void setInstances(List<InstanceBean> instances) {
        this.instances = instances;
    }

    @Override
    public String toString() {
        return "WSDeviceResponseBean{" +
                "ota_support=" + ota_support +
                ", device=" + device +
                ", instances=" + instances +
                '}';
    }
}
