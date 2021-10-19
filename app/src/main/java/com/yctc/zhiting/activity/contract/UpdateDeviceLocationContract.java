package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.entity.mine.AreasBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.request.AddRoomRequest;


/**
 * 修改设备位置
 */
public interface UpdateDeviceLocationContract {
    interface Model {
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
        void getAreaList(RequestDataCallback<AreasBean> callback);
        void addAreaRoom(AddRoomRequest request, RequestDataCallback<AreasBean> callback);
        void updateDeviceName(String deviceId, Request request, RequestDataCallback<String> callback);
    }

    interface View extends BaseView {
        void getPermissionsSuccess(PermissionBean permissionBean);
        void getFail(int errorCode, String msg);


        void onAreasSuccess(AreasBean data);

        void onAreasFail(int errorCode, String msg);

        void onAddAreaRoomSuccess(AreasBean data);

        void onAddAreaRoomFail(int errorCode, String msg);

        void onUpdateDeviceNameSuccess();

        void onUpdateDeviceNameFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getPermissions(int id);
        void getAreaList();

        void addAreaRoom(AddRoomRequest name);

        void updateDeviceName(String deviceId,Request request);
    }
}
