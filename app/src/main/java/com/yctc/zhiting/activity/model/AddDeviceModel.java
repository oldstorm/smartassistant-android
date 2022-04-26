package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AddDeviceContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.config.HttpUrlParams;
import com.yctc.zhiting.entity.home.DeviceTypeListBean;
import com.yctc.zhiting.entity.mine.CheckBindSaBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;

import java.util.List;

/**
 * 添加设备
 */
public class AddDeviceModel implements AddDeviceContract.Model {
    /**
     * 检查sa
     * @param callback
     */
    @Override
    public void checkBindSa(RequestDataCallback<CheckBindSaBean> callback) {
        HTTPCaller.getInstance().get(CheckBindSaBean.class, HttpUrlConfig.getCheckBindSA(), callback);
    }

    /**
     * 获取设备类型
     * @param callback
     */
    @Override
    public void getDeviceType(RequestDataCallback<DeviceTypeListBean> callback) {
        HTTPCaller.getInstance().get(DeviceTypeListBean.class, HttpUrlConfig.getDeviceType(), callback);
    }

    /**
     * 插件详情
     * @param id
     * @param callback
     */
    @Override
    public void getPluginDetail(String id, RequestDataCallback<PluginDetailBean> callback) {
        HTTPCaller.getInstance().get(PluginDetailBean.class, HttpUrlConfig.getPluginsDetail()+"/"+id, callback);
    }

    /**
     * 一级分类
     * @param callback
     */
    @Override
    public void getDeviceFirstType(RequestDataCallback<DeviceTypeListBean> callback) {
        HTTPCaller.getInstance().get(DeviceTypeListBean.class, HttpUrlConfig.getDeviceFirstType(), callback);
    }

    /**
     * 二级分类
     * @param callback
     */
    @Override
    public void getDeviceSecondType(List<NameValuePair> requestData, RequestDataCallback<DeviceTypeListBean> callback) {
        HTTPCaller.getInstance().get(DeviceTypeListBean.class, HttpUrlConfig.getDeviceSecondType(), requestData, callback);
    }
}
