package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.activity.model.BrandDetailModel;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.mine.BrandDetailBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

/**
 * 品牌详情
 */
public class BrandDetailPresenter extends BasePresenterImpl<BrandDetailContract.View> implements BrandDetailContract.Presenter {

    private BrandDetailModel model;

    @Override
    public void init() {
        model = new BrandDetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getDetail(String name) {
        mView.showLoadingView();
        model.getDetail(name, new RequestDataCallback<BrandDetailBean>() {
            @Override
            public void onSuccess(BrandDetailBean obj) {
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
     * 删除插件
     * @param addOrUpdatePluginRequest
     * @param name
     * @param position
     */
    @Override
    public void removePlugins(AddOrUpdatePluginRequest addOrUpdatePluginRequest, String name, int position) {
        model.removePlugins(addOrUpdatePluginRequest, name, new RequestDataCallback<OperatePluginBean>() {
            @Override
            public void onSuccess(OperatePluginBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.removePluginsSuccess(obj, position);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.removePluginsFail(errorCode, errorMessage, position);
                }
            }
        });
    }
}
