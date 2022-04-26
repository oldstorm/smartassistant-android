package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.ForgetPwdContract;
import com.yctc.zhiting.activity.model.ForgetModel;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.request.ForgetPwdRequest;

import java.util.List;

public class ForgetPwdPresenter extends BasePresenterImpl<ForgetPwdContract.View> implements ForgetPwdContract.Presenter {

    private ForgetModel model;

    @Override
    public void init() {
        model = new ForgetModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 验证码
     * @param requestData
     */
    @Override
    public void getCaptcha(List<NameValuePair> requestData) {
        if (mView!=null) {
            mView.showLoadingView();
        }
        model.getCaptcha(requestData, new RequestDataCallback<CaptchaBean>() {
            @Override
            public void onSuccess(CaptchaBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getCaptchaSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getCaptchaFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 忘记密码
     * @param forgetPwdRequest
     */
    @Override
    public void forgetPwdFail(ForgetPwdRequest forgetPwdRequest) {
        if (mView!=null) {
            mView.showLoadingView();
        }
        model.forgetPwd(forgetPwdRequest, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.forgetPwdSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.forgetPwdFail(errorCode, errorMessage);
                }
            }
        });
    }
}
