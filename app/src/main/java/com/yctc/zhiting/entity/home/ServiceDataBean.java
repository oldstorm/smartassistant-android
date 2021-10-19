package com.yctc.zhiting.entity.home;

/**
 * date : 2021/5/2712:16
 * desc :
 */
public class ServiceDataBean {
    private int device_id;//device_id
    private String power;//on/off/toggle

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }
}
