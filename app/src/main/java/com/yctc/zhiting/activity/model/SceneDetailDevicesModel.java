package com.yctc.zhiting.activity.model;


import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneDetailDeviceContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.RoomListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加设备
 */
public class SceneDetailDevicesModel implements SceneDetailDeviceContract.Model {

    /**
     * 设备列表
     * @param callback
     */
    @Override
    public void getDeviceList(RequestDataCallback<RoomDeviceListBean> callback) {
        List<NameValuePair> requestData = new ArrayList<>();
        requestData.add(new NameValuePair("type", String.valueOf(1)));
        HTTPCaller.getInstance().get(RoomDeviceListBean.class, HttpUrlConfig.getAddDeviceUrl(), requestData, callback);
    }

    /**
     * 房间列表
     * @param callback
     */
    @Override
    public void getRoomList(RequestDataCallback<RoomListBean> callback) {
        HTTPCaller.getInstance().get(RoomListBean.class, HttpUrlConfig.getLocation(),callback);
    }
}
