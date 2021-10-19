package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AuthorizeContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.ScopeTokenBean;
import com.yctc.zhiting.entity.ScopesBean;
import com.yctc.zhiting.entity.mine.LocationsBean;
import com.yctc.zhiting.entity.mine.LoginBean;

public class AuthorizeModel implements AuthorizeContract.Model {

    /**
     * 获取 SCOPE 列表
     * @param callback
     */
    @Override
    public void getScopeList(RequestDataCallback<ScopesBean> callback) {
        HTTPCaller.getInstance().get(ScopesBean.class, HttpUrlConfig.getScopes(),callback);
    }

    /**
     * 获取 SCOPE Token
     * @param body
     * @param callback
     */
    @Override
    public void getScopeToken(String body, RequestDataCallback<ScopeTokenBean> callback) {
        HTTPCaller.getInstance().post(ScopeTokenBean.class, HttpUrlConfig.getScopesToken(), body, callback);
    }
}
