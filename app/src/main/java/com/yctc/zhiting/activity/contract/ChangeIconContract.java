package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.home.DeviceLogoListBean;

public interface ChangeIconContract {
    interface Model {
        void getLogoList(int id, RequestDataCallback<DeviceLogoListBean> callback);
        void updateType(int id, String logoType, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void getLogoListSuccess(DeviceLogoListBean deviceLogoListBean);
        void getLogoListFail(int errorCode, String msg);
        void updateTypeSuccess();
        void updateTypeFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getLogoList(int id);
        void updateType(int id, int logoType);
    }
}
