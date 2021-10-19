package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.mine.PermissionBean;


/**
 * 设备设置
 */
public interface DeviceSettingContract {
    interface Model {
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
        void getDeviceDetail(int id, RequestDataCallback<DeviceDetailBean> callback);
        void updateName(int id, String name, RequestDataCallback<Object> callback);
        void delDevice(int id, RequestDataCallback<Object> callback);

        void getDeviceDetailRestructure(int id, RequestDataCallback<DeviceDetailResponseEntity> callback); // 设备详情，重构
    }

    interface View extends BaseView {
        void getPermissionsSuccess(PermissionBean permissionBean);
        void getDeviceDetailSuccess(DeviceDetailBean deviceDetailBean);
        void updateNameSuccess();
        void delDeviceSuccess();
        void getFail(int errorCode, String msg);

        void getDeviceDetailRestructureSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity); // 设备详情，重构
    }

    interface Presenter extends BasePresenter<View> {
        void getPermissions(int id);
        void getDeviceDetail(int id);
        void updateName(int id, String name, int location_id);
        void delDevice(int id);

        void getDeviceDetailRestructure(int id);   // 设备详情，重构
    }
}
