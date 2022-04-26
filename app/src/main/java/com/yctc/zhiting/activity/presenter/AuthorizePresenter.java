package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AuthorizeContract;
import com.yctc.zhiting.activity.model.AuthorizeModel;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.ScopeTokenBean;
import com.yctc.zhiting.entity.ScopesBean;

import java.util.List;

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
        model.getScopeList(new RequestDataCallback<ScopesBean>() {
            @Override
            public void onSuccess(ScopesBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getScopesSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
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
        model.getScopeToken(body, new RequestDataCallback<ScopeTokenBean>() {
            @Override
            public void onSuccess(ScopeTokenBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getScopeTokenSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.getScopeTokenFail(errorCode, errorMessage);
                }
            }
        });
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
