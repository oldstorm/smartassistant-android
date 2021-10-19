package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class BindCloudRequest extends Request {

    private int cloud_area_id;
    private int cloud_user_id;

    public BindCloudRequest() {
    }

    public BindCloudRequest(int cloud_area_id, int cloud_user_id) {
        this.cloud_area_id = cloud_area_id;
        this.cloud_user_id = cloud_user_id;
    }

    public int getCloud_area_id() {
        return cloud_area_id;
    }

    public void setCloud_area_id(int cloud_area_id) {
        this.cloud_area_id = cloud_area_id;
    }

    public int getCloud_user_id() {
        return cloud_user_id;
    }

    public void setCloud_user_id(int cloud_user_id) {
        this.cloud_user_id = cloud_user_id;
    }
}
