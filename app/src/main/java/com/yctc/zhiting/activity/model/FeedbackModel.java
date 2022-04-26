package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.yctc.zhiting.activity.contract.FeedbackContract;
import com.yctc.zhiting.activity.contract.FeedbackListContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.UploadFileBean;

import java.util.List;

public class FeedbackModel implements FeedbackContract.Model {

    @Override
    public void uploadFile(List<NameValuePair> requestData, RequestDataCallback<UploadFileBean> callback) {
        HttpConfig.clearHeader();
        HTTPCaller.getInstance().postFile(UploadFileBean.class, HttpUrlConfig.getFileUpload(), requestData, callback);
    }

    @Override
    public void feedback(int userId, String body, RequestDataCallback<Object> callback) {
        HttpConfig.clearHeader();
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getFeedbackList(userId), body, callback);
    }
}
