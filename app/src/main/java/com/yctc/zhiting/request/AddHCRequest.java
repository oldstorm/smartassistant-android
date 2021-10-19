package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

import java.util.List;

public class AddHCRequest extends Request {

    /**
     * name : id
     * location_names : ["nostrud non","consectetur nostrud","pariatur adipisicing laboris fugiat ","eu voluptate ad","dolor dolore et in"]
     */

    private String name;
    private List<String> location_names;

    public AddHCRequest(String name, List<String> location_names) {
        this.name = name;
        this.location_names = location_names;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLocation_names() {
        return location_names;
    }

    public void setLocation_names(List<String> location_names) {
        this.location_names = location_names;
    }
}
