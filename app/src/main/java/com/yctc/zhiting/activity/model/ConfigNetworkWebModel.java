package com.yctc.zhiting.activity.model;


import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ConfigNetworkWebContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.config.HttpUrlParams;
import com.yctc.zhiting.entity.home.AccessTokenBean;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.DevicePostBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

public class ConfigNetworkWebModel implements ConfigNetworkWebContract.Model {

    @Override
    public void getAccessToken(RequestDataCallback<AccessTokenBean> callback) {
        HTTPCaller.getInstance().post(AccessTokenBean.class, HttpUrlConfig.getDeviceAccessToken(), "", callback);
    }

    /**
     * 添加设备
     *
     * @param bean
     * @param callback
     */
    @Override
    public void addDevice(DeviceBean bean, RequestDataCallback<AddDeviceResponseBean> callback) {
        String body = GsonConverter.getGson().toJson(new DevicePostBean(bean));
        String requestUrl = HttpUrlConfig.getAddDeviceUrl();
        requestUrl = HttpUrlConfig.getAddLightDeviceUrl();
        HTTPCaller.getInstance().post(AddDeviceResponseBean.class, requestUrl, body, callback);
    }
}
