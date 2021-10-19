package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.ScopeTokenBean;
import com.yctc.zhiting.entity.ScopesBean;

public interface AuthorizeContract {
    interface Model {
        void getScopeList(RequestDataCallback<ScopesBean> callback);
        void getScopeToken(String body, RequestDataCallback<ScopeTokenBean> callback);
    }

    interface View extends BaseView {
        void getScopesSuccess(ScopesBean scopesBean);
        void getScopesFail(int errorCode, String msg);
        void getScopeTokenSuccess(ScopeTokenBean scopeTokenBean);
        void getScopeTokenFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getScopeList();
        void getScopeToken(String body);
    }
}
