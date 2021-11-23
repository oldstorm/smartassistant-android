package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AddDeviceContract;
import com.yctc.zhiting.activity.contract.PluginDetailContract;
import com.yctc.zhiting.activity.model.AddDeviceModel;
import com.yctc.zhiting.activity.model.PluginDetailModel;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;

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
