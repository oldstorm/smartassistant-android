package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.CreatePluginListBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

import java.util.List;

public interface SBSearchContract {
    interface Model {
        void getBrandList(List<NameValuePair> requestData, RequestDataCallback<BrandListBean> callback);
        void addOrUpdatePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, RequestDataCallback<OperatePluginBean> callback);
        void getCreateList(List<NameValuePair> requestData, RequestDataCallback<CreatePluginListBean> callback);
        void removePlugin(String id, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void getBrandListSuccess(BrandListBean brandListBean);
        void getBrandListFail(int errorCode, String msg);
        void addOrUpdatePluginsSuccess(OperatePluginBean operatePluginBean, int position);
        void addOrUpdatePluginsFail(int errorCode, String msg, int position);
        void getCreateListSuccess(CreatePluginListBean createPluginListBean);
        void getCreateListFail(int errorCode, String msg);
        void removePluginSuccess(int position);
        void removePluginFail(int errorCode, String msg, int position);
    }

    interface Presenter extends BasePresenter<View> {
        void getBrandList(List<NameValuePair> requestData, boolean showLoading);
        void addOrUpdatePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, int position);
        void getCreateList(List<NameValuePair> requestData, boolean showLoading);
        void removePlugin(String id, int position);
    }
}
