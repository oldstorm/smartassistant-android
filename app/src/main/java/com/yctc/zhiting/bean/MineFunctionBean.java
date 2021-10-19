package com.yctc.zhiting.bean;

import androidx.annotation.DrawableRes;

public class MineFunctionBean {

    @DrawableRes
    private int logo;
    private String name;
    private boolean enable;

    public MineFunctionBean(int logo, String name) {
        this.logo = logo;
        this.name = name;
    }

    public MineFunctionBean(int logo, String name, boolean enable) {
        this.logo = logo;
        this.name = name;
        this.enable = enable;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
