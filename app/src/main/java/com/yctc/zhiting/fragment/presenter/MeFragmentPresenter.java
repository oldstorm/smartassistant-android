package com.yctc.zhiting.fragment.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.ExtensionBean;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.LoginBean;
import com.yctc.zhiting.entity.mine.UploadFileBean;
import com.yctc.zhiting.fragment.contract.MeFragmentContract;
import com.yctc.zhiting.fragment.model.MeFragmentModel;

import java.util.List;

public class MeFragmentPresenter extends BasePresenterImpl<MeFragmentContract.View> implements MeFragmentContract.Presenter {

    MeFragmentModel model;

    @Override
    public void init() {
        model = new MeFragmentModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 修改昵称
     *
     * @param id
     * @param body
     */
    @Override
    public void updateMember(int id, String body) {
        model.updateMember(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateNameSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateNameFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 通过sc找回sa的用户凭证
     *
     * @param userId
     * @param requestData
     */
    @Override
    public void getSAToken(int userId, List<NameValuePair> requestData) {
        model.getSATokenBySC(userId, requestData, new RequestDataCallback<FindSATokenBean>() {
            @Override
            public void onSuccess(FindSATokenBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getSATokenSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getSATokenFail(errorCode, errorMessage);
                }
            }
        });
    }

    @Override
    public void getExtensions() {
        model.getExtensions(new RequestDataCallback<ExtensionBean>() {
            @Override
            public void onSuccess(ExtensionBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getExtensionsSuccess(obj.getExtension_names());
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getExtensionsFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 家庭详情
     *
     * @param id
     */
    @Override
    public void checkSaToken(long id) {
        model.checkSaToken(id, new RequestDataCallback<HomeCompanyBean>() {
            @Override
            public void onSuccess(HomeCompanyBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.checkTokenSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.checkTokenFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 上传头像
     * @param requestData
     */
    @Override
    public void uploadAvatar(List<NameValuePair> requestData) {
        model.uploadAvatar(requestData,  new RequestDataCallback<UploadFileBean>() {
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
     * 用户信息
     * @param id
     */
    @Override
    public void getSCUserInfo(int id) {
        model.getSCUserInfo(id, new RequestDataCallback<LoginBean>() {
            @Override
            public void onSuccess(LoginBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getSCUserInfoSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getSCUserInfoFail(errorCode, errorMessage);
                }
            }
        });
    }

}
