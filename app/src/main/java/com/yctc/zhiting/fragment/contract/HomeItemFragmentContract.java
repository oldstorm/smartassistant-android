package com.yctc.zhiting.fragment.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.home.BrandBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.RequestAddDeviceBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;

public interface HomeItemFragmentContract {

    interface Model {
        void getBrandList(RequestDataCallback<BrandBean> callback);
        void postAddDevice(RequestAddDeviceBean bean, RequestDataCallback<DeviceBean> callback);
        void getPluginDetail(String id, RequestDataCallback<PluginDetailBean> callback);
    }

    interface View extends BaseView {
//        void getBrandListSuccess(BrandBean data);
//
//        void getBrandListError(String msg);
        void getPluginDetailSuccess(PluginDetailBean pluginDetailBean);
        void getPluginDetailFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getBrandList();
        void postAddDevice();
        void getPluginDetail(String id);
    }
}
