package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DLUserDetailContract;
import com.yctc.zhiting.activity.model.DLUserDetailModel;

public class DLUserDetailPresenter extends BasePresenterImpl<DLUserDetailContract.View> implements DLUserDetailContract.Presenter {

    private DLUserDetailModel model;

    @Override
    public void init() {
        model = new DLUserDetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
