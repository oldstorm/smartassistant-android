package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.DeviceSettingContract;
import com.yctc.zhiting.activity.model.DeviceSettingModel;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.mine.PermissionBean;

/**
 * 添加设备
 */
public class DeviceSettingPresenter extends BasePresenterImpl<DeviceSettingContract.View> implements DeviceSettingContract.Presenter {

    private DeviceSettingModel model;

    @Override
    public void init() {
        model = new DeviceSettingModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 用户权限
     * @param id
     */
    @Override
    public void getPermissions(int id) {
        model.getPermissions(id, new RequestDataCallback<PermissionBean>() {
            @Override
            public void onSuccess(PermissionBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getPermissionsSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 设备详情
     * @param id
     */
    @Override
    public void getDeviceDetail(int id) {
        mView.showLoadingView();
        model.getDeviceDetail(id, new RequestDataCallback<DeviceDetailBean>() {
            @Override
            public void onSuccess(DeviceDetailBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getDeviceDetailSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 修改设备名称
     * @param id
     * @param name
     */
    @Override
    public void updateName(int id, String name, int location_id) {
        mView.showLoadingView();
        String body = "{\"name\":\""+ name+"\",\"location_id\":"+location_id+"}";
        model.updateName(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateNameSuccess();
                }

            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 删除设备
     * @param id
     */
    @Override
    public void delDevice(int id) {
        mView.showLoadingView();
        model.delDevice(id, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.delDeviceSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 设备详情 重构
     * @param id
     */
    @Override
    public void getDeviceDetailRestructure(int id) {
        model.getDeviceDetailRestructure(id, new RequestDataCallback<DeviceDetailResponseEntity>() {
            @Override
            public void onSuccess(DeviceDetailResponseEntity obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getDeviceDetailRestructureSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getFail(errorCode, errorMessage);
                }
            }
        });
    }
}
