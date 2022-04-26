package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DoorLockDetailContract;
import com.yctc.zhiting.activity.model.DoorLockDetailModel;

public class DoorLockDetailPresenter extends BasePresenterImpl<DoorLockDetailContract.View> implements DoorLockDetailContract.Presenter{

    private DoorLockDetailModel model;

    @Override
    public void init() {
        model = new DoorLockDetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
