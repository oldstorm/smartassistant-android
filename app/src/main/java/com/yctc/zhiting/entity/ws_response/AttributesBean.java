package com.yctc.zhiting.entity.ws_response;

import java.io.Serializable;

public class AttributesBean implements Serializable {


    /**
     * aid : 1
     * type : on_off
     * val_type :
     * permission : 0
     * val : null
     */

    private String iid;
    private int aid;
    private String type;
    private String val_type;
    private Integer permission;
    private Object val;

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVal_type() {
        return val_type;
    }

    public void setVal_type(String val_type) {
        this.val_type = val_type;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "AttributesBean{" +
                "iid='" + iid + '\'' +
                ", aid=" + aid +
                ", type='" + type + '\'' +
                ", val_type='" + val_type + '\'' +
                ", permission=" + permission +
                ", val=" + val +
                '}';
    }
}
