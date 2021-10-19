package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class ModifyDeviceRequest extends Request {
    private String name;
    private int location_id;

    public ModifyDeviceRequest(int location_id) {
        this.location_id = location_id;
    }

    public ModifyDeviceRequest(String name, int location_id) {
        this.name = name;
        this.location_id = location_id;
    }
}
