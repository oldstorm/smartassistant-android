package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.FeedbackDetailBean;

public interface FeedbackDetailContract {
    interface Model {
        void getFeedbackDetail(int userId, int feedbackId, RequestDataCallback<FeedbackDetailBean> callback);
    }

    interface View extends BaseView {
        void getFeedbackDetailSuccess(FeedbackDetailBean feedbackDetailBean);
        void getFeedbackDetailFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getFeedbackDetail(int userId, int feedbackId);
    }
}
