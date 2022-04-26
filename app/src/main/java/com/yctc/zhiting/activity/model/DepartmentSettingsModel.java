package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.DepartmentSettingsContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.DepartmentDetail;
import com.yctc.zhiting.entity.mine.PermissionBean;

public class DepartmentSettingsModel implements DepartmentSettingsContract.Model {
    @Override
    public void getDepartmentDetail(int id, RequestDataCallback<DepartmentDetail> callback) {
        HTTPCaller.getInstance().get(DepartmentDetail.class, HttpUrlConfig.getDepartments()+"/"+id, callback);
    }

    /**
     * 获取用户权限
     *
     * @param id
     * @param callback
     */
    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions1(id), callback);
    }

    /**
     * 修改部门
     * @param id
     * @param json
     * @param callback
     */
    @Override
    public void updateDepartment(int id, String json, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getDepartments()+"/"+id, json, callback);
    }

    /**
     * 删除部门
     * @param id
     * @param callback
     */
    @Override
    public void delDepartment(int id, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().delete(Object.class, HttpUrlConfig.getDepartments()+"/"+id, "", callback);
    }
}
