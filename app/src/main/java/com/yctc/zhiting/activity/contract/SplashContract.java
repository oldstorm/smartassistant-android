package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.AndroidAppVersionBean;

public interface SplashContract {
    interface Model {
        void getAppVersionInfo(RequestDataCallback<AndroidAppVersionBean> callback);
    }

    interface View extends BaseView {
        void getAppVersionInfoSuccess(AndroidAppVersionBean androidBean);

        void getAppVersionInfoFailed(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void checkAppVersionInfo();
    }
}
