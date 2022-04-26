package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.DepartmentListContract;
import com.yctc.zhiting.activity.model.DepartmentListModel;
import com.yctc.zhiting.entity.DepartmentListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;

public class DepartmentListPresenter extends BasePresenterImpl<DepartmentListContract.View> implements DepartmentListContract.Presenter {

    private DepartmentListModel model;

    @Override
    public void init() {
        model = new DepartmentListModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 部门列表
     * @param showLoading
     */
    @Override
    public void getDepartmentList(boolean showLoading) {
        if (showLoading)
            mView.showLoadingView();
        model.getDepartmentList(new RequestDataCallback<DepartmentListBean>() {
            @Override
            public void onSuccess(DepartmentListBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getDepartmentListSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getDepartmentListFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 添加部门
     * @param name
     */
    @Override
    public void addDepartment(String name) {
        mView.showLoadingView();
        String body = "{\"name\":\""+ name+"\"}";
        model.addDepartment(body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.addDepartmentSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.addDepartmentFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 排序部门
     * @param departments_id
     */
    @Override
    public void orderDepartment(String departments_id) {
        mView.showLoadingView();
        model.orderDepartment(departments_id, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.orderDepartmentSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.orderDepartmentFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 获取权限
     * @param id
     */
    @Override
    public void getPermissions(int id) {
        model.getPermissions(id, new RequestDataCallback<PermissionBean>() {
            @Override
            public void onSuccess(PermissionBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getPermissionsSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getPermissionsFail(errorCode, errorMessage);
                }
            }
        });
    }
}
