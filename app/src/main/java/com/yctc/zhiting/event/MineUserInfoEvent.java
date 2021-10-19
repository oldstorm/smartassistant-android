package com.yctc.zhiting.event;

public class MineUserInfoEvent {

    private boolean updateSAUserName;

    public MineUserInfoEvent() {
    }

    public MineUserInfoEvent(boolean updateSAUserName) {
        this.updateSAUserName = updateSAUserName;
    }

    public boolean isUpdateSAUserName() {
        return updateSAUserName;
    }

    public void setUpdateSAUserName(boolean updateSAUserName) {
        this.updateSAUserName = updateSAUserName;
    }
}
