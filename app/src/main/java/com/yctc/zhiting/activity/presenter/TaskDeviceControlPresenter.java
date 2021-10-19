package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneDeviceStatusControlContract;
import com.yctc.zhiting.activity.contract.TaskDeviceControlContract;
import com.yctc.zhiting.activity.model.SceneDeviceStatusControlModel;
import com.yctc.zhiting.activity.model.TaskDeviceControlModel;
import com.yctc.zhiting.bean.DeviceDetailBean;

/**
 * 任务设备控制
 */
public class TaskDeviceControlPresenter extends BasePresenterImpl<TaskDeviceControlContract.View> implements TaskDeviceControlContract.Presenter {

    private TaskDeviceControlModel model;

    @Override
    public void init() {
        model = new TaskDeviceControlModel();
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
