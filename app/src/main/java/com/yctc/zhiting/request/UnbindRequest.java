package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class UnbindRequest extends Request {
    private String area_id;
    private Integer app_id;

    public UnbindRequest(String area_id, Integer app_id) {
        this.area_id = area_id;
        this.app_id = app_id;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public Integer getApp_id() {
        return app_id;
    }

    public void setApp_id(Integer app_id) {
        this.app_id = app_id;
    }
}
