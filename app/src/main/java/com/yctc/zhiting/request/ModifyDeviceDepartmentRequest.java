package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class ModifyDeviceDepartmentRequest extends Request {
    private String name;
    private int department_id;

    public ModifyDeviceDepartmentRequest(String name, int department_id) {
        this.name = name;
        this.department_id = department_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }
}
