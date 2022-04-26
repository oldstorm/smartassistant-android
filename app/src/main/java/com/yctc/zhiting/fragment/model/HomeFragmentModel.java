package com.yctc.zhiting.fragment.model;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.home.ApiVersionBean;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.AndroidAppVersionBean;
import com.yctc.zhiting.entity.mine.AppVersionBean;
import com.yctc.zhiting.entity.mine.CheckBindSaBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;
import com.yctc.zhiting.entity.mine.UploadFileBean;
import com.yctc.zhiting.fragment.contract.HomeFragmentContract;

import java.util.List;

public class HomeFragmentModel implements HomeFragmentContract.Model {

    /**
     * 房间列表
     * @param callback
     */
    @Override
    public void getRoomList(int area_type, RequestDataCallback<RoomListBean> callback) {
        HTTPCaller.getInstance().get(RoomListBean.class, area_type == 2 ? HttpUrlConfig.getDepartments() : HttpUrlConfig.getLocation(),callback);
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

    @Override
    public void checkToken(long id, RequestDataCallback<HomeCompanyBean> callback) {
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

    /**
     * 获取家庭列表
     * @param callback
     */
    @Override
    public void getHomeList(RequestDataCallback<HomeCompanyListBean> callback) {
        HTTPCaller.getInstance().get(HomeCompanyListBean.class, HttpUrlConfig.getSCAreasUrl()+ Constant.ONLY_SC,callback);
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
     * 上传头像
     * @param requestData
     * @param callback
     */
    @Override
    public void uploadAvatar(List<NameValuePair> requestData,RequestDataCallback<UploadFileBean> callback) {
        HTTPCaller.getInstance().postFile(UploadFileBean.class, HttpUrlConfig.getUploadAvatarUrl(false), requestData, callback);
    }
    /**
     * 修改头像
     *
     * @param id
     * @param body
     * @param callback
     */
    @Override
    public void updateMember(int id, String body, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getACUsers() + "/" + id, body, callback);
    }

    /**
     * 检查SA状态信息
     * @param callback
     */
    @Override
    public void getSACheck(RequestDataCallback<CheckBindSaBean> callback) {
        HTTPCaller.getInstance().get(CheckBindSaBean.class, HttpUrlConfig.getCheck(),  callback);
    }

    /**
     * 获取App支持的最低Api版本
     * @param requestData
     * @param callback
     */
    @Override
    public void getSupportApi(List<NameValuePair> requestData, RequestDataCallback<ApiVersionBean> callback) {
        HTTPCaller.getInstance().get(ApiVersionBean.class, HttpUrlConfig.getSupportApi()+ Constant.ONLY_SC, requestData, callback );
    }

    @Override
    public void getAppSupportApi(List<NameValuePair> requestData, RequestDataCallback<ApiVersionBean> callback) {
        HTTPCaller.getInstance().get(ApiVersionBean.class, HttpUrlConfig.getAppSupportApi()+ Constant.ONLY_SC, requestData, callback );
    }

    /**
     * 获取更新app版本信息
     *
     * @param callback
     */
    @Override
    public void getAppVersionInfo(List<NameValuePair> requestData, RequestDataCallback<AndroidAppVersionBean> callback) {
        HTTPCaller.getInstance().get(AndroidAppVersionBean.class, HttpUrlConfig.getAppVersionInfo() + Constant.ONLY_SC, requestData, callback);
    }
}
