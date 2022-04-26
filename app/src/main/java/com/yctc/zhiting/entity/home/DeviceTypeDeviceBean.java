package com.yctc.zhiting.entity.home;

import java.io.Serializable;

/**
 * 设备分类下的设备
 */
public class DeviceTypeDeviceBean implements Serializable {

    /**
     * name : sint nostrud velit
     * model : consectetu
     * manufacturer : dolor consectetur eu ea anim
     * logo : nisi elit et fugiat
     * provisioning : qui dolore minim
     * plugin_id : dolore sit Lorem nulla
     */

    private String name;
    private String model;
    private String manufacturer;
    private String logo;
    private String provisioning;
    private String plugin_id;
    private String protocol;

    public DeviceTypeDeviceBean() {
    }

    public DeviceTypeDeviceBean(String name, String model, String manufacturer, String logo, String provisioning, String plugin_id) {
        this.name = name;
        this.model = model;
        this.manufacturer = manufacturer;
        this.logo = logo;
        this.provisioning = provisioning;
        this.plugin_id = plugin_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getProvisioning() {
        return provisioning;
    }

    public void setProvisioning(String provisioning) {
        this.provisioning = provisioning;
    }

    public String getPlugin_id() {
        return plugin_id;
    }

    public void setPlugin_id(String plugin_id) {
        this.plugin_id = plugin_id;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
