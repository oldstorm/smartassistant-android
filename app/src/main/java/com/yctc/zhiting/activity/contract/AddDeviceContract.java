package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.home.DeviceTypeListBean;
import com.yctc.zhiting.entity.mine.CheckBindSaBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;

import java.util.List;


/**
 * 添加设备
 */
public interface AddDeviceContract {
    interface Model {
        void checkBindSa(RequestDataCallback<CheckBindSaBean> callback);
        void getDeviceType(RequestDataCallback<DeviceTypeListBean> callback);
        void getPluginDetail(String id, RequestDataCallback<PluginDetailBean> callback);

        void getDeviceFirstType(RequestDataCallback<DeviceTypeListBean> callback);
        void getDeviceSecondType(List<NameValuePair> requestData, RequestDataCallback<DeviceTypeListBean> callback);
    }

    interface View extends BaseView {
        void checkBindSaSuccess(CheckBindSaBean data);
        void checkBindSaFail(int errorCode, String msg);
        void getDeviceTypeSuccess(DeviceTypeListBean deviceTypeListBean);
        void getDeviceTypeFail(int errorCode, String msg);
        void getPluginDetailSuccess(PluginDetailBean pluginDetailBean);
        void getPluginDetailFail(int errorCode, String msg);

        void getDeviceFirstTypeSuccess(DeviceTypeListBean deviceTypeListBean);
        void getDeviceFirstTypFail(int errorCode, String msg);
        void getDeviceSecondTypeSuccess(DeviceTypeListBean deviceTypeListBean);
        void getDeviceSecondTypeFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void checkBindSa();
        void getDeviceType();
        void getPluginDetail(String id);

        void getDeviceFirstType();
        void getDeviceSecondType(List<NameValuePair> requestData);
    }
}
