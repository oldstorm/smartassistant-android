package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.FeedbackContract;
import com.yctc.zhiting.activity.model.FeedbackModel;
import com.yctc.zhiting.entity.mine.UploadFileBean;

import java.util.List;

public class FeedbackPresenter extends BasePresenterImpl<FeedbackContract.View> implements FeedbackContract.Presenter {

    private FeedbackModel model;

    @Override
    public void init() {
        model = new FeedbackModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void uploadFile(List<NameValuePair> requestData) {
        if (mView!=null) {
            mView.showLoadingView();
        }
        model.uploadFile(requestData, new RequestDataCallback<UploadFileBean>() {
            @Override
            public void onSuccess(UploadFileBean obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.uploadFileSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.uploadFileFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 反馈
     * @param userId
     * @param body
     */
    @Override
    public void feedback(int userId, String body) {
        if (mView!=null) {
            mView.showLoadingView();
        }
        model.feedback(userId, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.feedbackSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.feedbackFail(errorCode, errorMessage);
                }
            }
        });
    }
}
