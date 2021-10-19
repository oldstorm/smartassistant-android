package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.HCDetailContract;
import com.yctc.zhiting.activity.contract.HomeCompanyContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.InvitationCodeBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.MembersBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;

import java.util.List;

/**
 * 家庭/公司详情
 */
public class HCDetailModel implements HCDetailContract.Model {
    /**
     * 家庭详情
     * @param id
     * @param callback
     */
    @Override
    public void getDetail(int id, RequestDataCallback<HomeCompanyBean> callback) {
        HTTPCaller.getInstance().get(HomeCompanyBean.class, HttpUrlConfig.getAreasUrl()+"/"+id, callback);
    }

    /**
     * 成员列表
     * @param callback
     */
    @Override
    public void getMembers(RequestDataCallback<MembersBean> callback) {
        HTTPCaller.getInstance().get(MembersBean.class, HttpUrlConfig.getUsers(), callback);
    }

    /**
     * 修改家庭名称
     * @param id
     * @param callback
     */
    @Override
    public void updateName(int id,String name, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getAreasUrl()+"/"+id, name, callback);
    }

    /**
     * 获取用户权限
     * @param id
     * @param callback
     */
    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions1(id), callback);
    }

    /**
     * 退出家庭
     * @param id 家庭id
     * @param user_id 用户id
     * @param callback
     */
    @Override
    public void exitHomeCompany(int id, int user_id, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().delete(Object.class, HttpUrlConfig.getExitHomeCompany(id, user_id), "", callback);
    }

    /**
     *
     * @param id  家庭id
     * @param callback
     */
    @Override
    public void delHomeCompany(int id, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().delete(Object.class, HttpUrlConfig.getDelHomeCompany(id), "", callback);
    }

    /**
     * 角色列表
     * @param callback
     */
    @Override
    public void getRoleList(RequestDataCallback<RolesBean> callback) {
        HTTPCaller.getInstance().get(RolesBean.class, HttpUrlConfig.getRoles(), callback);
    }

    /**
     * 生成邀请码
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void generateCode(int id, String body, RequestDataCallback<InvitationCodeBean> callback) {
        HTTPCaller.getInstance().post(InvitationCodeBean.class, HttpUrlConfig.getInvitationCode(id), body, callback);
    }

    /**
     * 用户详情
     * @param id
     * @param callback
     */
    @Override
    public void getMemberDetail(int id, RequestDataCallback<MemberDetailBean> callback) {
        HTTPCaller.getInstance().get(MemberDetailBean.class, HttpUrlConfig.getUsers()+"/"+id, callback);
    }
}
