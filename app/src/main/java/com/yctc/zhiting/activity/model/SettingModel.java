package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SettingContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

/**
 * 设置
 */
public class SettingModel implements SettingContract.Model {

    /**
     * 获取用户详情
     * @param id
     * @param callback
     */
    @Override
    public void getUserInfo(int id, RequestDataCallback<MemberDetailBean> callback) {
        HTTPCaller.getInstance().get(MemberDetailBean.class, HttpUrlConfig.getUsers()+"/"+id, callback);
    }
}
