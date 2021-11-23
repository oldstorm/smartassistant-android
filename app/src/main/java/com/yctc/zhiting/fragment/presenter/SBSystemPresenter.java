package com.yctc.zhiting.fragment.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.fragment.contract.SBSystemContract;
import com.yctc.zhiting.fragment.model.SBSystemModel;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

import java.util.List;

public class SBSystemPresenter extends BasePresenterImpl<SBSystemContract.View> implements SBSystemContract.Presenter {

    private SBSystemModel model;

    @Override
    public void init() {
        model = new SBSystemModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getBrandList(List<NameValuePair> requestData, boolean showLoading) {
        if (showLoading)
            mView.showLoadingView();
        model.getBrandList(requestData, new RequestDataCallback<BrandListBean>() {
            @Override
            public void onSuccess(BrandListBean obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.getBrandListSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.getBrandListFail(errorCode, errorMessage);
                }
            }
        });
    }



    /**
     * 添加更新插件
     * @param addOrUpdatePluginRequest
     * @param name
     * @param position
     */
    @Override
    public void addOrUpdatePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, int position) {
        model.addOrUpdatePlugins(addOrUpdatePluginRequest, name, new RequestDataCallback<OperatePluginBean>() {
            @Override
            public void onSuccess(OperatePluginBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.addOrUpdatePluginsSuccess(obj, position);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.addOrUpdatePluginsFail(errorCode, errorMessage, position);
                }
            }
        });
    }
}
