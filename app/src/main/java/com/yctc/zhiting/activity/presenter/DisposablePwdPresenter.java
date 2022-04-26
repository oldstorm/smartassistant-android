package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DisposablePwdContract;
import com.yctc.zhiting.activity.model.DisposablePwdModel;

public class DisposablePwdPresenter extends BasePresenterImpl<DisposablePwdContract.View> implements DisposablePwdContract.Presenter {

    private DisposablePwdModel model;

    @Override
    public void init() {
        model = new DisposablePwdModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
