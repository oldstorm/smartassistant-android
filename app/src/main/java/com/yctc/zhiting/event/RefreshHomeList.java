package com.yctc.zhiting.event;

/**
 * 仅仅更新列表
 */
public class RefreshHomeList {
    private String name;

    public RefreshHomeList() {
    }

    public RefreshHomeList(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
