package com.yctc.zhiting.entity.mine;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class FeedbackPictureBean implements MultiItemEntity {

    public static final int PICTURE = 1;
    public static final int ADD = 2;

    private int itemType;
    private String url;

    public FeedbackPictureBean(int itemType) {
        this.itemType = itemType;
    }

    public FeedbackPictureBean(int itemType, String url) {
        this.itemType = itemType;
        this.url = url;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
