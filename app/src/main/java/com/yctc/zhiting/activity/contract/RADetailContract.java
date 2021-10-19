package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.DevicesBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RADetailBean;

/**
 * 房间/区域详情
 */
public interface RADetailContract {
    interface Model {
        void getDetail(int id, RequestDataCallback<RADetailBean> callback);
        void updateName(int id, String name, RequestDataCallback<Object> callback);
        void delRoom(int id, RequestDataCallback<Object> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
    }

    interface View extends BaseView {
        void getDataSuccess(RADetailBean raDetailBean);
        void updateNameSuccess();
        void delRoomSuccess();
        void requestFail(int errorCode, String msg);
        void getPermissionsSuccess(PermissionBean permissionBean);
    }

    interface Presenter extends BasePresenter<View> {
        void getDetail(int id);
        void updateName(int id, String name);
        void delRoom(int id);
        void getPermissions(int id);
    }
}
