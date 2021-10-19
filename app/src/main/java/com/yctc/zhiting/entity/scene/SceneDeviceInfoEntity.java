package com.yctc.zhiting.entity.scene;

import java.io.Serializable;

public class SceneDeviceInfoEntity implements Serializable {

    private String name; // 设备名称
    private String location_name; // 设备所在房间/区域名称
    private String logo_url; // 设备图片
    private int status; // 设备状态:1为正常;2为被删除;3为离线

    public SceneDeviceInfoEntity() {
    }

    public SceneDeviceInfoEntity(String name, String location_name, String logo_url) {
        this.name = name;
        this.location_name = location_name;
        this.logo_url = logo_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
