package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DLAddPwdContract;
import com.yctc.zhiting.activity.model.DLAddPwdModel;

public class DLAddPwdPresenter extends BasePresenterImpl<DLAddPwdContract.View> implements DLAddPwdContract.Presenter {

    private DLAddPwdModel model;

    @Override
    public void init() {
        model = new DLAddPwdModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
