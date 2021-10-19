package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AddDeviceContract;
import com.yctc.zhiting.activity.contract.AddHCContract;
import com.yctc.zhiting.activity.model.AddDeviceModel;
import com.yctc.zhiting.activity.model.AddHCModel;
import com.yctc.zhiting.entity.mine.CheckBindSaBean;

/**
 * 添加设备
 */
public class AddDevicePresenter extends BasePresenterImpl<AddDeviceContract.View> implements AddDeviceContract.Presenter {

    private AddDeviceModel model;

    @Override
    public void init() {
        model = new AddDeviceModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 检查SA绑定情况
     */
    @Override
    public void checkBindSa() {
        model.checkBindSa(new RequestDataCallback<CheckBindSaBean>() {
            @Override
            public void onSuccess(CheckBindSaBean data) {
                super.onSuccess(data);
                if (mView != null) {
                    mView.checkBindSaSuccess(data);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.checkBindSaFail(errorCode,errorMessage);
                }
            }
        });
    }
}
