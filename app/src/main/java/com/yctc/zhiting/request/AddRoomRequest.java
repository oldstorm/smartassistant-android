package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class AddRoomRequest extends Request {
    private String name;

    public AddRoomRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
