package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SetUPContract;
import com.yctc.zhiting.config.HttpUrlConfig;

public class SetUpModel implements SetUPContract.Model {
    /**
     * 修改成员
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMember(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getUsers()+"/"+id, body, callback);
    }
}
