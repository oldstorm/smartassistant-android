package com.yctc.zhiting.entity;

import com.yctc.zhiting.entity.scene.SceneConditionAttrEntity;

import java.util.List;

/**
 * 设备详情（重构的数据）
 */
public class DeviceDetailEntity {

    private String name;
    private Integer id;
    private String logo_url;
    private String model;
    private AreaAndLocationBean area;
    private AreaAndLocationBean location;
    private PluginBean plugin;
    private List<SceneConditionAttrEntity> attributes;
    private PermissionsBean permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public AreaAndLocationBean getArea() {
        return area;
    }

    public void setArea(AreaAndLocationBean area) {
        this.area = area;
    }

    public AreaAndLocationBean getLocation() {
        return location;
    }

    public void setLocation(AreaAndLocationBean location) {
        this.location = location;
    }

    public PluginBean getPlugin() {
        return plugin;
    }

    public void setPlugin(PluginBean plugin) {
        this.plugin = plugin;
    }

    public List<SceneConditionAttrEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SceneConditionAttrEntity> attributes) {
        this.attributes = attributes;
    }

    public PermissionsBean getPermissions() {
        return permissions;
    }

    public void setPermissions(PermissionsBean permissions) {
        this.permissions = permissions;
    }

    public static class AreaAndLocationBean{

        private String name;
        private Integer id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public static class PluginBean{
        private String name;
        private String id;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class PermissionsBean {
        /**
         * delete_device : true
         * update_device : true
         */

        private boolean delete_device;
        private boolean update_device;

        public boolean isDelete_device() {
            return delete_device;
        }

        public void setDelete_device(boolean delete_device) {
            this.delete_device = delete_device;
        }

        public boolean isUpdate_device() {
            return update_device;
        }

        public void setUpdate_device(boolean update_device) {
            this.update_device = update_device;
        }
    }
}
