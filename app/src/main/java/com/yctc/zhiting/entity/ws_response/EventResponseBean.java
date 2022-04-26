package com.yctc.zhiting.entity.ws_response;

public class EventResponseBean {

    private String plugin_id;
    private AttributesBean attr;

    public String getPlugin_id() {
        return plugin_id;
    }

    public void setPlugin_id(String plugin_id) {
        this.plugin_id = plugin_id;
    }

    public AttributesBean getAttr() {
        return attr;
    }

    public void setAttr(AttributesBean attr) {
        this.attr = attr;
    }
}
