package com.yctc.zhiting.fragment.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;

public interface SceneItemFragmentContract {

    interface Model {
        void perform(int id, String body, RequestDataCallback<Object> callback);
        void orderScene(String scene_ids, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void performSuccess(int position);
        void performFail(int errorCode, int position, String msg);

        void orderSceneSuccess();
        void orderSceneFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<SceneItemFragmentContract.View> {
        void perform(int id, int position, boolean execute);
        void orderScene(String scene_ids);
    }
}
