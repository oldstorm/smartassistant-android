package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.UserManageContract;
import com.yctc.zhiting.activity.model.UserManageModel;

public class UserManagePresenter extends BasePresenterImpl<UserManageContract.View> implements UserManageContract.Presenter {

    private UserManageModel model;

    @Override
    public void init() {
        model = new UserManageModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
