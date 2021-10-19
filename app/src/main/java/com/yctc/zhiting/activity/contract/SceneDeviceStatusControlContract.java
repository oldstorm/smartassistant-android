package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.bean.DeviceDetailBean;


/**
 * 品牌详情
 */
public interface SceneDeviceStatusControlContract {
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
