package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;

public interface SetUPContract {
    interface Model {
        void updateMember(int id, String body, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void updateSuccess();
        void requestFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void updateMember(int id, String body);
    }
}
