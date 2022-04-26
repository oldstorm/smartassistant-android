package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.DeleteSaResponseEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.ExtensionBean;
import com.yctc.zhiting.entity.mine.DeleteSARequest;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.request.AddHCRequest;

import java.util.List;

/**
 * 设备详情
 */
public interface DeviceDetailContract {
    interface Model {
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);

        void getDeviceDetail(int id, RequestDataCallback<DeviceDetailBean> callback); // 设备详情

        void getDeviceDetailRestructure(int id, RequestDataCallback<DeviceDetailResponseEntity> callback); // 设备详情，重构

        void getUserInfo(int id, RequestDataCallback<MemberDetailBean> callback);

        void getExtensions(RequestDataCallback<ExtensionBean> callback);

        void deleteSa(DeleteSARequest request, RequestDataCallback<DeleteSaResponseEntity> callback);

        void addScHome(AddHCRequest addHCRequest, RequestDataCallback<IdBean> callback);

        void getPluginDetail(String id, RequestDataCallback<PluginDetailBean> callback);
    }

    interface View extends BaseView {
        void getPermissionsSuccess(PermissionBean permissionBean);

        void getDeviceDetailSuccess(DeviceDetailBean deviceDetailBean);

        void getDeviceDetailRestructureSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity); // 设备详情，重构

        void getFail(int errorCode, String msg);

        void getUserInfoSuccess(MemberDetailBean memberDetailBean);

        void getExtensionsSuccess(List<String> list);

        void getExtensionsFail(int errorCode, String msg);

        void deleteSaFailed(int errorCode, String msg);

        void deleteSaSuccess(int remove_status);

        void createHomeOrCompanySuccess(IdBean idBean);

        void createHomeOrCompanyFail(int errorCode, String msg);

        void getPluginDetailSuccess(PluginDetailBean pluginDetailBean);
        void getPluginDetailFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getPermissions(int id);

        void getDeviceDetail(int id);

        void getDeviceDetailRestructure(int id); // 设备详情，重构

        void getUserInfo(int id);

        void getExtensions();

        void deleteSa(DeleteSARequest request);

        void addScHome(AddHCRequest addHCRequest);

        void getPluginDetail(String id);
    }
}
