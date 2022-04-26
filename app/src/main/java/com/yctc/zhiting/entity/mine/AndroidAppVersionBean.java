package com.yctc.zhiting.entity.mine;

import com.app.main.framework.httputil.request.Request;

public class AndroidAppVersionBean extends Request {
    private String min_app_version;//最低app版本, 小于最低app版本时需要强制更新, 为空字符串时不需要强制更新
    private String max_app_version;//最高app版本
    private String remark;//更新内容
    private String link;//apk下载地址
    private int update_type;//更新类型 0不需操作 1普通更新 2强制更新（is_force_update==true）
    private boolean is_force_update;//是否强制更新

    public boolean isIs_force_update() {
        return is_force_update;
    }

    public void setIs_force_update(boolean is_force_update) {
        this.is_force_update = is_force_update;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getUpdate_type() {
        return update_type;
    }

    public void setUpdate_type(int update_type) {
        this.update_type = update_type;
    }

    public String getMin_app_version() {
        return min_app_version;
    }

    public void setMin_app_version(String min_app_version) {
        this.min_app_version = min_app_version;
    }

    public String getMax_app_version() {
        return max_app_version;
    }

    public void setMax_app_version(String max_app_version) {
        this.max_app_version = max_app_version;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
