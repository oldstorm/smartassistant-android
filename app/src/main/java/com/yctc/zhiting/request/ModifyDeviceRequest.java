package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.config.Constant;

public class ModifyDeviceRequest extends Request {
    private String name;
    private int location_id;
    private int department_id;

    public ModifyDeviceRequest(int location_id) {
        this.location_id = location_id;
    }

    public ModifyDeviceRequest(String name, int location_id) {
        this.name = name;
        if (Constant.AREA_TYPE == Constant.COMPANY_MODE) {
            this.department_id= location_id;
        } else {
            this.location_id = location_id;
        }
    }
}
