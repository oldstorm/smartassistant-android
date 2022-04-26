package com.yctc.zhiting.entity.mine;

public class DepartmentInfoBean {

    private int id;
    private String name;
    private boolean is_manager;

    private int pid; // 父级部门id，如果是1级部门就是0

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIs_manager() {
        return is_manager;
    }

    public void setIs_manager(boolean is_manager) {
        this.is_manager = is_manager;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
