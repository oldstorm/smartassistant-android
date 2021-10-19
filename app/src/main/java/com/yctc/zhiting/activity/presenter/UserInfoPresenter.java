package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.UserInfoContract;
import com.yctc.zhiting.activity.model.UserInfoModel;

public class UserInfoPresenter extends BasePresenterImpl<UserInfoContract.View> implements UserInfoContract.Presenter {

    UserInfoModel model;

    @Override
    public void init() {
        model = new UserInfoModel();
    }

    @Override
    public void clear() {
        model = null;
    }


    /**
     * 修改昵称
     * @param id
     * @param body
     */
    @Override
    public void updateMember(int id, String body) {
        mView.showLoadingView();
        model.updateMember(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 修改sc昵称信息
     * @param id
     * @param body
     */
    @Override
    public void updateMemberSC(int id, String body) {
        model.updateMemberSC(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateScSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateScFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 退出登录
     */
    @Override
    public void logout() {
        if (mView!=null){
            mView.showLoadingView();
        }

        model.logout(new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.logoutSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }
}
