package com.yctc.zhiting.fragment.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.fragment.contract.DLAuthWayFragmentContract;
import com.yctc.zhiting.fragment.model.DLAuthWayFragmentModel;

public class DLAuthWayFragmentPresenter extends BasePresenterImpl<DLAuthWayFragmentContract.View> implements DLAuthWayFragmentContract.Presenter {

    private DLAuthWayFragmentModel model;

    @Override
    public void init() {
        model = new DLAuthWayFragmentModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
