package com.yctc.zhiting.entity.home;

import android.bluetooth.BluetoothDevice;

import com.app.main.framework.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;
import com.yctc.zhiting.entity.ws_request.WSAuthParamsBean;

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
    @SerializedName("sa_id")
    private String sa_id;
    private int area_type; // 添加sa设备时，需要该sa表示该家庭/公司的mode
    private String iid;
    private boolean auth_required;

    private List<WSAuthParamsBean> auth_params; // 里面的type为homekit需要跳到设置pin码界面

    transient BluetoothDevice bluetoothDevice;

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

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getSa_id() {
        return sa_id;
    }

    public void setSa_id(String sa_id) {
        this.sa_id = sa_id;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public int getArea_type() {
        return area_type;
    }

    public void setArea_type(int area_type) {
        this.area_type = area_type;
    }

    public boolean isAuth_required() {
        return auth_required;
    }

    public void setAuth_required(boolean auth_required) {
        this.auth_required = auth_required;
    }

    public List<WSAuthParamsBean> getAuth_params() {
        return auth_params;
    }

    public void setAuth_params(List<WSAuthParamsBean> auth_params) {
        this.auth_params = auth_params;
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
