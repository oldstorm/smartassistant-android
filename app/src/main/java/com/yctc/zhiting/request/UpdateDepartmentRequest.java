package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class UpdateDepartmentRequest extends Request {

    private String name;
    private int manager_id;

    public UpdateDepartmentRequest(String name, int manager_id) {
        this.name = name;
        this.manager_id = manager_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getManager_id() {
        return manager_id;
    }

    public void setManager_id(int manager_id) {
        this.manager_id = manager_id;
    }
}
