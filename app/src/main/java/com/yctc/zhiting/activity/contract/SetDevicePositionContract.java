package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.mine.AreasBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.request.AddRoomRequest;

public interface SetDevicePositionContract {
    interface Model {
        void getAreaList(RequestDataCallback<AreasBean> callback);

        void addAreaRoom(AddRoomRequest request, RequestDataCallback<AreasBean> callback);

        void updateDeviceName(String deviceId, Request request, RequestDataCallback<String> callback);

        void getPluginDetail(String id, RequestDataCallback<PluginDetailBean> callback);

        void getDeviceDetailRestructure(int id, RequestDataCallback<DeviceDetailResponseEntity> callback); // 设备详情，重构
    }

    interface View extends BaseView {
        void onAreasSuccess(AreasBean data);

        void onAreasFail(int errorCode, String msg);

        void onAddAreaRoomSuccess(AreasBean data);

        void onAddAreaRoomFail(int errorCode, String msg);

        void onUpdateDeviceNameSuccess();

        void onUpdateDeviceNameFail(int errorCode, String msg);

        void getPluginDetailSuccess(PluginDetailBean pluginDetailBean);

        void getPluginDetailFail(int errorCode, String msg);

        void getDeviceDetailRestructureSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity);
        void getDeviceDetailRestructureFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getAreaList();

        void addAreaRoom(AddRoomRequest name);

        void updateDeviceName(String deviceId, Request request);

        void getPluginDetail(String id);

        void getDeviceDetailRestructure(int id);   // 设备详情，重构
    }
}
