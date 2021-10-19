package com.yctc.zhiting.event;

import com.yctc.zhiting.entity.mine.PermissionBean;

public class PermissionEvent {
    private PermissionBean.PermissionsBean permissions;

    public PermissionEvent(PermissionBean.PermissionsBean permissions) {
        this.permissions = permissions;
    }

    public PermissionBean.PermissionsBean getPermissions() {
        return permissions;
    }

    public void setPermissions(PermissionBean.PermissionsBean permissions) {
        this.permissions = permissions;
    }
}
