package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.UnregisterAreasBean;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.request.UnregisterUserRequest;

import java.util.List;

public interface AccountCancellationContract {
    interface Model {
        void getAreaList(int userId, RequestDataCallback<UnregisterAreasBean> callback);
        void getCaptcha(List<NameValuePair> requestData, RequestDataCallback<CaptchaBean> callback);
        void unregisterUser(int user_id, UnregisterUserRequest unregisterUserRequest, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void getAreaListSuccess(UnregisterAreasBean unregisterAreasBean);
        void getAreaListFail(int errorCode, String msg);
        void getCaptchaSuccess(CaptchaBean captchaBean);
        void getCaptchaFail(int errorCode, String msg);
        void unregisterUserSuccess();
        void unregisterUserFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getAreaList(int userId);
        void getCaptcha(List<NameValuePair> requestData);
        void unregisterUser(int user_id, UnregisterUserRequest unregisterUserRequest);

    }
}
