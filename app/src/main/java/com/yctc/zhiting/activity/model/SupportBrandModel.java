package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SupportBrandContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

import java.util.List;

/**
 *  支持品牌
 */
public class SupportBrandModel implements SupportBrandContract.Model {

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
     * 上传插件
     * @param requestData
     * @param callback
     */
    @Override
    public void uploadPlugin(List<NameValuePair> requestData, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().postFile(Object.class, HttpUrlConfig.getUploadPlugin(), requestData, callback);
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
}
