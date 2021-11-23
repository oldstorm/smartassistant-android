package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.AgreementAndPolicyContract;
import com.yctc.zhiting.activity.model.AgreementAndPolicyModel;

public class AgreementAndPolicyPresenter extends BasePresenterImpl<AgreementAndPolicyContract.View> implements AgreementAndPolicyContract.Presenter {

    private AgreementAndPolicyModel model;

    @Override
    public void init() {
        model = new AgreementAndPolicyModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
