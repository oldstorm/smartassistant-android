package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.mine.CurrentVersionBean;
import com.yctc.zhiting.entity.mine.FirmWareVersionBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.SoftWareVersionBean;

public interface SoftwareUpgradeContract {
    interface Model {
        void getCheckSoftWareVersion(RequestDataCallback<SoftWareVersionBean> callback);

        void postSoftWareUpgrade(Request request, RequestDataCallback<String> callback);

        void getCurrentVersion(RequestDataCallback<CurrentVersionBean> callback);

        void getFirmWareLatestVersion(RequestDataCallback<SoftWareVersionBean> callback);

        void updateFirmWare(Request request, RequestDataCallback<String> callback);

        void getCurrentFirmWareVersion(RequestDataCallback<CurrentVersionBean> callback);
    }

    interface View extends BaseView {
        void onCheckSoftWareVersionSuccess(SoftWareVersionBean bean);

        void onCheckSoftWareVersionFailed(int code, String msg);

        void onSoftWareUpgradeSuccess();

        void onSoftWareUpgradeFailed(int code, String msg);

        void onCurrentVersionSuccess(CurrentVersionBean data);

        void onCurrentVersionFailed(int code, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getCheckSoftWareVersion();

        void postSoftWareUpgrade(SoftWareVersionBean request);

        void getCurrentSoftWareVersion();

        void getFirmWareLatestVersion();

        void updateFirmWare(SoftWareVersionBean request);

        void getCurrentFirmWareVersion();
    }
}
