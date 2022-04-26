package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AvatarContract;
import com.yctc.zhiting.activity.model.AvatarModel;
import com.yctc.zhiting.entity.mine.UploadFileBean;

import java.util.List;

public class AvatarPresenter extends BasePresenterImpl<AvatarContract.View> implements AvatarContract.Presenter {

    private AvatarModel model;

    @Override
    public void init() {
        model = new AvatarModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 上传头像
     * @param requestData
     * @param sc
     */
    @Override
    public void uploadAvatar(List<NameValuePair> requestData, boolean sc) {
        model.uploadAvatar(requestData,new RequestDataCallback<UploadFileBean>() {
            @Override
            public void onSuccess(UploadFileBean obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    mView.uploadAvatarSuccess(obj);
                }

            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.uploadAvatarFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 上传SC头像
     * @param requestData
     * @param sc
     */
    @Override
    public void uploadAvatarSC(List<NameValuePair> requestData, boolean sc) {
        model.uploadAvatarSC(requestData,new RequestDataCallback<UploadFileBean>() {
            @Override
            public void onSuccess(UploadFileBean obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    mView.uploadAvatarSCSuccess(obj);
                }

            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.uploadAvatarSCFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 修改头像
     * @param id
     * @param body
     */
    @Override
    public void updateMember(int id, String body) {
        mView.showLoadingView();
        model.updateMember(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 修改sc头像信息
     * @param id
     * @param body
     */
    @Override
    public void updateMemberSC(int id, String body) {
        model.updateMemberSC(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateScSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateScFail(errorCode, errorMessage);
                }
            }
        });
    }
}
