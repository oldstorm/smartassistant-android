package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AvatarContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.UploadFileBean;
import com.yctc.zhiting.utils.UserUtils;

import java.util.List;

public class AvatarModel implements AvatarContract.Model {

    /**
     * 上传头像
     * @param requestData
     * @param callback
     */
    @Override
    public void uploadAvatar(List<NameValuePair> requestData, RequestDataCallback<UploadFileBean> callback) {
        HTTPCaller.getInstance().postFile(UploadFileBean.class, HttpUrlConfig.getUploadAvatarUrl(false), requestData, callback);
    }

    /**
     * 上传SC头像
     * @param requestData
     * @param callback
     */
    @Override
    public void uploadAvatarSC(List<NameValuePair> requestData, RequestDataCallback<UploadFileBean> callback) {
        HTTPCaller.getInstance().postFile(UploadFileBean.class, HttpUrlConfig.getUploadAvatarUrl(true), requestData, callback);
    }

    /**
     * 修改头像
     *
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMember(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, (HttpUrlConfig.getUsers()) + "/" + id, body, callback);
    }

    /**
     * 修改sc头像
     *
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMemberSC(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getSCUsersWithoutHeader() + "/" + id + Constant.ONLY_SC, body, callback);
    }
}
