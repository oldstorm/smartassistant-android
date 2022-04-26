package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DLGuideContract;
import com.yctc.zhiting.activity.model.DLGuideModel;

public class DLGuidePresenter extends BasePresenterImpl<DLGuideContract.View> implements DLGuideContract.Presenter {

    private DLGuideModel model;

    @Override
    public void init() {
        model = new DLGuideModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
