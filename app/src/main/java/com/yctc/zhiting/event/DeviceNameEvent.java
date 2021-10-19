package com.yctc.zhiting.event;

public class DeviceNameEvent {

    private String name;

    public DeviceNameEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
