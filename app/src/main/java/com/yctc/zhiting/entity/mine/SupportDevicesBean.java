package com.yctc.zhiting.entity.mine;

import java.io.Serializable;

public class SupportDevicesBean implements Serializable {
    /**
     * logo_url : https://tysq2.yctc.tech/api/file/originals/id/2009071/fn/yeelight吸顶灯1.png
     * model : ceiling17
     * name : yeelight吸顶灯
     * actions : null
     */

    private String logo_url;
    private String model;
    private String name;
    private Object actions;

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getActions() {
        return actions;
    }

    public void setActions(Object actions) {
        this.actions = actions;
    }
}
