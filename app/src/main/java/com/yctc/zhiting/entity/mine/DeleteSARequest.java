package com.yctc.zhiting.entity.mine;

import com.app.main.framework.httputil.request.Request;

public class DeleteSARequest extends Request {
    private boolean is_migration_sa;//是否创建云端家庭
    private boolean is_del_cloud_disk;//是否删除网盘数据
    private String  cloud_area_id;//云端家庭id， 如果是要创建云端家庭则必须
    private String  cloud_access_token;//云端家庭token，如果是要创建云端家庭则必须

    public DeleteSARequest(boolean is_mirgation_sa, boolean is_del_cloud_disk) {
        this.is_migration_sa = is_mirgation_sa;
        this.is_del_cloud_disk = is_del_cloud_disk;
    }

    public String getCloud_area_id() {
        return cloud_area_id;
    }

    public void setCloud_area_id(String cloud_area_id) {
        this.cloud_area_id = cloud_area_id;
    }

    public String getCloud_access_token() {
        return cloud_access_token;
    }

    public void setCloud_access_token(String cloud_access_token) {
        this.cloud_access_token = cloud_access_token;
    }

    public boolean isIs_mirgation_sa() {
        return is_migration_sa;
    }

    public void setIs_mirgation_sa(boolean is_mirgation_sa) {
        this.is_migration_sa = is_mirgation_sa;
    }

    public boolean isIs_del_cloud_disk() {
        return is_del_cloud_disk;
    }

    public void setIs_del_cloud_disk(boolean is_del_cloud_disk) {
        this.is_del_cloud_disk = is_del_cloud_disk;
    }
}
