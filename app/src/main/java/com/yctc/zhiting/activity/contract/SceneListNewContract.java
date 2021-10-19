package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.scene.SceneListBean;


/**
 * 控制场景  场景选择
 */
public interface SceneListNewContract {
    interface Model {
        void getSceneList(RequestDataCallback<SceneListBean> callback);
    }

    interface View extends BaseView {
        void getSceneListSuccess(SceneListBean data);
        void getSceneListError(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getSceneList();
    }
}
