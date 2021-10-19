package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.bean.DeviceDetailBean;


/**
 * 任务设备控制
 */
public interface TaskDeviceControlContract {
    interface Model {
        void getDeviceDetail(int id, RequestDataCallback<DeviceDetailBean> callback);
    }

    interface View extends BaseView {
        void getDeviceDetailSuccess(DeviceDetailBean deviceDetailBean);
        void getFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getDeviceDetail(int id);
    }
}
