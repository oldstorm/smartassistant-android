package com.yctc.zhiting.fragment.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;
import com.yctc.zhiting.fragment.contract.HomeFragmentContract;

public class HomeFragmentModel implements HomeFragmentContract.Model {

    /**
     * 房间列表
     * @param callback
     */
    @Override
    public void getRoomList(RequestDataCallback<RoomListBean> callback) {
        HTTPCaller.getInstance().get(RoomListBean.class, HttpUrlConfig.getLocation(),callback);
    }

    /**
     * 设备列表
     * @param callback
     */
    @Override
    public void getDeviceList(RequestDataCallback<RoomDeviceListBean> callback) {
        HTTPCaller.getInstance().get(RoomDeviceListBean.class, HttpUrlConfig.getDeviceList(),callback);
    }

    /**
     * 家庭详情
     * @param id
     * @param callback
     */
    @Override
    public void getDetail(int id, RequestDataCallback<HomeCompanyBean> callback) {
        HTTPCaller.getInstance().get(HomeCompanyBean.class, HttpUrlConfig.getAreasUrl()+"/"+id, callback);
    }

    /**
     * 用户权限
     * @param id
     * @param callback
     */
    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions(id), callback);
    }

    @Override
    public void checkInterfaceEnable(String url,RequestDataCallback<String> callback) {
        HTTPCaller.getInstance().post(String.class, url, "",callback);
    }

    /**
     * 获取家庭列表
     * @param callback
     */
    @Override
    public void getHomeList(RequestDataCallback<HomeCompanyListBean> callback) {
        HTTPCaller.getInstance().get(HomeCompanyListBean.class, HttpUrlConfig.getSCAreasUrl(),callback);
    }

    /**
     * sc绑sa
     * @param body
     * @param callback
     */
    @Override
    public void scBindSA(String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getBindCloud(), body, callback);
    }
}
