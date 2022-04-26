package com.yctc.zhiting.event;

/**
 * Author by Ouyangle, Date on 2022/4/7.
 * PS: Not easy to write code, please indicate.
 */
public class DevicesVisibleEvent {
    private boolean isGoOffLineDevices = false;

    public boolean isGoOffLineDevices() {
        return isGoOffLineDevices;
    }

    public void setGoOffLineDevices(boolean goOffLineDevices) {
        isGoOffLineDevices = goOffLineDevices;
    }

    public DevicesVisibleEvent(boolean isGoOffLineDevices) {
        this.isGoOffLineDevices = isGoOffLineDevices;
    }
}
