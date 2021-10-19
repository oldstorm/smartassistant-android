package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AddDeviceContract;
import com.yctc.zhiting.activity.contract.PluginDetailContract;
import com.yctc.zhiting.activity.model.AddDeviceModel;
import com.yctc.zhiting.activity.model.PluginDetailModel;
import com.yctc.zhiting.entity.scene.PluginDetailBean;

/**
 * 添加设备
 */
public class PluginDetailPresenter extends BasePresenterImpl<PluginDetailContract.View> implements PluginDetailContract.Presenter {

    private PluginDetailModel model;

    @Override
    public void init() {
        model = new PluginDetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getDetail(String id) {
        mView.showLoadingView();
        model.getDetail(id, new RequestDataCallback<PluginDetailBean>() {
            @Override
            public void onSuccess(PluginDetailBean obj) {
                super.onSuccess(obj);
                mView.hideLoadingView();
                mView.getDetailSuccess(obj);
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                mView.hideLoadingView();
                mView.getDetailFail(errorCode, errorMessage);
            }
        });
    }
}
