package com.yctc.zhiting.activity.model;


import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneListContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.scene.SceneListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制场景  场景选择
 */
public class SceneListModel implements SceneListContract.Model {

    /**
     * 场景列表
     * @param callback
     */
    @Override
    public void getSceneList(RequestDataCallback<SceneListBean> callback) {
        List<NameValuePair> requestData = new ArrayList<>();
        requestData.add(new NameValuePair("type", String.valueOf(1)));
        HTTPCaller.getInstance().get(SceneListBean.class, HttpUrlConfig.getScene(), requestData,callback);
    }
}
