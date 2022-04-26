package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.UploadFileBean;

import java.util.List;

public interface FeedbackContract {
    interface Model {
        void uploadFile(List<NameValuePair> requestData, RequestDataCallback<UploadFileBean> callback);
        void feedback(int userId, String body, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {

        void uploadFileSuccess(UploadFileBean uploadFileBean);
        void uploadFileFail(int errorCode, String msg);

        void feedbackSuccess();
        void feedbackFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void uploadFile(List<NameValuePair> requestData);
        void feedback(int userId, String body);
    }
}
