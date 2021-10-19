package com.yctc.zhiting.entity.home;

public class DirectBean {
    private int device_id;//设备id
    private DirectType type;

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public DirectType getType() {
        return type;
    }

    public void setType(DirectType type) {
        this.type = type;
    }

    public enum DirectType {
        FUNCTION,//设备功能
        INFO, //设备信息
        SWITCH;//开关切换
    }
}
