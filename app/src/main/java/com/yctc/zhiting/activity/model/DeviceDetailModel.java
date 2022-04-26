package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.DeviceDetailContract;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.DeleteSaResponseEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.ExtensionBean;
import com.yctc.zhiting.entity.mine.DeleteSARequest;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.request.AddHCRequest;

/**
 * 添加设备
 */
public class DeviceDetailModel implements DeviceDetailContract.Model {
    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions1(id), callback);
    }

    /**
     * 设备详情
     * @param id
     * @param callback
     */
    @Override
    public void getDeviceDetail(int id, RequestDataCallback<DeviceDetailBean> callback) {
        HTTPCaller.getInstance().get(DeviceDetailBean.class, HttpUrlConfig.getAddDeviceUrl()+"/"+id, callback);
    }

    /**
     * 设备详情  重构
     * @param id
     * @param callback
     */
    @Override
    public void getDeviceDetailRestructure(int id, RequestDataCallback<DeviceDetailResponseEntity> callback) {
        HTTPCaller.getInstance().get(DeviceDetailResponseEntity.class, HttpUrlConfig.getAddDeviceUrl()+"/"+id, callback);
    }

    /**
     * 用户详情页
     * @param id
     * @param callback
     */
    @Override
    public void getUserInfo(int id, RequestDataCallback<MemberDetailBean> callback) {
        HTTPCaller.getInstance().get(MemberDetailBean.class, HttpUrlConfig.getUsers() + "/" + id, callback);
    }

    @Override
    public void getExtensions(RequestDataCallback<ExtensionBean> callback) {
        HTTPCaller.getInstance().get(ExtensionBean.class, HttpUrlConfig.getExtensions(), callback);
    }

    @Override
    public void deleteSa(DeleteSARequest request, RequestDataCallback<DeleteSaResponseEntity> callback) {
        HTTPCaller.getInstance().delete(DeleteSaResponseEntity.class, HttpUrlConfig.getDeleteSaUrl(Constant.CurrentHome.getArea_id()),request.toString(), callback);
    }

    /**
     * 添加云端家庭
     * @param addHCRequest
     * @param callback
     */
    @Override
    public void addScHome(AddHCRequest addHCRequest, RequestDataCallback<IdBean> callback) {
        HTTPCaller.getInstance().post(IdBean.class, HttpUrlConfig.getScAreasNoHeader() + Constant.ONLY_SC, addHCRequest, callback);
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
}
