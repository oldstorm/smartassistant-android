package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.FeedbackListBean;

public interface FeedbackListContract {
    interface Model {
        void getFeedbackList(int userId, RequestDataCallback<FeedbackListBean> callback);
    }

    interface View extends BaseView {
        void getFeedbackListSuccess(FeedbackListBean feedbackListBean);
        void getFeedbackListFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getFeedbackList(int userId);
    }
}
