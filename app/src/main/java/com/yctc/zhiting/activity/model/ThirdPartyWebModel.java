package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ThirdPartyWebContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

public class ThirdPartyWebModel implements ThirdPartyWebContract.Model {

    /**
     * 解绑第三方
     * @param id
     * @param areaId
     * @param callback
     */
    @Override
    public void unbindThirdParty(String id, String areaId, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().delete(Object.class, HttpUrlConfig.getCloudUnbind(id, areaId), "", callback);
    }

    /**
     * 成员详情
     * @param id
     * @param callback
     */
    @Override
    public void getMemberDetail(int id, RequestDataCallback<MemberDetailBean> callback) {
        HTTPCaller.getInstance().get(MemberDetailBean.class, HttpUrlConfig.getUsers()+"/"+id, callback);
    }
}
