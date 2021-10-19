package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.RoomListBean;

public interface SceneDetailDeviceContract {
    interface Model {
        void getRoomList(RequestDataCallback<RoomListBean> callback);
        void getDeviceList(RequestDataCallback<RoomDeviceListBean> callback);
    }

    interface View extends BaseView {
        void getDeviceListSuccess(RoomDeviceListBean roomListBean);
        void getRoomListSuccess(RoomListBean roomListBean);
        void getDeviceFail(int errorCode, String msg);
        void requestFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getDeviceList();
        void getRoomList();
    }

}
