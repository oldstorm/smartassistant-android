package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ConfigNetworkWebContract;
import com.yctc.zhiting.activity.model.ConfigNetworkWebModel;
import com.yctc.zhiting.entity.home.AccessTokenBean;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.DeviceBean;

public class ConfigNetworkWebPresenter extends BasePresenterImpl<ConfigNetworkWebContract.View> implements ConfigNetworkWebContract.Presenter {

    private ConfigNetworkWebModel model;

    @Override
    public void init() {
        model = new ConfigNetworkWebModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getAccessToken() {
        model.getAccessToken(new RequestDataCallback<AccessTokenBean>() {
            @Override
            public void onSuccess(AccessTokenBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getAccessTokenSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getAccessTokenFail(errorCode, errorMessage);
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
}
