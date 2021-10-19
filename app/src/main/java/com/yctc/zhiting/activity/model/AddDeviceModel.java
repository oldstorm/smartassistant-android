package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AddDeviceContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.CheckBindSaBean;

/**
 * 添加设备
 */
public class AddDeviceModel implements AddDeviceContract.Model {
    @Override
    public void checkBindSa(RequestDataCallback<CheckBindSaBean> callback) {
        HTTPCaller.getInstance().get(CheckBindSaBean.class, HttpUrlConfig.getCheckBindSA(), callback);
    }
}
