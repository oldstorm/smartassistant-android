package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.FeedbackListContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.FeedbackListBean;

public class FeedbackListModel implements FeedbackListContract.Model {

    @Override
    public void getFeedbackList(int userId, RequestDataCallback<FeedbackListBean> callback) {
        HTTPCaller.getInstance().get(FeedbackListBean.class, HttpUrlConfig.getFeedbackList(userId), callback);
    }
}
