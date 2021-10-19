package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.MembersBean;

public class TransferOwnerContract {

    public interface Model {
        void getMembers(RequestDataCallback<MembersBean> callback);
        void transferOwner(int userId, RequestDataCallback<Object> callback);
    }

    public interface View extends BaseView {
        void getMembersSuccess(MembersBean membersBean);
        void getMemberFail(int errorCode, String msg);
        void transferSuccess();
        void transferFail(int errorCode, String msg);
    }

    public interface Presenter extends BasePresenter<View> {
        void getMembers();
        void transferOwner(int userId);
    }
}
