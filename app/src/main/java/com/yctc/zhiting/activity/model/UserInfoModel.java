package com.yctc.zhiting.activity.model;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.UserInfoContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.LoginBean;
import com.yctc.zhiting.utils.UserUtils;

/**
 * 个人资料
 */
public class UserInfoModel implements UserInfoContract.Model {

    /**
     * 用户信息
     *
     * @param id
     * @param callback
     */
    @Override
    public void getUserInfo(int id, RequestDataCallback<LoginBean> callback) {
        HTTPCaller.getInstance().get(LoginBean.class, HttpUrlConfig.getSCUsers() + "/" + id + Constant.ONLY_SC, callback);
    }

    /**
     * 修改昵称
     *
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMember(int id, String body, RequestDataCallback<Object> callback) {
        String url = (UserUtils.isLogin() ? HttpUrlConfig.getSCUsers() : HttpUrlConfig.getUsers()) + "/" + id;
        LogUtil.e("updateMember11="+UserUtils.isLogin());
        LogUtil.e("updateMember12="+url);
        HTTPCaller.getInstance().put(Object.class, url, body, callback);
    }

    /**
     * 修改sc昵称
     *
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMemberSC(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getSCUsersWithoutHeader() + "/" + id + Constant.ONLY_SC, body, callback);
    }

    /**
     * 退出登录
     *
     * @param callback
     */
    @Override
    public void logout(RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getLogout() + Constant.ONLY_SC, "", callback);
    }
}
