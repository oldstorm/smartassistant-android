package com.yctc.zhiting.request;

public class GetDeviceInfoRequest {


    /**
     * id : xxx
     * method : get_prop.info
     */

    private long id;
    private String method;

    public GetDeviceInfoRequest(long id, String method) {
        this.id = id;
        this.method = method;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
