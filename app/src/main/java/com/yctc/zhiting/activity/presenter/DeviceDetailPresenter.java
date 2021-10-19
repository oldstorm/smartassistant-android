package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.DeviceDetailContract;
import com.yctc.zhiting.activity.model.DeviceDetailModel;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.mine.PermissionBean;

/**
 * 设备详情
 */
public class DeviceDetailPresenter extends BasePresenterImpl<DeviceDetailContract.View> implements DeviceDetailContract.Presenter {

    private DeviceDetailModel model;

    @Override
    public void init() {
        model = new DeviceDetailModel();
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
        model.getDeviceDetail(id, new RequestDataCallback<DeviceDetailBean>() {
            @Override
            public void onSuccess(DeviceDetailBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getDeviceDetailSuccess(obj);
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
