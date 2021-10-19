package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.mine.PermissionBean;


/**
 * 设备详情
 */
public interface DeviceDetailContract {
    interface Model {
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
        void getDeviceDetail(int id, RequestDataCallback<DeviceDetailBean> callback); // 设备详情
        void getDeviceDetailRestructure(int id, RequestDataCallback<DeviceDetailResponseEntity> callback); // 设备详情，重构
    }

    interface View extends BaseView {
        void getPermissionsSuccess(PermissionBean permissionBean);
        void getDeviceDetailSuccess(DeviceDetailBean deviceDetailBean);
        void getDeviceDetailRestructureSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity); // 设备详情，重构
        void getFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getPermissions(int id);
        void getDeviceDetail(int id);
        void getDeviceDetailRestructure(int id); // 设备详情，重构
    }
}
