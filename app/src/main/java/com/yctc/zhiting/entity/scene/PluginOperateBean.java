package com.yctc.zhiting.entity.scene;

public class PluginOperateBean {

    /**
     * id : 1
     * domain : plugin
     * service : install
     * service_data : {"plugin_id":"plugin_id"}
     */

    private long id;
    private String domain;
    private String service;
    private ServiceDataBean service_data;

    public PluginOperateBean(int id, String domain) {
        this.id = id;
        this.domain = domain;
    }

    public PluginOperateBean(long id, String domain, String service, ServiceDataBean service_data) {
        this.id = id;
        this.domain = domain;
        this.service = service;
        this.service_data = service_data;
    }

    public PluginOperateBean(long id, String domain, ServiceDataBean service_data) {
        this.id = id;
        this.domain = domain;
        this.service_data = service_data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public ServiceDataBean getService_data() {
        return service_data;
    }

    public void setService_data(ServiceDataBean service_data) {
        this.service_data = service_data;
    }

    public static class ServiceDataBean {
        /**
         * plugin_id : plugin_id
         */

        private String plugin_id;

        public ServiceDataBean(String plugin_id) {
            this.plugin_id = plugin_id;
        }

        public String getPlugin_id() {
            return plugin_id;
        }

        public void setPlugin_id(String plugin_id) {
            this.plugin_id = plugin_id;
        }
    }
}
