package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.PwdModifyContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.LoginBean;

public class PwdModifyModel implements PwdModifyContract.Model {

    /**
     * 修改密码
     * @param id
     * @param callback
     */
    @Override
    public void updatePwd(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class,  HttpUrlConfig.getSCUsers() + "/" + id+ Constant.ONLY_SC, body, callback);
    }
}
