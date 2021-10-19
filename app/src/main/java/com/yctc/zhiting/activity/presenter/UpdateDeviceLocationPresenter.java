package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.activity.contract.UpdateDeviceLocationContract;
import com.yctc.zhiting.activity.model.BrandDetailModel;
import com.yctc.zhiting.activity.model.UpdateLocationModel;
import com.yctc.zhiting.entity.mine.AreasBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.request.AddRoomRequest;

/**
 * 添加设备
 */
public class UpdateDeviceLocationPresenter extends BasePresenterImpl<UpdateDeviceLocationContract.View> implements UpdateDeviceLocationContract.Presenter {

    private UpdateLocationModel model;

    @Override
    public void init() {
        model = new UpdateLocationModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 用户权限
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
                    mView.hideLoadingView();
                    mView.getFail(errorCode, errorMessage);
                }
            }
        });
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
}
