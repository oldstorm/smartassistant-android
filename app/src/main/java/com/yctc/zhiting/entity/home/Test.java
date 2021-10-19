package com.yctc.zhiting.entity.home;

import com.google.gson.annotations.SerializedName;

public class Test {

    /**
     * title : 标题名称
     * color : #333333
     * rgb值     background : #ffffff
     * isShow : true
     */

    private String title;
    private String color;
    private String background;
    private boolean isShow;

    public Test(String title, String color, String background, boolean isShow) {
        this.title = title;
        this.color = color;
        this.background = background;
        this.isShow = isShow;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }


    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
