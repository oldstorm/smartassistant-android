package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.scene.SceneDetailBean;


/**
 * 创建场景
 */
public interface CreateSceneContract {
    interface Model {
        void createScene(String body, RequestDataCallback<Object> callback);
        void modifyScene(int id, String body, RequestDataCallback<Object> callback);
        void delScene(int id, RequestDataCallback<Object> callback);
        void getDetail(int id, RequestDataCallback<SceneDetailBean> callback);
    }

    interface View extends BaseView {
        void createSceneSuccess();
        void modifySceneSuccess();
        void delSuccess();
        void requestFail(int errorCode, String msg);
        void getDetailSuccess(SceneDetailBean sceneDetail);
        void getDetailFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void createScene(String body);
        void modifyScene(int id, String body);
        void delScene(int id);
        void getDetail(int id);
    }
}
