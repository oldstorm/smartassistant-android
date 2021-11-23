package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.CreatePluginDetailContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.scene.PluginDetailBean;

public class CreatePluginDetailModel implements CreatePluginDetailContract.Model {

    @Override
    public void getDetail(String id, RequestDataCallback<PluginDetailBean> callback) {
        HTTPCaller.getInstance().get(PluginDetailBean.class, HttpUrlConfig.getPluginsDetail()+"/"+id, callback);
    }

    /**
     * 删除插件
     * @param id
     * @param callback
     */
    @Override
    public void removePlugin(String id, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().delete(Object.class, HttpUrlConfig.getCreatePluginList()+"/"+id, "", callback);
    }
}
