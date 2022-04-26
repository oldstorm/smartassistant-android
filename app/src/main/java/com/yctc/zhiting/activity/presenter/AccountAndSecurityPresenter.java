package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.AboutContract;
import com.yctc.zhiting.activity.contract.AccountAndSecurityContract;
import com.yctc.zhiting.activity.model.AboutModel;
import com.yctc.zhiting.activity.model.AccountAndSecurityModel;

public class AccountAndSecurityPresenter extends BasePresenterImpl<AccountAndSecurityContract.View> implements AccountAndSecurityContract.Presenter {

    private AccountAndSecurityModel model;

    @Override
    public void init() {
        model = new AccountAndSecurityModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
