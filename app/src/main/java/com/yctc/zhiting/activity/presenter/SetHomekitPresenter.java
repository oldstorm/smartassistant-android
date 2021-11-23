package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.SetHomekitContract;
import com.yctc.zhiting.activity.model.SetHomekitModel;

public class SetHomekitPresenter extends BasePresenterImpl<SetHomekitContract.View> implements SetHomekitContract.Presenter{

    private SetHomekitModel model;

    @Override
    public void init() {
        model = new SetHomekitModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
