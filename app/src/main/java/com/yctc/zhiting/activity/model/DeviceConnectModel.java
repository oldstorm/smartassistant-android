package com.yctc.zhiting.activity.model;


import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.DeviceConnectContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.DevicePostBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.request.BindCloudRequest;

/**
 * 添加设备
 */
public class DeviceConnectModel implements DeviceConnectContract.Model {

    /**
     * 数据同步
     *
     * @param body
     * @param callback
     */
    @Override
    public void sync(String body, RequestDataCallback<InvitationCheckBean> callback) {
        HTTPCaller.getInstance().post(InvitationCheckBean.class, HttpUrlConfig.getSync(), body, callback);
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
        if (bean.getType()!=null && !bean.getType().equalsIgnoreCase(Constant.DeviceType.TYPE_SA))
            requestUrl = HttpUrlConfig.getAddLightDeviceUrl();
        HTTPCaller.getInstance().post(AddDeviceResponseBean.class, requestUrl, body, callback);
    }

    /**
     * 绑定云端
     *
     * @param request
     * @param callback
     */
    @Override
    public void bindCloud(BindCloudRequest request, RequestDataCallback<String> callback) {
        HTTPCaller.getInstance().post(String.class, HttpUrlConfig.getBindCloud(), request, callback);
    }
}
