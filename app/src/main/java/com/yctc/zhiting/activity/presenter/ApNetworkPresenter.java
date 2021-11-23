package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.ApNetworkContract;
import com.yctc.zhiting.activity.contract.SetHomekitContract;
import com.yctc.zhiting.activity.model.ApNetworkModel;
import com.yctc.zhiting.activity.model.SetHomekitModel;

public class ApNetworkPresenter extends BasePresenterImpl<ApNetworkContract.View> implements ApNetworkContract.Presenter{

    private ApNetworkModel model;

    @Override
    public void init() {
        model = new ApNetworkModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
