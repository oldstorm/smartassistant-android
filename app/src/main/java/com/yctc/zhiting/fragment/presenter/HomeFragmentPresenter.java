package com.yctc.zhiting.fragment.presenter;

import static com.yctc.zhiting.config.Constant.CurrentHome;

import android.text.TextUtils;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.comfig.HttpConfig;
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
import com.yctc.zhiting.fragment.model.HomeFragmentModel;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.UpdateApkUtil;

import java.util.List;

public class HomeFragmentPresenter extends BasePresenterImpl<HomeFragmentContract.View> implements HomeFragmentContract.Presenter {
    HomeFragmentModel model;
    private String TAG = "HomeFragmentPresenter==";

    @Override
    public void init() {
        model = new HomeFragmentModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 房间列表
     */
    @Override
    public void getRoomList(int area_type, boolean showLoading) {
        if (showLoading) {
            mView.showLoadingView();
        }
        if (model != null) {
            model.getRoomList(area_type, new RequestDataCallback<RoomListBean>() {
                @Override
                public void onSuccess(RoomListBean obj) {
                    super.onSuccess(obj);
                    if (mView != null) {
                        mView.hideLoadingView();
                        mView.getRoomListSuccess(obj);
                        LogUtil.e(TAG + "getRoomList=success");
                    }
                }

                @Override
                public void onFailed(int errorCode, String errorMessage) {
                    super.onFailed(errorCode, errorMessage);
                    if (mView != null) {
                        mView.hideLoadingView();
                        mView.getRoomListFailed(errorCode, errorMessage);
                        LogUtil.e(TAG + "getRoomList=failed");
                    }
                }
            });
        }
    }

    /**
     * 设备列表
     */
    @Override
    public void getDeviceList(boolean showLoading) {
        model.getDeviceList(new RequestDataCallback<RoomDeviceListBean>() {
            @Override
            public void onSuccess(RoomDeviceListBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getDeviceListSuccess(obj);
                    LogUtil.e(TAG + "getDeviceList=success");
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getDeviceFail(errorCode, errorMessage);
                    LogUtil.e(TAG + "getDeviceList=failed");
                }
            }
        });
    }

