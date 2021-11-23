package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class BindCloudStrRequest extends Request {

    private String access_token;
//    private String cloud_area_id;
    private int cloud_user_id;

//    public String getCloud_area_id() {
//        return cloud_area_id;
//    }
//
//    public void setCloud_area_id(String cloud_area_id) {
//        this.cloud_area_id = cloud_area_id;
//    }

    public int getCloud_user_id() {
        return cloud_user_id;
    }

    public void setCloud_user_id(int cloud_user_id) {
        this.cloud_user_id = cloud_user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
