package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DLAddPwdSuccessContract;
import com.yctc.zhiting.activity.model.DLAddPwdSuccessModel;

public class DLAddPwdSuccessPresenter extends BasePresenterImpl<DLAddPwdSuccessContract.View> implements DLAddPwdSuccessContract.Presenter {

    private DLAddPwdSuccessModel model;

    @Override
    public void init() {
        model = new DLAddPwdSuccessModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
