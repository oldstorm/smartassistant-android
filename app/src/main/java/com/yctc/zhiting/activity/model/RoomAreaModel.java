package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.RoomAreaContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;

/**
 * 家庭/公司详情
 */
public class RoomAreaModel implements RoomAreaContract.Model {
    @Override
    public void getRoomList(RequestDataCallback<RoomListBean> callback) {
        HTTPCaller.getInstance().get(RoomListBean.class, HttpUrlConfig.getLocation(),callback);
    }

    @Override
    public void addRoom(String name, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getLocation(), name, callback);
    }

    @Override
    public void orderRoom(String locations_id, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getLocation(), locations_id, callback);
    }

    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions1(id), callback);
    }
}
