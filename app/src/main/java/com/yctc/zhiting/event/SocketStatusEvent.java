package com.yctc.zhiting.event;

/**
 * 智慧中心连接状态
 */
public class SocketStatusEvent {

    private boolean showTip;//socket是否连接中

    public SocketStatusEvent(boolean showTip) {
        this.showTip = showTip;
    }

    public boolean isShowTip() {
        return showTip;
    }
}
