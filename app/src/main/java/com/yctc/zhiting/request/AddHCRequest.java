package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

import java.util.List;

public class AddHCRequest extends Request {

    /**
     * name : id
     * location_names : ["nostrud non","consectetur nostrud","pariatur adipisicing laboris fugiat ","eu voluptate ad","dolor dolore et in"]
     */

    private String name;
    private int area_type;
    private List<String> location_names;
    private List<String> department_names;

    public AddHCRequest(String name, int area_type) {
        this.name = name;
        this.area_type = area_type;
    }

    public AddHCRequest(String name, List<String> location_names) {
        this.name = name;
        this.location_names = location_names;
    }

    public AddHCRequest(String name, int area_type, List<String> location_names) {
        this.name = name;
        this.area_type = area_type;
        if (area_type == 1) {
            this.location_names = location_names;
        } else {
            this.department_names = location_names;
        }
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

    public int getArea_type() {
        return area_type;
    }

    public void setArea_type(int area_type) {
        this.area_type = area_type;
    }

    public List<String> getDepartment_names() {
        return department_names;
    }

    public void setDepartment_names(List<String> department_names) {
        this.department_names = department_names;
    }
}