    /**
     * 家庭详情
     *
     * @param id
     */
    @Override
    public void getDetail(long id, boolean showLoading) {
        if (showLoading) {
            mView.showLoadingView();
        }
        HttpConfig.clearHeader();
        HttpConfig.addHeader(CurrentHome.getSa_user_token());
        LogUtil.e("getDetail123=" + CurrentHome.getName() + ",id=" + CurrentHome.getId());
        model.getDetail(id, new RequestDataCallback<HomeCompanyBean>() {
            @Override
            public void onSuccess(HomeCompanyBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getDetailSuccess(obj);
                    LogUtil.e(TAG + "getDetail=success");
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getDetailFail(errorCode, errorMessage);
                    LogUtil.e(TAG + "getDetail=failed");
                }
            }
        });
    }

    @Override
    public void checkToken(long id, String name) {
        model.checkToken(id, new RequestDataCallback<HomeCompanyBean>() {
            @Override
            public void onSuccess(HomeCompanyBean obj) {
                super.onSuccess(obj);
                LogUtil.e(TAG + "checkToken=success");
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    LogUtil.e(TAG + "checkToken=failed");
                    mView.checkTokenFail(errorCode, errorMessage, name);
                }
            }
        });
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
                    mView.hideLoadingView();
                    LogUtil.e(TAG + "getPermissions=success");
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.requestFail(errorCode, errorMessage);
                    LogUtil.e(TAG + "getPermissions=onFailed");
                }
            }
        });
    }

    /**
     * 获取家庭列表
     */
    @Override
    public void getHomeList() {
        if (mView != null) mView.showLoadingView();
        if (model!=null)
        model.getHomeList(new RequestDataCallback<HomeCompanyListBean>() {
            @Override
            public void onSuccess(HomeCompanyListBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    if (obj != null) {
                        List<HomeCompanyBean> areas = obj.getAreas();
                        mView.getHomeListSuccess(areas);
                        LogUtil.e(TAG + "getHomeList=success");
                    }
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getHomeListFail(errorCode, errorMessage);
                    LogUtil.e(TAG + "getHomeList=failed");
                }
            }
        });
    }

    /**
     * 通过sc找回sa的用户凭证
     *
     * @param userId
     * @param requestData
     */
    @Override
    public void getSAToken(int userId, List<NameValuePair> requestData) {
        requestData.clear();
        long areaId = HomeUtil.isSAEnvironment() ? CurrentHome.getArea_id() : CurrentHome.getId();
        NameValuePair nameValuePair = new NameValuePair("area_id", String.valueOf(areaId));
        requestData.add(nameValuePair);

        model.getSATokenBySC(userId, requestData, new RequestDataCallback<FindSATokenBean>() {
            @Override
            public void onSuccess(FindSATokenBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getSATokenSuccess(obj);
                    LogUtil.e(TAG + "getSAToken=success");
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getSATokenFail(errorCode, errorMessage);
                    LogUtil.e(TAG + "getSAToken=failed");
                }
            }
        });
    }

    /**
     * 检测SA地址是否可用
     */
    @Override
    public void checkSaAddress() {
        AllRequestUtil.checkUrl500(CurrentHome.getSa_lan_address(), new AllRequestUtil.onCheckUrlListener() {
            @Override
            public void onSuccess() {//可以连接上
                LogUtil.e(TAG + "checkSaAddress===onSuccess");
                mView.onCheckSaAddressSuccess();
            }

            @Override
            public void onError() {//连接失败
                LogUtil.e(TAG + "onCheckSaAddressFailed1===onError");
                mView.onCheckSaAddressFailed();
            }
        });
    }

    /**
     * 上传头像
     *
     * @param requestData
     */
    @Override
    public void uploadAvatar(List<NameValuePair> requestData) {
        model.uploadAvatar(requestData, new RequestDataCallback<UploadFileBean>() {
            @Override
            public void onSuccess(UploadFileBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.uploadAvatarSuccess(obj);
                    LogUtil.e(TAG + "uploadAvatar=success");
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.uploadAvatarFail(errorCode, errorMessage);
                    LogUtil.e(TAG + "uploadAvatar=failed");
                }
            }
        });
    }

    /**
     * 修改头像
     *
     * @param id
     * @param body
     */
    @Override
    public void updateMember(int id, String body) {
        model.updateMember(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateMemberSuccess();
                    LogUtil.e(TAG + "updateMember=success");
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateMemberFail(errorCode, errorMessage);
                    LogUtil.e(TAG + "updateMember=onFailed");
                }
            }
        });
    }

    /**
     * 检查SA状态信息
     */
    @Override
    public void getSACheck() {
        model.getSACheck(new RequestDataCallback<CheckBindSaBean>() {
            @Override
            public void onSuccess(CheckBindSaBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getSACheckSuccess(obj);
                    LogUtil.e(TAG + "getSACheck=success");
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getSACheckFail(errorCode, errorMessage);
                    LogUtil.e(TAG + "getSACheck=onFailed");
                }
            }
        });
    }

    /**
     * 获取App支持的最低Api版本
     *
     * @param requestData
     */
    @Override
    public void getSupportApi(List<NameValuePair> requestData) {
        model.getSupportApi(requestData, new RequestDataCallback<ApiVersionBean>() {
            @Override
            public void onSuccess(ApiVersionBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getSupportApiSuccess(obj);
                    LogUtil.e(TAG + "getSupportApi=success");
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getSupportApiFail(errorCode, errorMessage);
                    LogUtil.e(TAG + "getSupportApi=failed");
                }
            }
        });
    }

    /**
     * 获取App支持的最低Api版本
     *
     * @param requestData
     */
    @Override
    public void getAppSupportApi(List<NameValuePair> requestData) {
        model.getAppSupportApi(requestData, new RequestDataCallback<ApiVersionBean>() {
            @Override
            public void onSuccess(ApiVersionBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getAppSupportApiSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getAppSupportApiFail(errorCode, errorMessage);
                }
            }
        });
    }

    @Override
    public void checkAppVersionInfo(List<NameValuePair> requestData) {
        if (model != null) {
            model.getAppVersionInfo(requestData, new RequestDataCallback<AndroidAppVersionBean>() {
                @Override
                public void onSuccess(AndroidAppVersionBean obj) {
                    super.onSuccess(obj);
                    if (mView != null) {
                        mView.getAppVersionInfoSuccess(obj);
                        LogUtil.e(TAG + "checkAppVersionInfo=success");
                    }
                }

                @Override
                public void onFailed(int errorCode, String errorMessage) {
                    super.onFailed(errorCode, errorMessage);
                    if (mView != null) {
                        mView.getAppVersionInfoFailed(errorCode, errorMessage);
                        LogUtil.e(TAG + "checkAppVersionInfo=onFailed");
                    }
                }
            });
        }
    }
}
