package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.BrandDetailBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

/**
 * 添加设备
 */
public class BrandDetailModel implements BrandDetailContract.Model {
    @Override
    public void getDetail(String name, RequestDataCallback<BrandDetailBean> callback) {
        HTTPCaller.getInstance().get(BrandDetailBean.class, HttpUrlConfig.getBrandListUrl()+"/"+name, callback);
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
