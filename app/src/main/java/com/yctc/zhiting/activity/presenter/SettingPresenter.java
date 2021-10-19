package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SettingContract;
import com.yctc.zhiting.activity.model.SettingModel;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

/**
 * 设置
 */
public class SettingPresenter extends BasePresenterImpl<SettingContract.View> implements SettingContract.Presenter {

    private SettingModel model;

    @Override
    public void init() {
        model = new SettingModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getUserInfo(int id) {
        mView.showLoadingView();
        model.getUserInfo(id, new RequestDataCallback<MemberDetailBean>() {
            @Override
            public void onSuccess(MemberDetailBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getUserInfoSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getUserInfoFail(errorCode, errorMessage);
                }
            }
        });
    }
}
