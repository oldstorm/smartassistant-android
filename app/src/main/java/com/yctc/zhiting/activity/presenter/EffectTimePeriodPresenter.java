package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.EffectTimePeriodContract;
import com.yctc.zhiting.activity.model.EffectTimePeriodModel;

/**
 * 生效时段
 */
public class EffectTimePeriodPresenter extends BasePresenterImpl<EffectTimePeriodContract.View> implements EffectTimePeriodContract.Presenter {

    private EffectTimePeriodModel model;

    @Override
    public void init() {
        model = new EffectTimePeriodModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
