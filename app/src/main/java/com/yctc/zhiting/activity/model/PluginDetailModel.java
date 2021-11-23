package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.PluginDetailContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

/**
 * 插件详情
 */
public class PluginDetailModel implements PluginDetailContract.Model {
    @Override
    public void getDetail(String id, RequestDataCallback<PluginDetailBean> callback) {
        HTTPCaller.getInstance().get(PluginDetailBean.class, HttpUrlConfig.getPluginsDetail()+"/"+id, callback);
    }

    /**
     * 添加更新插件
     * @param addOrUpdatePluginRequest
     * @param name
     * @param callback
     */
    @Override
    public void addOrUpdatePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, RequestDataCallback<OperatePluginBean> callback) {
        HTTPCaller.getInstance().post(OperatePluginBean.class, HttpUrlConfig.getAddOrUpdatePlugin(name), addOrUpdatePluginRequest, callback);
    }

    @Override
    public void removePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, RequestDataCallback<OperatePluginBean> callback) {
        HTTPCaller.getInstance().delete(OperatePluginBean.class, HttpUrlConfig.getAddOrUpdatePlugin(name), addOrUpdatePluginRequest.toString(), callback);
    }
}
