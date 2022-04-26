package com.yctc.zhiting.activity.model;


import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.activity.contract.DeviceConnectContract;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.config.HttpUrlParams;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.AreaMoveUrlBean;
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
    public void sync(String body,String url, RequestDataCallback<InvitationCheckBean> callback) {
        HTTPCaller.getInstance().post(InvitationCheckBean.class, url+ HttpUrlConfig.API + HttpUrlParams.sync, body, callback);
    }

    /**
     * 添加SA设备
     *
     * @param bean
     * @param callback
     */
    @Override
    public void addDevice(DeviceBean bean, RequestDataCallback<AddDeviceResponseBean> callback) {
        String body = GsonConverter.getGson().toJson(new DevicePostBean(bean));
        String requestUrl = HttpUrlConfig.getAddDeviceUrl();
        if (bean.getType()!=null){
            if(bean.getType().equalsIgnoreCase(Constant.DeviceType.TYPE_SA)){  // sa设备
                requestUrl = Constant.HTTP_HEAD + bean.getAddress() + ":" + bean.getPort() + HttpUrlConfig.API + HttpUrlParams.sa;
            }else {
                requestUrl = HttpUrlConfig.getAddLightDeviceUrl();
            }
        }
        HTTPCaller.getInstance().post(AddDeviceResponseBean.class, requestUrl, body, callback);
    }

    /**
     * 绑定云端
     *
     * @param request
     * @param callback
     */
    @Override
    public void bindCloud(BindCloudRequest request, String url, RequestDataCallback<String> callback) {
        HTTPCaller.getInstance().post(String.class, url+ HttpUrlConfig.API + HttpUrlParams.cloud_bind, request, callback);
    }


    /**
     * 获取家庭迁移地址
     * @param callback
     */
    @Override
    public void getAreaMoveUrl(String id, RequestDataCallback<AreaMoveUrlBean> callback) {
        HTTPCaller.getInstance().get(AreaMoveUrlBean.class, HttpUrlConfig.getAreaDetailUrl() +"/"+id+"/"+HttpUrlParams.migration, callback);
    }

    /**
     * 迁移云端家庭到本地
     * @param body
     * @param callback
     */
    @Override
    public void areaMove(String body, String url, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, url+ HttpUrlConfig.API + HttpUrlParams.cloud_migration, body, callback);
    }

    /**
     * 找回凭证
     * @param request
     * @param callback
     */
    @Override
    public void putFindCertificate(Request request, RequestDataCallback<String> callback) {
        String body = GsonConverter.getGson().toJson(request);
        HTTPCaller.getInstance().put(String.class, HttpUrlConfig.getFindCertificate(), body, callback);
    }
}
