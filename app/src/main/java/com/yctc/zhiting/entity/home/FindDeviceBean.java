package com.yctc.zhiting.entity.home;

import com.google.gson.annotations.SerializedName;

/**
 * date : 2021/5/2411:55
 * desc :
 */
public class FindDeviceBean {

    @SerializedName("domain")
    private String domain;
    @SerializedName("id")
    private long id;
    @SerializedName("service")
    private String service;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}

