package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.DepartmentDetail;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.UserBean;

public interface DepartmentDetailContract {
    interface Model {
        void getDepartmentDetail(int id, RequestDataCallback<DepartmentDetail> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
    }

    interface View extends BaseView {
        void getDepartmentDetailSuccess(DepartmentDetail userBean);
        void getDepartmentDetailFail(int errorCode, String msg);
        void getPermissionsSuccess(PermissionBean permissionBean);
        void getPermissionFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getDepartmentDetail(int id);
        void getPermissions(int id);
    }
}
