package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceBean extends BaseEntity {
    private int device_id;
    private String plugin_url;
    private UserInfo user_info;
    @SerializedName("address")
    private String address;
    @SerializedName("identity")//设备唯一值
    private String identity;
    @SerializedName("manufacturer")//制造商
    private String manufacturer;
    @SerializedName("model")//设备型号
    private String model;
    @SerializedName("name")
    private String name;
    @SerializedName("plugin_id")
    private String pluginId;
    @SerializedName("sw_version")//软件版本
    private String swVersion;
    @SerializedName("type")//设备类型，如：light,switch,sa...
    private String type;
    @SerializedName("logo_url")//设备logo（未定）
    private String logoUrl;
    @SerializedName("bind")//如果是SA，是否已经绑定了用户
    private boolean bind;
    @SerializedName("brand_id")
    private String brandId;
    @SerializedName("ip")
    private String ip;
    @SerializedName("port")
    private String port;

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) {
        this.bind = bind;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getSwVersion() {
        return swVersion;
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public String getPlugin_url() {
        return plugin_url;
    }

    public void setPlugin_url(String plugin_url) {
        this.plugin_url = plugin_url;
    }

    public UserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfo user_info) {
        this.user_info = user_info;
    }

    class UserInfo extends BaseEntity {
        private String user_id;
        private String account_name;
        private String nick_name;
        private String token;
        private String phone;
        private String is_set_password;
    }
}
