package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DLAddPwdFailContract;
import com.yctc.zhiting.activity.model.DLAddPwdFailModel;

public class DLAddPwdFailPresenter extends BasePresenterImpl<DLAddPwdFailContract.View> implements DLAddPwdFailContract.Presenter {

    private DLAddPwdFailModel model;

    @Override
    public void init() {
        model = new DLAddPwdFailModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
