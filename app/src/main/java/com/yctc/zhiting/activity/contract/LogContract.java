package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.scene.LogBean;

import java.util.List;


/**
 * 执行日志
 */
public interface LogContract {
    interface Model {
        void getLogList(List<NameValuePair> requestData, RequestDataCallback<List<LogBean>> callback);
    }

    interface View extends BaseView {
        void getLogListSuccess(List<LogBean> logBeans);
        void getLogListFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getLogList(boolean showLoading, List<NameValuePair> requestData);
    }
}
