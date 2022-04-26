package com.yctc.zhiting.entity.mine;

import java.util.List;

public class InvitationCodePost {


    /**
     * role_ids : [1,2]
     * area_id : 1
     */

    private long area_id;
    private List<Integer> role_ids;
    private List<Integer> department_ids;

    public List<Integer> getDepartment_ids() {
        return department_ids;
    }

    public void setDepartment_ids(List<Integer> department_ids) {
        this.department_ids = department_ids;
    }

    public long getArea_id() {
        return area_id;
    }

    public void setArea_id(long area_id) {
        this.area_id = area_id;
    }

    public List<Integer> getRole_ids() {
        return role_ids;
    }

    public void setRole_ids(List<Integer> role_ids) {
        this.role_ids = role_ids;
    }
}
