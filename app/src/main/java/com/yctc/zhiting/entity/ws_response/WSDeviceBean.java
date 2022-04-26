package com.yctc.zhiting.entity.ws_response;

import java.io.Serializable;

public class WSDeviceBean implements Serializable {

    /**
     * id : 1
     * model : model
     * plugin_id : 1
     * plugin_url : http://127.0.0.1/index.html
     */

    private int id;
    private String model;
    private String plugin_id;
    private String plugin_url;
    private String control;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlugin_id() {
        return plugin_id;
    }

    public void setPlugin_id(String plugin_id) {
        this.plugin_id = plugin_id;
    }

    public String getPlugin_url() {
        return plugin_url;
    }

    public void setPlugin_url(String plugin_url) {
        this.plugin_url = plugin_url;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }
}
