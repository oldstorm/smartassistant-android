package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.UploadFileBean;

import java.util.List;

public interface AvatarContract {

    interface Model {
        void uploadAvatar(List<NameValuePair> requestData, RequestDataCallback<UploadFileBean> callback);
        void uploadAvatarSC(List<NameValuePair> requestData, RequestDataCallback<UploadFileBean> callback);
        void updateMember(int id, String body, RequestDataCallback<Object> callback);
        void updateMemberSC(int id, String body, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void uploadAvatarSuccess(UploadFileBean uploadFileBean);
        void uploadAvatarFail(int errorCode, String msg);
        void uploadAvatarSCSuccess(UploadFileBean uploadFileBean);
        void uploadAvatarSCFail(int errorCode, String msg);
        void updateSuccess();
        void updateFail(int errorCode, String msg);
        void updateScSuccess();
        void updateScFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<AvatarContract.View> {
        void uploadAvatar(List<NameValuePair> requestData, boolean sc);
        void uploadAvatarSC(List<NameValuePair> requestData, boolean sc);
        void updateMember(int id, String body);
        void updateMemberSC(int id, String body);
    }
}
