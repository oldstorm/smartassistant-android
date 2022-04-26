package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.DeviceDetailContract;
import com.yctc.zhiting.activity.model.DeviceDetailModel;
import com.yctc.zhiting.bean.DeviceDetailBean;
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
     *
     * @param id
     */
    @Override
    public void getPermissions(int id) {
        model.getPermissions(id, new RequestDataCallback<PermissionBean>() {
            @Override
            public void onSuccess(PermissionBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
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
     *
     * @param id
     */
    @Override
    public void getDeviceDetail(int id) {
        model.getDeviceDetail(id, new RequestDataCallback<DeviceDetailBean>() {
            @Override
            public void onSuccess(DeviceDetailBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
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
     *
     * @param id
     */
    @Override
    public void getDeviceDetailRestructure(int id) {
        model.getDeviceDetailRestructure(id, new RequestDataCallback<DeviceDetailResponseEntity>() {
            @Override
            public void onSuccess(DeviceDetailResponseEntity obj) {
                super.onSuccess(obj);
                if (mView != null) {
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

    /**
     * 用户详情页
     */
    @Override
    public void getUserInfo(int id) {
        model.getUserInfo(id, new RequestDataCallback<MemberDetailBean>() {
            @Override
            public void onSuccess(MemberDetailBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getUserInfoSuccess(obj);
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

    @Override
    public void getExtensions() {
        model.getExtensions(new RequestDataCallback<ExtensionBean>() {
            @Override
            public void onSuccess(ExtensionBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getExtensionsSuccess(obj.getExtension_names());
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getExtensionsFail(errorCode, errorMessage);
                }
            }
        });
    }

    @Override
    public void deleteSa(DeleteSARequest request) {
        if (mView != null) {
            mView.showLoadingView();
        }
        model.deleteSa(request, new RequestDataCallback<DeleteSaResponseEntity>() {
            @Override
            public void onSuccess(DeleteSaResponseEntity obj) {
                super.onSuccess(obj);
                if (mView != null && obj != null) {
                    mView.hideLoadingView();
                    mView.deleteSaSuccess(obj.getRemove_status());
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.deleteSaFailed(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 添加云端家庭/公司
     *
     * @param addHCRequest
     */
    @Override
    public void addScHome(AddHCRequest addHCRequest) {
        if (mView != null) {
            mView.showLoadingView();
        }
        model.addScHome(addHCRequest, new RequestDataCallback<IdBean>() {
            @Override
            public void onSuccess(IdBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.createHomeOrCompanySuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.createHomeOrCompanyFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 插件详情
     * @param id
     */
    @Override
    public void getPluginDetail(String id) {
        model.getPluginDetail(id, new RequestDataCallback<PluginDetailBean>() {
            @Override
            public void onSuccess(PluginDetailBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getPluginDetailSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getPluginDetailFail(errorCode,errorMessage);
                }
            }
        });
    }
}
