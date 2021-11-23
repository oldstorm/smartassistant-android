package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;


/**
 * 插件详情
 */
public interface PluginDetailContract {
    interface Model {
        void getDetail(String id, RequestDataCallback<PluginDetailBean> callback);
        void addOrUpdatePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, RequestDataCallback<OperatePluginBean> callback);
        void removePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, RequestDataCallback<OperatePluginBean> callback);
    }

    interface View extends BaseView {
        void getDetailSuccess(PluginDetailBean pluginDetailBean);
        void getDetailFail(int errorCode, String msg);
        void addOrUpdatePluginsSuccess(OperatePluginBean operatePluginBean, int position);
        void addOrUpdatePluginsFail(int errorCode, String msg, int position);
        void removePluginsSuccess(OperatePluginBean operatePluginBean, int position);
        void removePluginsFail(int errorCode, String msg, int position);
    }

    interface Presenter extends BasePresenter<View> {
        void getDetail(String id);
        void addOrUpdatePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, int position);
        void removePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, int position);
    }
}
