package com.yctc.zhiting.fragment.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.CreatePluginListBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

import java.util.List;

public interface SBCreateContract {

    interface Model {
        void getCreateList(List<NameValuePair> requestData, RequestDataCallback<CreatePluginListBean> callback);
        void uploadPlugin(List<NameValuePair> requestData, RequestDataCallback<Object> callback);
        void removePlugin(String id, RequestDataCallback<Object> callback);
    }
    interface View extends BaseView {
        void getCreateListSuccess(CreatePluginListBean createPluginListBean);
        void getCreateListFail(int errorCode, String msg);
        void uploadPluginSuccess(Object object);
        void uploadPluginFail(int errorCode, String msg);
        void removePluginSuccess(int position);
        void removePluginFail(int errorCode, String msg, int position);

    }

    interface Presenter extends BasePresenter<View> {
        void getCreateList(List<NameValuePair> requestData, boolean showLoading);
        void uploadPlugin(List<NameValuePair> requestData);
        void removePlugin(String id, int position);
    }
}
