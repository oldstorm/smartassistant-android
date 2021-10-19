package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.yctc.zhiting.activity.contract.TransferOwnerContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.MembersBean;

public class TransferOwnerModel implements TransferOwnerContract.Model {

    /**
     * 成员列表
     * @param callback
     */
    @Override
    public void getMembers(RequestDataCallback<MembersBean> callback) {
        HTTPCaller.getInstance().get(MembersBean.class, HttpUrlConfig.getUsers(), callback);
    }

    /**
     * 转移拥有者
     * @param userId
     * @param callback
     */
    @Override
    public void transferOwner(int userId, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getTransferOwner(userId), "", callback);
    }
}
