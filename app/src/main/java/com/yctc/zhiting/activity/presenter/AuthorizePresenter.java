package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AuthorizeContract;
import com.yctc.zhiting.activity.model.AuthorizeModel;
import com.yctc.zhiting.entity.ScopeTokenBean;
import com.yctc.zhiting.entity.ScopesBean;

public class AuthorizePresenter extends BasePresenterImpl<AuthorizeContract.View> implements AuthorizeContract.Presenter {

    private AuthorizeModel model;

    @Override
    public void init() {
        model = new AuthorizeModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 获取 SCOPE 列表
     */
    @Override
    public void getScopeList() {
        if (mView!=null){
            mView.showLoadingView();
        }
        model.getScopeList(new RequestDataCallback<ScopesBean>() {
            @Override
            public void onSuccess(ScopesBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getScopesSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getScopesFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 获取 SCOPE Token
     * @param body
     */
    @Override
    public void getScopeToken(String body) {
        if (mView!=null){
            mView.showLoadingView();
        }

        model.getScopeToken(body, new RequestDataCallback<ScopeTokenBean>() {
            @Override
            public void onSuccess(ScopeTokenBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getScopeTokenSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getScopeTokenFail(errorCode, errorMessage);
                }
            }
        });
    }
}
