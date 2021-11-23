package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SoftwareUpgradeContract;
import com.yctc.zhiting.activity.model.SoftwareUpgradeModel;
import com.yctc.zhiting.entity.mine.CurrentVersionBean;
import com.yctc.zhiting.entity.mine.SoftWareVersionBean;

public class SoftwareUpgradePresenter extends BasePresenterImpl<SoftwareUpgradeContract.View> implements SoftwareUpgradeContract.Presenter {

    private SoftwareUpgradeModel model;

    @Override
    public void init() {
        model = new SoftwareUpgradeModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getCheckSoftWareVersion() {
        mView.showLoadingView();
        model.getCheckSoftWareVersion(new RequestDataCallback<SoftWareVersionBean>() {
            @Override
            public void onSuccess(SoftWareVersionBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.onCheckSoftWareVersionSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.onCheckSoftWareVersionFailed(errorCode, errorMessage);
                }
            }
        });
    }

    @Override
    public void postSoftWareUpgrade(SoftWareVersionBean request) {
//        mView.showLoadingView();
        model.postSoftWareUpgrade(request,new RequestDataCallback<String>() {
            @Override
            public void onSuccess(String obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.onSoftWareUpgradeSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.onSoftWareUpgradeFailed(errorCode, errorMessage);
                }
            }
        });
    }

    @Override
    public void getCurrentVersion() {
        mView.showLoadingView();
        model.getCurrentVersion(new RequestDataCallback<CurrentVersionBean>() {
            @Override
            public void onSuccess(CurrentVersionBean data) {
                super.onSuccess(data);
                if (mView != null) {
                    mView.onCurrentVersionSuccess(data);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.onCurrentVersionFailed(errorCode, errorMessage);
                }
            }
        });
    }
}
