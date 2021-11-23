package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.DeviceConnectContract;
import com.yctc.zhiting.activity.model.DeviceConnectModel;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.request.BindCloudRequest;

/**
 * 添加设备
 */
public class DeviceConnectPresenter extends BasePresenterImpl<DeviceConnectContract.View> implements DeviceConnectContract.Presenter {

    private DeviceConnectModel model;

    @Override
    public void init() {
        model = new DeviceConnectModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void sync(String body, String url) {
        model.sync(body, url, new RequestDataCallback<InvitationCheckBean>() {
            @Override
            public void onSuccess(InvitationCheckBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.syncSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.syncFail(errorCode, errorMessage);
                }
            }
        });
    }

    @Override
    public void addDevice(DeviceBean bean) {
        if (model!=null)
        model.addDevice(bean, new RequestDataCallback<AddDeviceResponseBean>() {
            @Override
            public void onSuccess(AddDeviceResponseBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.addDeviceSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.addDeviceFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 绑定云端
     *
     * @param request
     */
    @Override
    public void bindCloud(BindCloudRequest request) {
        model.bindCloud(request, new RequestDataCallback<String>() {
            @Override
            public void onSuccess(String obj) {
                super.onSuccess(obj);
                if (mView != null)
                    mView.bindCloudSuccess();
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null)
                    mView.bindCloudFail(errorCode, errorMessage);
            }
        });
    }
}
