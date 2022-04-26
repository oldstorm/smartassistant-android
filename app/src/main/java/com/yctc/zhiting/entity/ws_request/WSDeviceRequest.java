package com.yctc.zhiting.entity.ws_request;

public class WSDeviceRequest {

    private String iid;
    private WSAuthParamsBean auth_params;

    public WSDeviceRequest() {
    }

    public WSDeviceRequest(String iid) {
        this.iid = iid;
    }

    public WSDeviceRequest(String iid, WSAuthParamsBean auth_params) {
        this.iid = iid;
        this.auth_params = auth_params;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public WSAuthParamsBean getAuth_params() {
        return auth_params;
    }

    public void setAuth_params(WSAuthParamsBean auth_params) {
        this.auth_params = auth_params;
    }
}
