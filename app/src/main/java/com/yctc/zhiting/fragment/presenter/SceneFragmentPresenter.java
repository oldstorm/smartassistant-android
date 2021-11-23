package com.yctc.zhiting.fragment.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.scene.SceneListBean;
import com.yctc.zhiting.fragment.contract.SceneFragmentContract;
import com.yctc.zhiting.fragment.model.SceneFragmentModel;

import java.util.List;

public class SceneFragmentPresenter extends BasePresenterImpl<SceneFragmentContract.View> implements SceneFragmentContract.Presenter {
    SceneFragmentModel model;

    @Override
    public void init() {
        model = new SceneFragmentModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 场景列表
     */
    @Override
    public void getSceneList(boolean showLoading) {
        if (mView!=null && showLoading) {
            mView.showLoadingView();
        }
        if (model!=null)
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

    /**
     * 执行
     * @param id
     * @param execute
     */
    @Override
    public void perform(int id, boolean execute) {
        String body = "{\"is_execute\":"+ execute+"}";
        model.perform(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.performSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.performFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 用户权限
     * @param id
     */
    @Override
    public void getPermissions(int id) {
        if (model!=null) {
            model.getPermissions(id, new RequestDataCallback<PermissionBean>() {
                @Override
                public void onSuccess(PermissionBean obj) {
                    super.onSuccess(obj);
                    if (mView != null) {
                        mView.getPermissionsSuccess(obj);
                    }
                }

                @Override
                public void onFailed(int errorCode, String errorMessage) {
                    super.onFailed(errorCode, errorMessage);
                    if (mView != null) {
                        mView.onPermissionsFail(errorCode, errorMessage);
                    }
                }
            });
        }
    }

    /**
     * 通过sc找回sa的用户凭证
     * @param userId
     * @param requestData
     */
    @Override
    public void getSAToken(int userId, List<NameValuePair> requestData) {
        model.getSATokenBySC(userId, requestData, new RequestDataCallback<FindSATokenBean>() {
            @Override
            public void onSuccess(FindSATokenBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getSATokenSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.getSATokenFail(errorCode, errorMessage);
                }
            }
        });
    }
}
