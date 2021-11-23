package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class BooleanRequest extends Request {

    private boolean is_del_cloud_disk;

    public BooleanRequest(boolean is_del_cloud_disk) {
        this.is_del_cloud_disk = is_del_cloud_disk;
    }

    public boolean isIs_del_cloud_disk() {
        return is_del_cloud_disk;
    }

    public void setIs_del_cloud_disk(boolean is_del_cloud_disk) {
        this.is_del_cloud_disk = is_del_cloud_disk;
    }
}
