package com.yctc.zhiting.fragment.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.fragment.contract.MeFragmentContract;
import com.yctc.zhiting.fragment.model.MeFragmentModel;

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
}
