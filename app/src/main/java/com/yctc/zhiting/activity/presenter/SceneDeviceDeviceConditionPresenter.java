package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneDeviceConditionAttrContract;
import com.yctc.zhiting.activity.contract.SceneDeviceStatusControlContract;
import com.yctc.zhiting.activity.model.SceneDeviceConditionAttrModel;
import com.yctc.zhiting.activity.model.SceneDeviceStatusControlModel;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;

/**
 * 添加设备
 */
public class SceneDeviceDeviceConditionPresenter extends BasePresenterImpl<SceneDeviceConditionAttrContract.View> implements SceneDeviceConditionAttrContract.Presenter {

    private SceneDeviceConditionAttrModel model;

    @Override
    public void init() {
        model = new SceneDeviceConditionAttrModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getDeviceDetail(int id) {
        model.getDeviceDetail(id, new RequestDataCallback<DeviceDetailResponseEntity>() {
            @Override
            public void onSuccess(DeviceDetailResponseEntity obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getDeviceDetailSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getFail(errorCode, errorMessage);
                }
            }
        });
    }
}
