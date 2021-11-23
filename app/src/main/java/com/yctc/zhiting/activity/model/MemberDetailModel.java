package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.MemberDetailContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;

/**
 * 家庭/公司详情
 */
public class MemberDetailModel implements MemberDetailContract.Model {

    /**
     * 成员详情
     * @param id
     * @param callback
     */
    @Override
    public void getMemberDetail(int id, RequestDataCallback<MemberDetailBean> callback) {
        HTTPCaller.getInstance().get(MemberDetailBean.class, HttpUrlConfig.getUsers()+"/"+id, callback);
    }

    /**
     * 用户权限
     * @param id
     * @param callback
     */
    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions1(id), callback);
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
     * 修改成员
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMember(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getUsers()+"/"+id, body, callback);
    }

    /**
     * 删除成员
     * @param id
     * @param callback
     */
    @Override
    public void delMember(int id, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().delete(Object.class, HttpUrlConfig.getUsers()+"/"+id, "", callback);
    }


}
