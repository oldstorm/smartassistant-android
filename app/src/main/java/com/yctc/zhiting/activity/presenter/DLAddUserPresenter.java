
package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.yctc.zhiting.activity.contract.DLAddUserContract;
import com.yctc.zhiting.activity.contract.DLLogContract;
import com.yctc.zhiting.activity.model.DLAddUserModel;
import com.yctc.zhiting.activity.model.DLLogModel;

public class DLAddUserPresenter extends BasePresenterImpl<DLAddUserContract.View> implements DLAddUserContract.Presenter {

    private DLAddUserModel model;

    @Override
    public void init() {
        model = new DLAddUserModel();
    }

    @Override
    public void clear() {
        model = null;
    }
}
