package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.activity.contract.SetDevicePositionContract;
import com.yctc.zhiting.activity.model.SetDevicePositionModel;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.mine.AreasBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.request.AddRoomRequest;

public class SetDevicePositionPresenter extends BasePresenterImpl<SetDevicePositionContract.View> implements SetDevicePositionContract.Presenter {

    private SetDevicePositionModel model;

    @Override
    public void init() {
        model = new SetDevicePositionModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 检查SA绑定情况
     */
    @Override
    public void getAreaList() {
        model.getAreaList(new RequestDataCallback<AreasBean>() {
            @Override
            public void onSuccess(AreasBean data) {
                super.onSuccess(data);
                if (mView != null) {
                    mView.onAreasSuccess(data);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.onAreasFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 添加房间
     *
     * @param request
     */
    @Override
    public void addAreaRoom(AddRoomRequest request) {
        mView.showLoadingView();
        model.addAreaRoom(request, new RequestDataCallback<AreasBean>() {
            @Override
            public void onSuccess(AreasBean data) {
                super.onSuccess(data);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.onAddAreaRoomSuccess(data);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.onAddAreaRoomFail(errorCode, errorMessage);
                }
            }
        });
    }

    @Override
    public void updateDeviceName(String deviceId, Request request) {
        mView.showLoadingView();
        model.updateDeviceName(deviceId, request, new RequestDataCallback<String>() {
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.onUpdateDeviceNameSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.onUpdateDeviceNameFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 插件详情
     * @param id
     */
    @Override
    public void getPluginDetail(String id) {
        model.getPluginDetail(id, new RequestDataCallback<PluginDetailBean>() {
            @Override
            public void onSuccess(PluginDetailBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getPluginDetailSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getPluginDetailFail(errorCode,errorMessage);
                }
            }
        });
    }

    /**
     * 设备详情 重构
     * @param id
     */
    @Override
    public void getDeviceDetailRestructure(int id) {
        model.getDeviceDetailRestructure(id, new RequestDataCallback<DeviceDetailResponseEntity>() {
            @Override
            public void onSuccess(DeviceDetailResponseEntity obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getDeviceDetailRestructureSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getDeviceDetailRestructureFail(errorCode, errorMessage);
                }
            }
        });
    }
}
