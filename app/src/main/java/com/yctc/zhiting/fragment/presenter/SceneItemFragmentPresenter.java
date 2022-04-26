package com.yctc.zhiting.fragment.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.fragment.contract.SceneItemFragmentContract;
import com.yctc.zhiting.fragment.model.SceneItemFragmentModel;

public class SceneItemFragmentPresenter extends BasePresenterImpl<SceneItemFragmentContract.View> implements SceneItemFragmentContract.Presenter {

    private SceneItemFragmentModel model;

    @Override
    public void init() {
        model = new SceneItemFragmentModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 执行
     *
     * @param id
     * @param execute
     */
    @Override
    public void perform(int id, int position, boolean execute) {
        String body = "{\"is_execute\":" + execute + "}";
        model.perform(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.performSuccess(position);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.performFail(errorCode, position, errorMessage);
                }
            }
        });
    }

    /**
     * 排序
     *
     * @param scene_ids
     */
    @Override
    public void orderScene(String scene_ids) {
        if (mView != null) {
            mView.showLoadingView();
        }
        model.orderScene(scene_ids, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.orderSceneSuccess();
                    mView.hideLoadingView();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.orderSceneFail(errorCode, errorMessage);
                    mView.hideLoadingView();
                }
            }
        });
    }
}
