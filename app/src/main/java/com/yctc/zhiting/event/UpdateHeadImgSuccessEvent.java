package com.yctc.zhiting.event;

public class UpdateHeadImgSuccessEvent {

    private String headImgUrl;

    public UpdateHeadImgSuccessEvent(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }
}
