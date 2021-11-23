package com.yctc.zhiting.activity.model;


import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.RADetailContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RADetailBean;

/**
 * 房间/区域详情
 */
public class RADetailModel implements RADetailContract.Model {

    /**
     * 房间/区域详情
     * @param id
     * @param callback
     */
    @Override
    public void getDetail(int id, RequestDataCallback<RADetailBean> callback) {
        HTTPCaller.getInstance().get(RADetailBean.class, HttpUrlConfig.getLocation()+"/"+id,callback);
    }

    /**
     * 修改房间名称
     * @param id
     * @param name
     * @param callback
     */
    @Override
    public void updateName(int id, String name, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getLocation()+"/"+id, name, callback);
    }

    /**
     * 删除房间
     * @param id
     * @param callback
     */
    @Override
    public void delRoom(int id, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().delete(Object.class, HttpUrlConfig.getLocation()+"/"+id, "", callback);
    }

    @Override
    public void getPermissions(int id, RequestDataCallback<PermissionBean> callback) {
        HTTPCaller.getInstance().get(PermissionBean.class, HttpUrlConfig.getPermissions1(id), callback);
    }
}
