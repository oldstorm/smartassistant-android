package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.BuildConfig;
import com.yctc.zhiting.activity.contract.AboutContract;
import com.yctc.zhiting.activity.model.AboutModel;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.AndroidAppVersionBean;
import com.yctc.zhiting.utils.UpdateApkUtil;

public class AboutPresenter extends BasePresenterImpl<AboutContract.View> implements AboutContract.Presenter {

    private AboutModel model;

    @Override
    public void init() {
        model = new AboutModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void checkAppVersionInfo() {
        if (model != null) {
            model.getAppVersionInfo(new RequestDataCallback<AndroidAppVersionBean>() {
                @Override
                public void onSuccess(AndroidAppVersionBean android) {
                    super.onSuccess(android);
                    if (mView != null) {
                        if (android != null) {
                            int updateType = AndroidUtil.checkUpdateInfo(android.isIs_force_update(), BuildConfig.VERSION_NAME, android.getMin_app_version(), android.getMax_app_version());
                            android.setUpdate_type(updateType);
                            mView.getAppVersionInfoSuccess(android);
                        }
                    }
                }

                @Override
                public void onFailed(int errorCode, String errorMessage) {
                    super.onFailed(errorCode, errorMessage);
                    if (mView != null) {
                        mView.getAppVersionInfoFailed(errorCode, errorMessage);;
                    }
                }
            });
        }
    }
}
