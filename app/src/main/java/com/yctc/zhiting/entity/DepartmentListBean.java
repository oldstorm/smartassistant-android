package com.yctc.zhiting.entity;

import com.yctc.zhiting.entity.mine.LocationBean;

import java.util.List;

public class DepartmentListBean {

    private List<LocationBean> departments;

    public List<LocationBean> getDepartments() {
        return departments;
    }

    public void setDepartments(List<LocationBean> departments) {
        this.departments = departments;
    }
}
