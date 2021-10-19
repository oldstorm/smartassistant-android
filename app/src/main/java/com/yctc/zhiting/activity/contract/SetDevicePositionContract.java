package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.entity.mine.AreasBean;
import com.yctc.zhiting.request.AddRoomRequest;

public interface SetDevicePositionContract {
    interface Model {
        void getAreaList(RequestDataCallback<AreasBean> callback);
        void addAreaRoom(AddRoomRequest request, RequestDataCallback<AreasBean> callback);
        void updateDeviceName(String deviceId,Request request, RequestDataCallback<String> callback);
    }

    interface View extends BaseView {
        void onAreasSuccess(AreasBean data);

        void onAreasFail(int errorCode, String msg);

        void onAddAreaRoomSuccess(AreasBean data);

        void onAddAreaRoomFail(int errorCode, String msg);

        void onUpdateDeviceNameSuccess();

        void onUpdateDeviceNameFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getAreaList();

        void addAreaRoom(AddRoomRequest name);

        void updateDeviceName(String deviceId,Request request);
    }
}
