package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneDeviceTaskAttrContract;
import com.yctc.zhiting.activity.contract.TaskDeviceControlContract;
import com.yctc.zhiting.activity.model.SceneDeviceTaskAttrModel;
import com.yctc.zhiting.activity.model.TaskDeviceControlModel;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;

/**
 * 任务设备控制
 */
public class SceneDeviceTaskAttrPresenter extends BasePresenterImpl<SceneDeviceTaskAttrContract.View> implements SceneDeviceTaskAttrContract.Presenter {

    private SceneDeviceTaskAttrModel model;

    @Override
    public void init() {
        model = new SceneDeviceTaskAttrModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getDeviceDetail(int id, int type) {
        model.getDeviceDetail(id, type, new RequestDataCallback<DeviceDetailResponseEntity>() {
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
