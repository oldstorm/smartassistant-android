package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.CommonWebContract;
import com.yctc.zhiting.activity.contract.CreatePluginDetailContract;
import com.yctc.zhiting.activity.model.CommonWebModel;
import com.yctc.zhiting.activity.model.CreatePluginDetailModel;
import com.yctc.zhiting.entity.scene.PluginDetailBean;

public class CreatePluginDetailPresenter extends BasePresenterImpl<CreatePluginDetailContract.View> implements CreatePluginDetailContract.Presenter {

    private CreatePluginDetailModel model;

    @Override
    public void init() {
        model = new CreatePluginDetailModel();
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

    /**
     * 删除插件
     * @param id
     */
    @Override
    public void removePlugin(String id) {
        if (mView!=null)
        mView.showLoadingView();
        model.removePlugin(id, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.removePluginSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.removePluginFail(errorCode, errorMessage);
                }
            }
        });
    }
}
