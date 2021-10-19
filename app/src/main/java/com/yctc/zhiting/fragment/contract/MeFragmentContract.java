package com.yctc.zhiting.fragment.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;

public interface MeFragmentContract {
    interface Model {
        void updateMember(int id, String body, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void updateNameSuccess();
        void updateNameFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void updateMember(int id, String body);
    }
}
