package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.AboutContract;
import com.yctc.zhiting.activity.model.AboutModel;

public class AboutPresenter extends BasePresenterImpl<AboutContract.View> implements AboutContract.Presenter {

    private AboutModel model;

    @Override
    public void init() {
        model = new AboutModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
