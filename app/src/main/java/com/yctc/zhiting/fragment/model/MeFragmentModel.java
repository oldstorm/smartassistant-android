package com.yctc.zhiting.fragment.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.ExtensionBean;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.LoginBean;
import com.yctc.zhiting.entity.mine.UploadFileBean;
import com.yctc.zhiting.fragment.contract.MeFragmentContract;

import java.util.List;

public class MeFragmentModel implements MeFragmentContract.Model {
    /**
     * 修改昵称
     *
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMember(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getACUsers() + "/" + id, body, callback);
    }

    /**
     * 通过sc找回sa的用户凭证
     * @param userId
     * @param requestData
     * @param callback
     */
    @Override
    public void getSATokenBySC(int userId, List<NameValuePair> requestData, RequestDataCallback<FindSATokenBean> callback) {
        HTTPCaller.getInstance().get(FindSATokenBean.class, HttpUrlConfig.getSAToken(userId)+ Constant.ONLY_SC, requestData, callback );
    }

    @Override
    public void getExtensions(RequestDataCallback<ExtensionBean> callback) {
        HTTPCaller.getInstance().get(ExtensionBean.class, HttpUrlConfig.getExtensions(), callback);
    }

    /**
     * 家庭详情
     * @param id
     * @param callback
     */
    @Override
    public void checkSaToken(long id, RequestDataCallback<HomeCompanyBean> callback) {
        HTTPCaller.getInstance().get(HomeCompanyBean.class, HttpUrlConfig.getAreasUrl()+"/"+id, callback);
    }

    /**
     * 上传头像
     * @param requestData
     * @param callback
     */
    @Override
    public void uploadAvatar(List<NameValuePair> requestData,RequestDataCallback<UploadFileBean> callback) {
        HTTPCaller.getInstance().postFile(UploadFileBean.class, HttpUrlConfig.getUploadAvatarUrl(false), requestData, callback);
    }

    /**
     * 用户信息
     * @param id
     * @param callback
     */
    @Override
    public void getSCUserInfo(int id, RequestDataCallback<LoginBean> callback) {
        HTTPCaller.getInstance().get(LoginBean.class,  HttpUrlConfig.getSCUsers() + "/" + id+ Constant.ONLY_SC, callback);
    }
}
