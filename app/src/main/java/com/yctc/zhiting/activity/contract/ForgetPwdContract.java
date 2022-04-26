package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.request.ForgetPwdRequest;

import java.util.List;

public interface ForgetPwdContract {
    interface Model {
        void getCaptcha(List<NameValuePair> requestData, RequestDataCallback<CaptchaBean> callback);
        void forgetPwd(ForgetPwdRequest forgetPwdRequest, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void getCaptchaSuccess(CaptchaBean captchaBean);
        void getCaptchaFail(int errorCode, String msg);
        void forgetPwdSuccess();
        void forgetPwdFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getCaptcha(List<NameValuePair> requestData);
        void forgetPwdFail(ForgetPwdRequest forgetPwdRequest);
    }
}
