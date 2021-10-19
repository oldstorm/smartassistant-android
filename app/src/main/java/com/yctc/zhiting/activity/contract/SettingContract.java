package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

/**
 * 设置
 */
public interface SettingContract {
    interface Model {
        void getUserInfo(int id, RequestDataCallback<MemberDetailBean> callback);
    }

    interface View extends BaseView {
        void getUserInfoSuccess(MemberDetailBean memberDetailBean);
        void getUserInfoFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getUserInfo(int id);
    }
}
