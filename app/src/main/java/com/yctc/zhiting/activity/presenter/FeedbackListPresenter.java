package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.FeedbackListContract;
import com.yctc.zhiting.activity.model.FeedbackListModel;
import com.yctc.zhiting.entity.mine.FeedbackListBean;

public class FeedbackListPresenter extends BasePresenterImpl<FeedbackListContract.View> implements FeedbackListContract.Presenter {

    private FeedbackListModel model;

    @Override
    public void init() {
        model = new FeedbackListModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 问题反馈-获取列表
     *
     * @param userId
     */
    @Override
    public void getFeedbackList(int userId) {
        if (mView != null) {
            mView.showLoadingView();
        }
        model.getFeedbackList(userId, new RequestDataCallback<FeedbackListBean>() {
            @Override
            public void onSuccess(FeedbackListBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getFeedbackListSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getFeedbackListFail(errorCode, errorMessage);
                }
            }
        });
    }
}
