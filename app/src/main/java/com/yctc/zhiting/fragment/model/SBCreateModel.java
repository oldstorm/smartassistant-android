package com.yctc.zhiting.fragment.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.CreatePluginListBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.fragment.contract.SBCreateContract;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

import java.util.List;

public class SBCreateModel implements SBCreateContract.Model {

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
     * 上传插件
     * @param requestData
     * @param callback
     */
    @Override
    public void uploadPlugin(List<NameValuePair> requestData, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().postFile(Object.class, HttpUrlConfig.getUploadPlugin(), requestData, callback);
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
