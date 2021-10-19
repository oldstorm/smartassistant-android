package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.CheckBindSaBean;


/**
 * 添加设备
 */
public interface AddDeviceContract {
    interface Model {
        void checkBindSa(RequestDataCallback<CheckBindSaBean> callback);
    }

    interface View extends BaseView {
        void checkBindSaSuccess(CheckBindSaBean data);
        void checkBindSaFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void checkBindSa();
    }
}
