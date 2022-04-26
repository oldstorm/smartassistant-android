package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.yctc.zhiting.activity.contract.AddDeviceContract;
import com.yctc.zhiting.activity.contract.AddHCContract;
import com.yctc.zhiting.activity.model.AddDeviceModel;
import com.yctc.zhiting.activity.model.AddHCModel;
import com.yctc.zhiting.entity.home.DeviceTypeListBean;
import com.yctc.zhiting.entity.mine.CheckBindSaBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;

import java.util.List;

/**
 * 添加设备
 */
public class AddDevicePresenter extends BasePresenterImpl<AddDeviceContract.View> implements AddDeviceContract.Presenter {

    private AddDeviceModel model;

    @Override
    public void init() {
        model = new AddDeviceModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 检查SA绑定情况
     */
    @Override
    public void checkBindSa() {
        HttpConfig.clearHear(HttpConfig.AREA_ID);
        model.checkBindSa(new RequestDataCallback<CheckBindSaBean>() {
            @Override
            public void onSuccess(CheckBindSaBean data) {
                super.onSuccess(data);
                if (mView != null) {
                    mView.checkBindSaSuccess(data);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.checkBindSaFail(errorCode,errorMessage);
                }
            }
        });
    }

    /**
     * 获取设备分类列表
     */
    @Override
    public void getDeviceType() {
        model.getDeviceType(new RequestDataCallback<DeviceTypeListBean>() {
            @Override
            public void onSuccess(DeviceTypeListBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getDeviceTypeSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getDeviceTypeFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 插件详情
     * @param id
     */
    @Override
    public void getPluginDetail(String id) {
        model.getPluginDetail(id, new RequestDataCallback<PluginDetailBean>() {
            @Override
            public void onSuccess(PluginDetailBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getPluginDetailSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getPluginDetailFail(errorCode,errorMessage);
                }
            }
        });
    }

    /**
     * 一级分类
     */
    @Override
    public void getDeviceFirstType() {
        model.getDeviceFirstType(new RequestDataCallback<DeviceTypeListBean>() {
            @Override
            public void onSuccess(DeviceTypeListBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getDeviceFirstTypeSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getDeviceFirstTypFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 二级分类
     */
    @Override
    public void getDeviceSecondType(List<NameValuePair> requestData) {
        model.getDeviceSecondType(requestData, new RequestDataCallback<DeviceTypeListBean>() {
            @Override
            public void onSuccess(DeviceTypeListBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getDeviceSecondTypeSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getDeviceSecondTypeFail(errorCode, errorMessage);
                }
            }
        });
    }
}
