package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DLLogContract;
import com.yctc.zhiting.activity.model.DLLogModel;

public class DLLogPresenter extends BasePresenterImpl<DLLogContract.View> implements DLLogContract.Presenter {

    private DLLogModel model;

    @Override
    public void init() {
        model = new DLLogModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
