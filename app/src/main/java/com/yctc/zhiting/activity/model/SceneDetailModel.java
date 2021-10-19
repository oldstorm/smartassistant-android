package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneDetailContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.scene.SceneDetailBean;
import com.yctc.zhiting.entity.scene.SceneDetailEntity;

public class SceneDetailModel implements SceneDetailContract.Model {
    /**
     * 创建场景
     * @param body
     * @param callback
     */
    @Override
    public void createScene(String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getScene(), body, callback);
    }

    /**
     * 修改场景
     * @param body
     * @param callback
     */
    @Override
    public void modifyScene(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getScene()+"/"+id, body, callback);
    }

    /**
     * 删除
     * @param id
     * @param callback
     */
    @Override
    public void delScene(int id, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().delete(Object.class, HttpUrlConfig.getScene()+"/"+id, "", callback);
    }

    /**
     * 场景详情
     * @param id
     * @param callback
     */
    @Override
    public void getDetail(int id, RequestDataCallback<SceneDetailEntity> callback) {
        HTTPCaller.getInstance().get(SceneDetailEntity.class, HttpUrlConfig.getScene()+"/"+id, callback);
    }
}
