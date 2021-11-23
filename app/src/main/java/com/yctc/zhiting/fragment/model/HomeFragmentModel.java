package com.yctc.zhiting.fragment.model;

import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.config.HttpUrlParams;
import com.yctc.zhiting.entity.AreaIdBean;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;
import com.yctc.zhiting.fragment.contract.HomeFragmentContract;

import java.util.List;

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
    public void getDetail(long id, RequestDataCallback<HomeCompanyBean> callback) {
        HTTPCaller.getInstance().get(HomeCompanyBean.class, HttpUrlConfig.getAreasUrl()+"/"+id, callback);
    }

    /**
     * 用户权限
     * @param id
     * @param callback
     */
    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions1(id), callback);
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
        HTTPCaller.getInstance().get(HomeCompanyListBean.class, HttpUrlConfig.getSCAreasUrl()+ Constant.ONLY_SC,callback);
    }

    /**
     * sc绑sa
     * @param body
     * @param callback
     */
    @Override
    public void scBindSA(String body, RequestDataCallback<AreaIdBean> callback) {
        HTTPCaller.getInstance().post(AreaIdBean.class, HttpUrlConfig.getBindCloud(), body, callback);
    }

    /**
     * 通过sc找回sa的用户凭证
     * @param userId
     * @param requestData
     * @param callback
     */
    @Override
    public void getSATokenBySC(int userId, List<NameValuePair> requestData, RequestDataCallback<FindSATokenBean> callback) {
        HTTPCaller.getInstance().get(FindSATokenBean.class, HttpUrlConfig.getSAToken(userId)+ Constant.ONLY_SC, requestData, callback );
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
