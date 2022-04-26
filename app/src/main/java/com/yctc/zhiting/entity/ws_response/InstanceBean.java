package com.yctc.zhiting.entity.ws_response;

import java.io.Serializable;
import java.util.List;

public class InstanceBean implements Serializable {

    private String iid;
    private List<ServicesBean> services;

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public List<ServicesBean> getServices() {
        return services;
    }

    public void setServices(List<ServicesBean> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "InstanceBean{" +
                "iid='" + iid + '\'' +
                ", services=" + services +
                '}';
    }
}
