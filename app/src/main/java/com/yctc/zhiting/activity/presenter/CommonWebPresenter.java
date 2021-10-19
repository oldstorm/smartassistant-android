package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.CommonWebContract;
import com.yctc.zhiting.activity.contract.SupportBrandContract;
import com.yctc.zhiting.activity.model.CommonWebModel;
import com.yctc.zhiting.activity.model.SupportBrandModel;

public class CommonWebPresenter extends BasePresenterImpl<CommonWebContract.View> implements CommonWebContract.Presenter {

    private CommonWebModel model;

    @Override
    public void init() {
        model = new CommonWebModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
