package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.FeedbackDetailContract;
import com.yctc.zhiting.activity.contract.FeedbackListContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.FeedbackDetailBean;

public class FeedbackDetailModel implements FeedbackDetailContract.Model {

    @Override
    public void getFeedbackDetail(int userId, int feedbackId, RequestDataCallback<FeedbackDetailBean> callback) {
        HTTPCaller.getInstance().get(FeedbackDetailBean.class, HttpUrlConfig.getFeedbackDetail(userId, feedbackId), callback);
    }
}
