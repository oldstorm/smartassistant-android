package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;

public interface PwdModifyContract {
    interface Model {
        void updatePwd(int id, String body, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void updatePwdSuccess();
        void updatePwdFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void updatePwd(int id, String body);
    }
}
