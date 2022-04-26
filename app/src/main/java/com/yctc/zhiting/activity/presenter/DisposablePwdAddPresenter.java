package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DisposablePwdAddContract;
import com.yctc.zhiting.activity.model.DisposableAddPwdModel;

public class DisposablePwdAddPresenter extends BasePresenterImpl<DisposablePwdAddContract.View> implements DisposablePwdAddContract.Presenter {

    private DisposableAddPwdModel model;

    @Override
    public void init() {
        model = new DisposableAddPwdModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
