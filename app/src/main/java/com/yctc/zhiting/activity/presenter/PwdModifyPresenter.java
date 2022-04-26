package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.PwdModifyContract;
import com.yctc.zhiting.activity.model.PwdModifyModel;

public class PwdModifyPresenter extends BasePresenterImpl<PwdModifyContract.View> implements PwdModifyContract.Presenter {

    private PwdModifyModel model;

    @Override
    public void init() {
        model = new PwdModifyModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 修改密码
     * @param id
     * @param body
     */
    @Override
    public void updatePwd(int id, String body) {
        model.updatePwd(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.updatePwdSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.updatePwdFail(errorCode, errorMessage);
                }
            }
        });
    }
}
