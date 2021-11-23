package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SBSearchContract;
import com.yctc.zhiting.activity.model.SBSearchModel;
import com.yctc.zhiting.entity.CreatePluginListBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.fragment.model.SBSystemModel;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

import java.util.List;

public class SBSearchPresenter extends BasePresenterImpl<SBSearchContract.View> implements SBSearchContract.Presenter {

    private SBSearchModel model;

    @Override
    public void init() {
        model = new SBSearchModel();
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

    /**
     * 插件列表
     * @param requestData
     * @param showLoading
     */
    @Override
    public void getCreateList(List<NameValuePair> requestData, boolean showLoading) {
        if (showLoading){
            if (mView!=null){
                mView.showLoadingView();
            }
        }
        model.getCreateList(requestData, new RequestDataCallback<CreatePluginListBean>() {
            @Override
            public void onSuccess(CreatePluginListBean obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.getCreateListSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.getCreateListFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 删除插件
     * @param id
     * @param position
     */
    @Override
    public void removePlugin(String id, int position) {
        model.removePlugin(id, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.removePluginSuccess(position);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.removePluginFail(errorCode, errorMessage, position);
                }
            }
        });
    }
}
