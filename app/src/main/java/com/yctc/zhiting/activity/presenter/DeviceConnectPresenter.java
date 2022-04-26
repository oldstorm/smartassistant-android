package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.activity.contract.DeviceConnectContract;
import com.yctc.zhiting.activity.model.DeviceConnectModel;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.AreaMoveUrlBean;
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

    /**
     * 之前添加SA和普通设备，现在时添加SA设备
     * @param bean
     */
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
    public void bindCloud(BindCloudRequest request,String url) {
        model.bindCloud(request, url, new RequestDataCallback<String>() {
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

    /**
     * 获取家庭迁移地址
     */
    @Override
    public void getAreaMoveUrl(String id) {
        model.getAreaMoveUrl(id, new RequestDataCallback<AreaMoveUrlBean>() {
            @Override
            public void onSuccess(AreaMoveUrlBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getAreaMoveUrlSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.getAreaMoveUrlFail(errorCode,errorMessage);
                }
            }
        });
    }

    /**
     * 迁移云端家庭到本地
     * @param body
     */
    @Override
    public void areaMove(String body, String url) {
        model.areaMove(body, url, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.areaMoveSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.areaMoveFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 找回凭证方式
     * @param request
     */
    @Override
    public void putFindCertificate(Request request) {
        model.putFindCertificate(request, new RequestDataCallback<String>() {
            @Override
            public void onSuccess(String obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.onCertificateSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.onCertificateFail(errorCode, errorMessage);
                }
            }
        });
    }
}
