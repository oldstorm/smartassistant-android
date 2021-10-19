package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.request.BindCloudRequest;

/**
 * 添加设备
 */
public interface DeviceConnectContract {
    interface Model {
        void sync(String body, RequestDataCallback<InvitationCheckBean> callback);

        void addDevice(DeviceBean bean, RequestDataCallback<AddDeviceResponseBean> callback);

        void bindCloud(BindCloudRequest request, RequestDataCallback<String> callback);
    }

    interface View extends BaseView {
        void syncSuccess(InvitationCheckBean invitationCheckBean);

        void syncFail(int errorCode, String msg);

        void addDeviceSuccess(AddDeviceResponseBean data);

        void addDeviceFail(int errorCode, String msg);

        void bindCloudSuccess();

        void bindCloudFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void sync(String body);

        void addDevice(DeviceBean bean);

        void bindCloud(BindCloudRequest request);
    }
}
