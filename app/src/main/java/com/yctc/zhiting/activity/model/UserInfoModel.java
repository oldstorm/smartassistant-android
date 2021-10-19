package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.UserInfoContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.utils.UserUtils;

/**
 * 个人资料
 */
public class UserInfoModel implements UserInfoContract.Model {

    /**
     * 修改昵称
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMember(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, (UserUtils.isLogin() ?HttpUrlConfig.getSCUsers() :HttpUrlConfig.getUsers())+"/"+id, body, callback);
    }

    /**
     * 修改sc昵称
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMemberSC(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getSCUsersWithoutHeader() +"/"+id, body, callback);
    }

    /**
     * 退出登录
     * @param callback
     */
    @Override
    public void logout(RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getLogout(), "", callback);
    }
}
