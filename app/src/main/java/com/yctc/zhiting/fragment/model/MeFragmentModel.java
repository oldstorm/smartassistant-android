package com.yctc.zhiting.fragment.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.google.gson.Gson;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.fragment.contract.MeFragmentContract;

public class MeFragmentModel implements MeFragmentContract.Model {
    /**
     * 修改昵称
     *
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMember(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getACUsers() + "/" + id, body, callback);
    }
}
