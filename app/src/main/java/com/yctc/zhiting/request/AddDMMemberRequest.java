package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

import java.util.List;

public class AddDMMemberRequest extends Request {

    private List<Integer> users;

    public AddDMMemberRequest(List<Integer> users) {
        this.users = users;
    }

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }
}
