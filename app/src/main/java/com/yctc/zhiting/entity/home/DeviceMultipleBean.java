package com.yctc.zhiting.entity.home;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

public class DeviceMultipleBean implements MultiItemEntity, Serializable {

    public static final int DEVICE = 1;
    public static final int ADD = 2;


    private int itemType;

    /**
     * id : 1
     * name : 测试服's SA
     * logo_url : http://sa.zhitingtech.com/static/test-sa/img/%E6%99%BA%E6%85%A7%E4%B8%AD%E5%BF%83.png
     * plugin_id :
     * location_id : 0
     * location_name :
     * is_sa : true
     * plugin_url :
     * type :
     */

    private int id;
    private String name;
    private String logo_url;
    private String plugin_id;
    private int location_id;
    private String location_name;
    private boolean is_sa;
    private String plugin_url;
    private String type;
    private int area_id;
    private String sa_user_token;
    private boolean is_permit;//是否有有权限
    private String power="off";//on/off 打开、关闭状态
    private boolean online;//是否离线
    private String identity;//设备标识
    private int instance_id;
    private String control; // 本地加载相对路径

    public int getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(int instance_id) {
        this.instance_id = instance_id;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isIs_permit() {
        return is_permit;
    }

    public void setIs_permit(boolean is_permit) {
        this.is_permit = is_permit;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public DeviceMultipleBean() {
    }

    public DeviceMultipleBean(int itemType) {
        this.itemType = itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getPlugin_id() {
        return plugin_id;
    }

    public void setPlugin_id(String plugin_id) {
        this.plugin_id = plugin_id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public boolean isIs_sa() {
        return is_sa;
    }

    public void setIs_sa(boolean is_sa) {
        this.is_sa = is_sa;
    }

    public String getPlugin_url() {
        return plugin_url;
    }

    public void setPlugin_url(String plugin_url) {
        this.plugin_url = plugin_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public String getSa_user_token() {
        return sa_user_token;
    }

    public void setSa_user_token(String sa_user_token) {
        this.sa_user_token = sa_user_token;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }
}
