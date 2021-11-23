package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SupportBrandContract;
import com.yctc.zhiting.activity.model.SupportBrandModel;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

import java.util.List;

/**
 * 支持品牌
 */
public class SupportBrandPresenter extends BasePresenterImpl<SupportBrandContract.View> implements SupportBrandContract.Presenter {

    private SupportBrandModel model;

    @Override
    public void init() {
        model = new SupportBrandModel();
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

    @Override
    public void uploadPlugin(List<NameValuePair> requestData) {
        model.uploadPlugin(requestData, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    mView.uploadPluginSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.uploadPluginFail(errorCode, errorMessage);
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
