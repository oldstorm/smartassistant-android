package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.FeedbackDetailContract;
import com.yctc.zhiting.activity.model.FeedbackDetailModel;
import com.yctc.zhiting.entity.mine.FeedbackDetailBean;

public class FeedbackDetailPresenter extends BasePresenterImpl<FeedbackDetailContract.View> implements FeedbackDetailContract.Presenter {

    private FeedbackDetailModel model;

    @Override
    public void init() {
        model = new FeedbackDetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 反馈详情
     *
     * @param userId
     * @param feedbackId
     */
    @Override
    public void getFeedbackDetail(int userId, int feedbackId) {
        if (mView != null) {
            mView.showLoadingView();
        }
        model.getFeedbackDetail(userId, feedbackId, new RequestDataCallback<FeedbackDetailBean>() {
            @Override
            public void onSuccess(FeedbackDetailBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getFeedbackDetailSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getFeedbackDetailFail(errorCode, errorMessage);
                }
            }
        });
    }
}
