package com.yctc.zhiting.event;

public class HomeSelectedEvent {

    private String name;
    private boolean load;//是否请求数据

    public HomeSelectedEvent(boolean load) {
        this.load = load;
    }

    public boolean isLoad() {
        return load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public HomeSelectedEvent(String name) {
        this.name = name;
    }

    public HomeSelectedEvent() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
