package com.yctc.zhiting.event;

public class UpdateNameEvent {

    private String name;

    public UpdateNameEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
