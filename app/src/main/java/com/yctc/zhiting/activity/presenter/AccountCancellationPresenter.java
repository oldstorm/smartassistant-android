package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AccountAndSecurityContract;
import com.yctc.zhiting.activity.contract.AccountCancellationContract;
import com.yctc.zhiting.activity.model.AccountAndSecurityModel;
import com.yctc.zhiting.activity.model.AccountCancellationModel;
import com.yctc.zhiting.entity.UnregisterAreasBean;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.request.UnregisterUserRequest;

import java.util.List;

public class AccountCancellationPresenter extends BasePresenterImpl<AccountCancellationContract.View> implements AccountCancellationContract.Presenter {

    private AccountCancellationModel model;

    @Override
    public void init() {
        model = new AccountCancellationModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 要删除/退出的家庭
     *
     * @param userId
     */
    @Override
    public void getAreaList(int userId) {
        if (mView != null) {
            mView.showLoadingView();
        }
        model.getAreaList(userId, new RequestDataCallback<UnregisterAreasBean>() {
            @Override
            public void onSuccess(UnregisterAreasBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getAreaListSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getAreaListFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 验证码
     *
     * @param requestData
     */
    @Override
    public void getCaptcha(List<NameValuePair> requestData) {
        if (mView != null) {
            mView.showLoadingView();
        }
        model.getCaptcha(requestData, new RequestDataCallback<CaptchaBean>() {
            @Override
            public void onSuccess(CaptchaBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getCaptchaSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getCaptchaFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 注销
     * @param user_id
     * @param unregisterUserRequest
     */
    @Override
    public void unregisterUser(int user_id, UnregisterUserRequest unregisterUserRequest) {
        if (mView != null) {
            mView.showLoadingView();
        }
        model.unregisterUser(user_id, unregisterUserRequest, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.unregisterUserSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.unregisterUserFail(errorCode, errorMessage);
                }
            }
        });
    }
}
