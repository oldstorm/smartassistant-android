package com.yctc.zhiting.fragment.presenter;

import android.text.TextUtils;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.entity.AreaIdBean;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;
import com.yctc.zhiting.fragment.contract.HomeFragmentContract;
import com.yctc.zhiting.fragment.model.HomeFragmentModel;

import java.util.List;

public class HomeFragmentPresenter extends BasePresenterImpl<HomeFragmentContract.View> implements HomeFragmentContract.Presenter {
    HomeFragmentModel model;

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
    public void getRoomList(boolean showLoading) {
        if (showLoading) {
            mView.showLoadingView();
        }
        if (model!=null) {
            model.getRoomList(new RequestDataCallback<RoomListBean>() {
                @Override
                public void onSuccess(RoomListBean obj) {
                    super.onSuccess(obj);
                    if (mView != null) {
                        mView.hideLoadingView();
                        mView.getRoomListSuccess(obj);
                    }
                }

                @Override
                public void onFailed(int errorCode, String errorMessage) {
                    super.onFailed(errorCode, errorMessage);
                    if (mView != null) {
                        mView.hideLoadingView();
                        mView.requestFail(errorCode, errorMessage);
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
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getDeviceFail(errorCode, errorMessage);
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
        model.getDetail(id, new RequestDataCallback<HomeCompanyBean>() {
            @Override
            public void onSuccess(HomeCompanyBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getDetailSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getDetailFail(errorCode, errorMessage);
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
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 检测接口是否可用
     */
    @Override
    public void checkInterfaceEnable(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (!url.startsWith("http"))
                url = "http://" + url;
            url = url + "/api/check";
        }
        model.checkInterfaceEnable(url, new RequestDataCallback<String>() {
            @Override
            public void onSuccess(String obj) {
                super.onSuccess(obj);
                LogUtil.e("checkInterfaceEnable=成功");
                if (mView != null) {
                    mView.checkSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                LogUtil.e("checkInterfaceEnable=失败");
                if (mView != null) {
                    mView.checkFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 获取家庭列表
     */
    @Override
    public void getHomeList() {
        model.getHomeList(new RequestDataCallback<HomeCompanyListBean>() {
            @Override
            public void onSuccess(HomeCompanyListBean obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    if (obj != null) {
                        List<HomeCompanyBean> areas = obj.getAreas();
                        mView.getHomeListSuccess(areas);
                    }
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.getHomeListFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * sc绑sa
     * @param body
     */
    @Override
    public void scBindSA(String body) {
        model.scBindSA(body, new RequestDataCallback<AreaIdBean>() {
            @Override
            public void onSuccess(AreaIdBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.scBindSASuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.scBindSAFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 通过sc找回sa的用户凭证
     * @param userId
     * @param requestData
     */
    @Override
    public void getSAToken(int userId, List<NameValuePair> requestData) {
        model.getSATokenBySC(userId, requestData, new RequestDataCallback<FindSATokenBean>() {
            @Override
            public void onSuccess(FindSATokenBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getSATokenSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.getSATokenFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 找回凭证方式
     * @param request
     */
    @Override
    public void putFindCertificate(Request request) {
        model.putFindCertificate(request, new RequestDataCallback<String>() {
            @Override
            public void onSuccess(String obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.onCertificateSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.onCertificateFail(errorCode, errorMessage);
                }
            }
        });
    }
}
