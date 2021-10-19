package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.CreateSceneContract;
import com.yctc.zhiting.activity.contract.SceneDetailContract;
import com.yctc.zhiting.activity.model.SceneDetailModel;
import com.yctc.zhiting.entity.scene.SceneDetailBean;
import com.yctc.zhiting.entity.scene.SceneDetailEntity;

public class SceneDetailPresenter  extends BasePresenterImpl<SceneDetailContract.View> implements SceneDetailContract.Presenter {

    private SceneDetailModel model;

    @Override
    public void init() {
        model = new SceneDetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 创建场景
     * @param body
     */
    @Override
    public void createScene(String body) {
        model.createScene(body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.createSceneSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 修改场景
     * @param body
     */
    @Override
    public void modifyScene(int id, String body) {
        model.modifyScene(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.modifySceneSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delScene(int id) {
        model.delScene(id, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.delSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 场景详情
     * @param id
     */
    @Override
    public void getDetail(int id) {
        mView.showLoadingView();
        model.getDetail(id, new RequestDataCallback<SceneDetailEntity>() {
            @Override
            public void onSuccess(SceneDetailEntity obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getDetailSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getDetailFail(errorCode, errorMessage);
                }
            }
        });
    }
}
