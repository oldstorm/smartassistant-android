package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.LogContract;
import com.yctc.zhiting.activity.model.LogModel;
import com.yctc.zhiting.entity.scene.LogBean;

import java.util.List;

/**
 * 添加设备
 */
public class LogPresenter extends BasePresenterImpl<LogContract.View> implements LogContract.Presenter {

    private LogModel model;

    @Override
    public void init() {
        model = new LogModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getLogList(boolean showLoading, List<NameValuePair> requestData) {
        if (mView!=null && showLoading) {
            mView.showLoadingView();
        }
        model.getLogList(requestData, new RequestDataCallback<List<LogBean>>() {
            @Override
            public void onSuccess(List<LogBean> obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.getLogListSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.getLogListFail(errorCode, errorMessage);
                }
            }
        });
    }
}
