
package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DLAddUserContract;
import com.yctc.zhiting.activity.contract.DLAuthWayContract;
import com.yctc.zhiting.activity.model.DLAddUserModel;
import com.yctc.zhiting.activity.model.DLAuthWayModel;

public class DLAuthWayPresenter extends BasePresenterImpl<DLAuthWayContract.View> implements DLAuthWayContract.Presenter {

    private DLAuthWayModel model;

    @Override
    public void init() {
        model = new DLAuthWayModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
