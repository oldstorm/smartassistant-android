package com.yctc.zhiting.fragment.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;

import java.util.List;

public interface HomeFragmentContract {
    interface Model {
        void getRoomList(RequestDataCallback<RoomListBean> callback);
        void getDeviceList(RequestDataCallback<RoomDeviceListBean> callback);
        void getDetail(int id, RequestDataCallback<HomeCompanyBean> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
        void checkInterfaceEnable(String url,RequestDataCallback<String> callback);
        void getHomeList(RequestDataCallback<HomeCompanyListBean> callback);
        void scBindSA(String body, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void getRoomListSuccess(RoomListBean roomListBean);
        void getDeviceListSuccess(RoomDeviceListBean roomListBean);
        void getPermissionsSuccess(PermissionBean permissionBean);
        void requestFail(int errorCode, String msg);
        void getDeviceFail(int errorCode, String msg);
        void getDetailSuccess(HomeCompanyBean homeCompanyBean);
        void getDetailFail(int errorCode, String msg);
        void checkSuccess();
        void checkFail(int errorCode, String msg);
        void getHomeListFail(int errorCode, String msg);
        void getHomeListSuccess( List<HomeCompanyBean> areas);
        void scBindSASuccess();
        void scBindSAFail(int errorCode, String msg);

    }

    interface Presenter extends BasePresenter<View> {
        void getRoomList(boolean showLoading);
        void getDeviceList(boolean showLoading);
        void getDetail(int id, boolean showLoading);
        void getPermissions(int id);
        void checkInterfaceEnable(String url);
        void getHomeList();
        void scBindSA(String body);
    }
}
