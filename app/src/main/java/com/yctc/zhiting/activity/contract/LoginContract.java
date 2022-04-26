package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.entity.mine.LoginBean;

import java.util.List;

/**
 * 登录
 */
public interface LoginContract {
    interface Model {
        void login(String body, RequestDataCallback<LoginBean> callback);

        void getCaptcha(List<NameValuePair> requestData, RequestDataCallback<CaptchaBean> callback);
    }

    interface View extends BaseView {
        void loginSuccess(LoginBean loginBean);

        void loginFail(int errorCode, String msg);

        void getCaptchaSuccess(CaptchaBean captchaBean);

        void getCaptchaFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void login(String body);

        void getCaptcha(List<NameValuePair> requestData);
    }
}
