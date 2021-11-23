package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.SupportBrand2Contract;
import com.yctc.zhiting.activity.model.SupportBrand2Model;

public class SupportBrand2Presenter extends BasePresenterImpl<SupportBrand2Contract.View> implements SupportBrand2Contract.Presenter {

    private SupportBrand2Model model;

    @Override
    public void init() {
        model = new SupportBrand2Model();
    }

    @Override
    public void clear() {
        model = null;
    }
}
