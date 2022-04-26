package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;

/**
 * 家庭/公司详情
 */
public interface RoomAreaContract {
    interface Model {
        void getRoomList(RequestDataCallback<RoomListBean> callback);
        void addRoom(String name, RequestDataCallback<Object> callback);
        void orderRoom(String locations_id, RequestDataCallback<Object> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
    }

    interface View extends BaseView {
        void getRoomListSuccess(RoomListBean roomListBean);
        void addRoomSuccess();
        void orderRoomSuccess();
        void getPermissionsSuccess(PermissionBean permissionBean);
        void requestFail(int errorCode, String msg);
        void orderFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getRoomList(boolean showLoading);
        void addRoom(String name);
        void orderRoom(String locations_id);
        void getPermissions(int id);
    }
}
