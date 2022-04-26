package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.LoginContract;
import com.yctc.zhiting.activity.model.LoginModel;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.entity.mine.LoginBean;

import java.util.List;

/**
 * 登录
 */
public class LoginPresenter extends BasePresenterImpl<LoginContract.View> implements LoginContract.Presenter {

    private LoginModel model;

    @Override
    public void init() {
        model = new LoginModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 登录
     *
     * @param body
     */
    @Override
    public void login(String body) {
        model.login(body, new RequestDataCallback<LoginBean>() {
            @Override
            public void onSuccess(LoginBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.loginSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.loginFail(errorCode, errorMessage);
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
}
