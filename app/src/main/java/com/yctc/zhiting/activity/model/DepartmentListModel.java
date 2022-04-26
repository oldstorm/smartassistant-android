package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.DepartmentListContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.DepartmentListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;

public class DepartmentListModel implements DepartmentListContract.Model {

    @Override
    public void getDepartmentList(RequestDataCallback<DepartmentListBean> callback) {
        HTTPCaller.getInstance().get(DepartmentListBean.class, HttpUrlConfig.getDepartments(),callback);
    }

    @Override
    public void addDepartment(String name, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getDepartments(), name, callback);
    }

    @Override
    public void orderDepartment(String departments_id, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getDepartments(), departments_id, callback);
    }

    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions1(id), callback);
    }
}
