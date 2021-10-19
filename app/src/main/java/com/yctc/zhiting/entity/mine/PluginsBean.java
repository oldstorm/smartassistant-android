package com.yctc.zhiting.entity.mine;

import java.io.Serializable;
import java.util.List;

public class PluginsBean implements Serializable {
    /**
     * id : light_001
     * name : light
     * logo_url : html/static/img/yeelight.jpg
     * version : 0.0.1
     * brand : yeelight
     * info : 测试插件
     * download_url : https://test.yctc.tech/api/file/originals/id/2009325/fn/yeelight-plugin.zip
     * visit_url : www.baidu.com
     * support_devices : [{"logo_url":"https://tysq2.yctc.tech/api/file/originals/id/2009071/fn/yeelight吸顶灯1.png","model":"ceiling17","name":"yeelight吸顶灯","actions":null},{"logo_url":"https://tysq1.yctc.tech/api/file/originals/id/2009070/fn/yeelight台灯.png","model":"lamp9","name":"yeelight台灯","actions":null}]
     * is_added : true
     * is_newest : true
     */


    private long pid; // 发送websocket用的id

    private String id;
    private String name;
    private String logo_url;
    private String version;
    private String brand;
    private String info;
    private String download_url;
    private String visit_url;
    private boolean is_added;
    private boolean is_newest;
    private boolean updating; // 是否在更新中
    private List<SupportDevicesBean> support_devices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getVisit_url() {
        return visit_url;
    }

    public void setVisit_url(String visit_url) {
        this.visit_url = visit_url;
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

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    public List<SupportDevicesBean> getSupport_devices() {
        return support_devices;
    }

    public void setSupport_devices(List<SupportDevicesBean> support_devices) {
        this.support_devices = support_devices;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }
}
