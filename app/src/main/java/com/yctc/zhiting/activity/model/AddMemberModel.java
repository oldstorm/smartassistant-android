package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AddMemberContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.config.HttpUrlParams;
import com.yctc.zhiting.entity.mine.DepartmentDetail;
import com.yctc.zhiting.entity.mine.MembersBean;

public class AddMemberModel implements AddMemberContract.Model {

    /**
     * 成员列表
     *
     * @param callback
     */
    @Override
    public void getMembers(RequestDataCallback<MembersBean> callback) {
        HTTPCaller.getInstance().get(MembersBean.class, HttpUrlConfig.getUsers(), callback);
    }

    /**
     * 已在的成员
     * @param id
     * @param callback
     */
    @Override
    public void getDepartmentDetail(int id, RequestDataCallback<DepartmentDetail> callback) {
        HTTPCaller.getInstance().get(DepartmentDetail.class, HttpUrlConfig.getDepartments()+"/"+id, callback);
    }

    /**
     * 添加部门成员
     * @param id
     * @param json
     * @param callback
     */
    @Override
    public void addDMDepartmentMember(int id, String json, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getDepartments()+"/"+id+"/"+ HttpUrlParams.users, json, callback);
    }

}
