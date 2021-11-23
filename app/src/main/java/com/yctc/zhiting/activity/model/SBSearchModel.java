package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SBSearchContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.CreatePluginListBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

import java.util.List;

public class SBSearchModel implements SBSearchContract.Model {
    /**
     * 品牌列表
     * @param requestData
     * @param callback
     */
    @Override
    public void getBrandList(List<NameValuePair> requestData, RequestDataCallback<BrandListBean> callback) {
        HTTPCaller.getInstance().get(BrandListBean.class, HttpUrlConfig.getBrandListUrl(), requestData, callback);
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

    /**
     * 品牌创作插件
     * @param requestData
     * @param callback
     */
    @Override
    public void getCreateList(List<NameValuePair> requestData, RequestDataCallback<CreatePluginListBean> callback) {
        HTTPCaller.getInstance().get(CreatePluginListBean.class, HttpUrlConfig.getCreatePluginList(), requestData, callback);
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
