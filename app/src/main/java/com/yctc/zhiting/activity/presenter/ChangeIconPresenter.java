package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ChangeIconContract;
import com.yctc.zhiting.activity.model.ChangeIconModel;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.home.DeviceLogoListBean;

public class ChangeIconPresenter extends BasePresenterImpl<ChangeIconContract.View> implements ChangeIconContract.Presenter {

    private ChangeIconModel model;

    @Override
    public void init() {
        model = new ChangeIconModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 设备图标
     * @param id
     */
    @Override
    public void getLogoList(int id) {
        if (mView!=null) {
            mView.showLoadingView();
        }
        model.getLogoList(id, new RequestDataCallback<DeviceLogoListBean>() {
            @Override
            public void onSuccess(DeviceLogoListBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getLogoListSuccess(obj);
                    mView.hideLoadingView();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.getLogoListFail(errorCode, errorMessage);
                    mView.hideLoadingView();
                }
            }
        });
    }

    /**
     * 修改图标
     * @param id
     * @param logoType
     */
    @Override
    public void updateType(int id, int logoType) {
        String body = "{\"logo_type\":"+ logoType+"}";
        model.updateType(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if(mView!=null) {
                    mView.updateTypeSuccess();;
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.updateTypeFail(errorCode, errorMessage);
                }
            }
        });
    }
}
