package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;


/**
 * 插件详情
 */
public interface PluginDetailContract {
    interface Model {
        void getDetail(String id, RequestDataCallback<PluginDetailBean> callback);
    }

    interface View extends BaseView {
        void getDetailSuccess(PluginDetailBean pluginDetailBean);
        void getDetailFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getDetail(String id);
    }
}
