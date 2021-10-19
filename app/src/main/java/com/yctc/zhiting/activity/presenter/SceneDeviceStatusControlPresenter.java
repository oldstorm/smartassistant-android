package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.activity.contract.SceneDeviceStatusControlContract;
import com.yctc.zhiting.activity.model.BrandDetailModel;
import com.yctc.zhiting.activity.model.SceneDeviceStatusControlModel;
import com.yctc.zhiting.bean.DeviceDetailBean;

/**
 * 添加设备
 */
public class SceneDeviceStatusControlPresenter extends BasePresenterImpl<SceneDeviceStatusControlContract.View> implements SceneDeviceStatusControlContract.Presenter {

    private SceneDeviceStatusControlModel model;

    @Override
    public void init() {
        model = new SceneDeviceStatusControlModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getDeviceDetail(int id) {
        model.getDeviceDetail(id, new RequestDataCallback<DeviceDetailBean>() {
            @Override
            public void onSuccess(DeviceDetailBean obj) {
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
