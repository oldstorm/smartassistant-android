package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.DepartmentListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;

public interface DepartmentListContract {
    interface Model {
        void getDepartmentList(RequestDataCallback<DepartmentListBean> callback);
        void addDepartment(String name, RequestDataCallback<Object> callback);
        void orderDepartment(String departments_id, RequestDataCallback<Object> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
    }

    interface View extends BaseView {
        void getDepartmentListSuccess(DepartmentListBean roomListBean);
        void getDepartmentListFail(int errorCode, String msg);
        void addDepartmentSuccess();
        void addDepartmentFail(int errorCode, String msg);
        void orderDepartmentSuccess();
        void orderDepartmentFail(int errorCode, String msg);
        void getPermissionsSuccess(PermissionBean permissionBean);
        void getPermissionsFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getDepartmentList(boolean showLoading);
        void addDepartment(String name);
        void orderDepartment(String departments_id);
        void getPermissions(int id);
    }
}
