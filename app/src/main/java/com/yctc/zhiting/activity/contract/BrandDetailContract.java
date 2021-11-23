package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.BrandDetailBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;


/**
 * 品牌详情
 */
public interface BrandDetailContract {
    interface Model {
        void getDetail(String name, RequestDataCallback<BrandDetailBean> callback);
        void addOrUpdatePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, RequestDataCallback<OperatePluginBean> callback);
        void removePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, RequestDataCallback<OperatePluginBean> callback);
    }

    interface View extends BaseView {
        void getDetailSuccess(BrandDetailBean brandsBean);
        void getDetailFail(int errorCode, String msg);
        void addOrUpdatePluginsSuccess(OperatePluginBean operatePluginBean, int position);
        void addOrUpdatePluginsFail(int errorCode, String msg, int position);
        void removePluginsSuccess(OperatePluginBean operatePluginBean, int position);
        void removePluginsFail(int errorCode, String msg, int position);
    }

    interface Presenter extends BasePresenter<View> {
        void getDetail(String name);
        void addOrUpdatePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, int position);
        void removePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, int position);
    }
}
