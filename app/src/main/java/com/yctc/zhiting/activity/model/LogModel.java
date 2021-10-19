package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.google.gson.reflect.TypeToken;
import com.yctc.zhiting.activity.contract.LogContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.scene.LogBean;

import java.util.List;

/**
 * 执行日志
 */
public class LogModel implements LogContract.Model {

    @Override
    public void getLogList(List<NameValuePair> requestData, RequestDataCallback<List<LogBean>> callback) {
        HTTPCaller.getInstance().get(HttpUrlConfig.getSceneLog(), requestData, new TypeToken<List<LogBean>>(){}.getType(), callback);
    }
}
