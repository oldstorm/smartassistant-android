package com.yctc.zhiting.fragment.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.fragment.contract.SceneItemFragmentContract;

public class SceneItemFragmentModel implements SceneItemFragmentContract.Model {
    @Override
    public void perform(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getExecute(id), body, callback);
    }


    @Override
    public void orderScene(String scene_ids, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getScene(), scene_ids, callback);
    }
}
