package com.yctc.zhiting.activity.model;

import com.app.main.framework.entity.HttpResult;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.MainContract;
import com.yctc.zhiting.request.AddRoomRequest;

public class MainModel implements MainContract.Model {

    @Override
    public void postOrderCheck(AddRoomRequest request, RequestDataCallback<HttpResult> callback) {
        //HTTPCaller.getInstance().post(HttpResult.class, HttpUrlConfig.getOrderState(), new Gson().toJson(request), callback);
    }
}
