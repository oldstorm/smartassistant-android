package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.home.AccessTokenBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

public interface ConfigNetworkWebContract {
    interface Model {
        void getAccessToken(RequestDataCallback<AccessTokenBean> callback);
    }

    interface View extends BaseView {
        void getAccessTokenSuccess(AccessTokenBean accessTokenBean);
        void getAccessTokenFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getAccessToken();
    }

}
