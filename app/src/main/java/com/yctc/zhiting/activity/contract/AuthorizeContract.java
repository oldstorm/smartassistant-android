package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.ScopeTokenBean;
import com.yctc.zhiting.entity.ScopesBean;

import java.util.List;

public interface AuthorizeContract {
    interface Model {
        void getScopeList(RequestDataCallback<ScopesBean> callback);
        void getScopeToken(String body, RequestDataCallback<ScopeTokenBean> callback);

        void getSATokenBySC(int userId, List<NameValuePair> requestData, RequestDataCallback<FindSATokenBean> callback);
    }

    interface View extends BaseView {
        void getScopesSuccess(ScopesBean scopesBean);
        void getScopesFail(int errorCode, String msg);
        void getScopeTokenSuccess(ScopeTokenBean scopeTokenBean);
        void getScopeTokenFail(int errorCode, String msg);

        void getSATokenSuccess(FindSATokenBean findSATokenBean);
        void getSATokenFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getScopeList();
        void getScopeToken(String body);
        void getSAToken(int userId, List<NameValuePair> requestData);
    }
}
