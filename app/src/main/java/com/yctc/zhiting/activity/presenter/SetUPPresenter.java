package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.CommonWebContract;
import com.yctc.zhiting.activity.contract.SetUPContract;
import com.yctc.zhiting.activity.model.CommonWebModel;
import com.yctc.zhiting.activity.model.SetUpModel;

public class SetUPPresenter extends BasePresenterImpl<SetUPContract.View> implements SetUPContract.Presenter {

    private SetUpModel model;

    @Override
    public void init() {
        model = new SetUpModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 修改成员
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
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }
}
