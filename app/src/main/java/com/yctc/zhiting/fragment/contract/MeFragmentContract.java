package com.yctc.zhiting.fragment.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.ExtensionBean;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.LoginBean;
import com.yctc.zhiting.entity.mine.UploadFileBean;

import java.util.List;

public interface MeFragmentContract {
    interface Model {
        void updateMember(int id, String body, RequestDataCallback<Object> callback);
        void getSATokenBySC(int userId, List<NameValuePair> requestData, RequestDataCallback<FindSATokenBean> callback);
        void getExtensions(RequestDataCallback<ExtensionBean> callback);
        void checkSaToken(long id, RequestDataCallback<HomeCompanyBean> callback);

        void uploadAvatar(List<NameValuePair> requestData, RequestDataCallback<UploadFileBean> callback);

        void getSCUserInfo(int id, RequestDataCallback<LoginBean> callback);
    }

    interface View extends BaseView {
        void updateNameSuccess();
        void updateNameFail(int errorCode, String msg);
        void getSATokenSuccess(FindSATokenBean findSATokenBean);
        void getSATokenFail(int errorCode, String msg);

        void getExtensionsSuccess(List<String> list);
        void getExtensionsFail(int errorCode, String msg);
        void checkTokenSuccess(HomeCompanyBean homeCompanyBean);
        void checkTokenFail(int errorCode, String msg);

        void uploadAvatarSuccess(UploadFileBean uploadFileBean);
        void uploadAvatarFail(int errorCode, String msg);

        void getSCUserInfoSuccess(LoginBean loginBean);
        void getSCUserInfoFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void updateMember(int id, String body);
        void getSAToken(int userId, List<NameValuePair> requestData);
        void getExtensions();
        void checkSaToken(long id);

        void uploadAvatar(List<NameValuePair> requestData);

        void getSCUserInfo(int id);
    }
}
