package com.yctc.zhiting.fragment.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;
import com.yctc.zhiting.entity.scene.SceneListBean;
import com.yctc.zhiting.fragment.contract.MeFragmentContract;
import com.yctc.zhiting.fragment.contract.SceneFragmentContract;

import java.util.List;

/**
 * 场景
 */
public class SceneFragmentModel implements SceneFragmentContract.Model {

    /**
     * 场景列表
     * @param callback
     */
    @Override
    public void getSceneList(RequestDataCallback<SceneListBean> callback) {
        HTTPCaller.getInstance().get(SceneListBean.class, HttpUrlConfig.getScene(),callback);
    }

    @Override
    public void perform(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getExecute(id), body, callback);
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
     * 通过sc找回sa的用户凭证
     * @param userId
     * @param requestData
     * @param callback
     */
    @Override
    public void getSATokenBySC(int userId, List<NameValuePair> requestData, RequestDataCallback<FindSATokenBean> callback) {
        HTTPCaller.getInstance().get(FindSATokenBean.class, HttpUrlConfig.getSAToken(userId)+ Constant.ONLY_SC, requestData, callback );
    }
}
