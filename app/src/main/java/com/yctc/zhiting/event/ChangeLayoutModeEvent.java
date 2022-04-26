package com.yctc.zhiting.event;


/**
 * Author by Ouyangle, Date on 2022/4/2.
 * PS: Not easy to write code, please indicate.
 */
public class ChangeLayoutModeEvent {
    private boolean isLinear = false;

    public ChangeLayoutModeEvent(boolean isLinear) {
        this.isLinear = isLinear;
    }

    public boolean isLinear() {
        return isLinear;
    }

    public void setLinear(boolean linear) {
        isLinear = linear;
    }
}
