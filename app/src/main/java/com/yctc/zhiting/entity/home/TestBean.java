package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;

public class TestBean extends BaseEntity {
    private boolean open;
    private String name;
    private int device_type;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDevice_type() {
        return device_type;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }
}
