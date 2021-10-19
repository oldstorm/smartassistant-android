package com.yctc.zhiting.entity.home;

/**
 * date : 2021/5/2712:08
 * desc :
 */
public class SwitchBean {
    private String domain;//yeelight
    private int id;
    private String service;//switch
    private ServiceDataBean service_data;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
