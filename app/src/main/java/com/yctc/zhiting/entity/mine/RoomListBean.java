package com.yctc.zhiting.entity.mine;

import java.util.List;

public class RoomListBean {


    private List<LocationBean> locations;
    private List<LocationBean> departments;

    public List<LocationBean> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationBean> locations) {
        this.locations = locations;
    }

    public List<LocationBean> getDepartments() {
        return departments;
    }

    public void setDepartments(List<LocationBean> departments) {
        this.departments = departments;
    }
}
