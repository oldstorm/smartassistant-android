package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;


/**
 * 品牌详情
 */
public interface SceneDeviceConditionAttrContract {
    interface Model {
        void getDeviceDetail(int id, int type, RequestDataCallback<DeviceDetailResponseEntity> callback);
    }

    interface View extends BaseView {
        void getDeviceDetailSuccess(DeviceDetailResponseEntity deviceDetailBean);
        void getFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getDeviceDetail(int id, int type);
    }
}
