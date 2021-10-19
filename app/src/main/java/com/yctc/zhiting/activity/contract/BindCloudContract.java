package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.InvitationCodeBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

import java.util.List;

/**
 * 绑定云
 */
public interface BindCloudContract {
    interface Model {
        void getCaptcha(List<NameValuePair> requestData, RequestDataCallback<CaptchaBean> callback);
        void register(String body, RequestDataCallback<MemberDetailBean> callback);
    }

    interface View extends BaseView {
        void getCaptchaSuccess(CaptchaBean captchaBean);
        void getCaptchaFail(int errorCode, String msg);
        void registerSuccess(MemberDetailBean memberDetailBean);
        void registerFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getCaptcha(List<NameValuePair> requestData);
        void register(String body);
    }
}
