package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;

public class BrandBeanItem extends BaseEntity {
    private String id;
    private String logo_url;
    private String name;
    private int plugin_amount;
    private boolean is_added;//是否添加
    private boolean is_newest;//是否最新

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlugin_amount() {
        return plugin_amount;
    }

    public void setPlugin_amount(int plugin_amount) {
        this.plugin_amount = plugin_amount;
    }

    public boolean isIs_added() {
        return is_added;
    }

    public void setIs_added(boolean is_added) {
        this.is_added = is_added;
    }

    public boolean isIs_newest() {
        return is_newest;
    }

    public void setIs_newest(boolean is_newest) {
        this.is_newest = is_newest;
    }
}
