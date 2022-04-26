package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.MainContract;
import com.yctc.zhiting.activity.model.MainModel;

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {
    MainModel model;

    @Override
    public void init() {
        model = new MainModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
