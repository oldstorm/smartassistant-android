package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.fragment.contract.HomeItemFragmentContract;

/**
 * 个人资料
 */
public interface UserInfoContract {
    interface Model {
        void updateMember(int id, String body, RequestDataCallback<Object> callback);
        void updateMemberSC(int id, String body, RequestDataCallback<Object> callback);
        void logout(RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void updateSuccess();
        void updateFail(int errorCode, String msg);
        void updateScSuccess();
        void updateScFail(int errorCode, String msg);
        void logoutSuccess();
        void requestFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void updateMember(int id, String body);
        void updateMemberSC(int id, String body);
        void logout();
    }
}
