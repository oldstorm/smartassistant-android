package com.yctc.zhiting.entity.mine;

import com.app.main.framework.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class AreasBean extends BaseEntity implements Serializable {
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
