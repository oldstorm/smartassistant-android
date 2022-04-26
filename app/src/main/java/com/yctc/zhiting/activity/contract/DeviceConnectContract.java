package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.AreaMoveUrlBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.request.BindCloudRequest;

/**
 * 添加设备
 */
public interface DeviceConnectContract {
    interface Model {
        void sync(String body, String url, RequestDataCallback<InvitationCheckBean> callback);

        void addDevice(DeviceBean bean, RequestDataCallback<AddDeviceResponseBean> callback);

        void bindCloud(BindCloudRequest request,String url, RequestDataCallback<String> callback);

        void getAreaMoveUrl(String id, RequestDataCallback<AreaMoveUrlBean> callback);

        void areaMove(String body, String url, RequestDataCallback<Object> callback);

        void putFindCertificate(Request request, RequestDataCallback<String> callback);
    }

    interface View extends BaseView {
        void syncSuccess(InvitationCheckBean invitationCheckBean);

        void syncFail(int errorCode, String msg);

        void addDeviceSuccess(AddDeviceResponseBean data);

        void addDeviceFail(int errorCode, String msg);

        void bindCloudSuccess();

        void bindCloudFail(int errorCode, String msg);

        void getAreaMoveUrlSuccess(AreaMoveUrlBean areaMoveUrlBean);
        void getAreaMoveUrlFail(int errorCode, String msg);
        void areaMoveSuccess();
        void areaMoveFail(int errorCode, String msg);

        void onCertificateSuccess();
        void onCertificateFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void sync(String body, String url);

        void addDevice(DeviceBean bean);

        void bindCloud(BindCloudRequest request,String url);

        void getAreaMoveUrl(String id);
        void areaMove(String body, String url);

        void putFindCertificate(Request request);
    }
}
