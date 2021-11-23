package com.yctc.zhiting.activity.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.activity.contract.DeviceDetailContract;
import com.yctc.zhiting.activity.contract.SoftwareUpgradeContract;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.mine.CurrentVersionBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.SoftWareVersionBean;

public class SoftwareUpgradeModel implements SoftwareUpgradeContract.Model {
    @Override
    public void getCheckSoftWareVersion(RequestDataCallback<SoftWareVersionBean> callback) {
        HTTPCaller.getInstance().get(SoftWareVersionBean.class, HttpUrlConfig.getSoftWareVersion(), callback);
    }

    @Override
    public void postSoftWareUpgrade(Request request, RequestDataCallback<String> callback) {
        HTTPCaller.getInstance().post(String.class, HttpUrlConfig.getUpgradeSoftWare(),request, callback);
    }

    @Override
    public void getCurrentVersion(RequestDataCallback<CurrentVersionBean> callback) {
        HTTPCaller.getInstance().get(CurrentVersionBean.class, HttpUrlConfig.getCurrentVersion(), callback);
    }
}
