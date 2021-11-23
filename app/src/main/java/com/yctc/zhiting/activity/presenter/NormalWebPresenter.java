package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.NormalWebContract;
import com.yctc.zhiting.activity.model.NormalWebModel;

public class NormalWebPresenter extends BasePresenterImpl<NormalWebContract.View> implements NormalWebContract.Presenter {

    private NormalWebModel model;

    @Override
    public void init() {
        model = new NormalWebModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
