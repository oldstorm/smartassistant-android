package com.yctc.zhiting.fragment.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.CreatePluginListBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.fragment.contract.SBCreateContract;
import com.yctc.zhiting.fragment.contract.SBSystemContract;
import com.yctc.zhiting.fragment.model.SBCreateModel;
import com.yctc.zhiting.fragment.model.SBSystemModel;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

import java.util.List;

public class SBCreatePresenter extends BasePresenterImpl<SBCreateContract.View> implements SBCreateContract.Presenter {

    private SBCreateModel model;

    @Override
    public void init() {
        model = new SBCreateModel();
    }

    @Override
    public void clear() {
        model = null;
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
     * 添加插件
     * @param requestData
     */
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
