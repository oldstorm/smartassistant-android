package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

import java.util.List;


/**
 * 支持品牌
 */
public interface SupportBrandContract {
    interface Model {
        void getBrandList(List<NameValuePair> requestData, RequestDataCallback<BrandListBean> callback);
        void uploadPlugin(List<NameValuePair> requestData, RequestDataCallback<Object> callback);
        void addOrUpdatePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, RequestDataCallback<OperatePluginBean> callback);
    }

    interface View extends BaseView {
        void getBrandListSuccess(BrandListBean brandListBean);
        void getBrandListFail(int errorCode, String msg);
        void uploadPluginSuccess(Object object);
        void uploadPluginFail(int errorCode, String msg);
        void addOrUpdatePluginsSuccess(OperatePluginBean operatePluginBean, int position);
        void addOrUpdatePluginsFail(int errorCode, String msg, int position);

    }

    interface Presenter extends BasePresenter<View> {
        void getBrandList(List<NameValuePair> requestData, boolean showLoading);
        void uploadPlugin(List<NameValuePair> requestData);
        void addOrUpdatePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, int position);
    }
}
