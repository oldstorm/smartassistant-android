package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneListContract;
import com.yctc.zhiting.activity.contract.SceneListNewContract;
import com.yctc.zhiting.activity.model.SceneListModel;
import com.yctc.zhiting.activity.model.SceneListNewModel;
import com.yctc.zhiting.entity.scene.SceneListBean;

/**
 * 控制场景  场景选择
 */
public class SceneListNewPresenter extends BasePresenterImpl<SceneListNewContract.View> implements SceneListNewContract.Presenter {

    private SceneListNewModel model;

    @Override
    public void init() {
        model = new SceneListNewModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 场景列表
     */
    @Override
    public void getSceneList() {
        mView.showLoadingView();
        model.getSceneList(new RequestDataCallback<SceneListBean>() {
            @Override
            public void onSuccess(SceneListBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getSceneListSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getSceneListError(errorCode, errorMessage);
                }
            }
        });
    }
}
