package com.yctc.zhiting.fragment.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
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

import java.util.List;

public interface HomeFragmentContract {
    interface Model {
        void getRoomList(int area_type, RequestDataCallback<RoomListBean> callback);

        void getDeviceList(RequestDataCallback<RoomDeviceListBean> callback);

        void getDetail(long id, RequestDataCallback<HomeCompanyBean> callback);

        void checkToken(long id, RequestDataCallback<HomeCompanyBean> callback);

        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);

        void getHomeList(RequestDataCallback<HomeCompanyListBean> callback);

        void getSATokenBySC(int userId, List<NameValuePair> requestData, RequestDataCallback<FindSATokenBean> callback);

        void uploadAvatar(List<NameValuePair> requestData, RequestDataCallback<UploadFileBean> callback);
        void updateMember(int id, String body, RequestDataCallback<Object> callback);

        void getSACheck(RequestDataCallback<CheckBindSaBean> callback);
        void getSupportApi(List<NameValuePair> requestData, RequestDataCallback<ApiVersionBean> callback);
        void getAppSupportApi(List<NameValuePair> requestData, RequestDataCallback<ApiVersionBean> callback);
        void getAppVersionInfo(List<NameValuePair> requestData, RequestDataCallback<AndroidAppVersionBean> callback);
    }

    interface View extends BaseView {
        void getRoomListSuccess(RoomListBean roomListBean);

        void getRoomListFailed(int errorCode, String msg);

        void getDeviceListSuccess(RoomDeviceListBean roomListBean);

        void getPermissionsSuccess(PermissionBean permissionBean);

        void requestFail(int errorCode, String msg);

        void getDeviceFail(int errorCode, String msg);

        void getDetailSuccess(HomeCompanyBean homeCompanyBean);

        void getDetailFail(int errorCode, String msg);

        void checkTokenFail(int errorCode, String msg, String name);

        void getHomeListFail(int errorCode, String msg);

        void getHomeListSuccess(List<HomeCompanyBean> areas);

        void getSATokenSuccess(FindSATokenBean findSATokenBean);

        void getSATokenFail(int errorCode, String msg);

        void onCheckSaAddressSuccess();

        void onCheckSaAddressFailed();

        void uploadAvatarSuccess(UploadFileBean uploadFileBean);
        void uploadAvatarFail(int errorCode, String msg);
        void updateMemberSuccess();
        void updateMemberFail(int errorCode, String msg);

        void getSACheckSuccess(CheckBindSaBean checkBindSaBean);
        void getSACheckFail(int errorCode, String msg);
        void getSupportApiSuccess(ApiVersionBean apiVersionBean);
        void getSupportApiFail(int errorCode, String msg);
        void getAppSupportApiSuccess(ApiVersionBean apiVersionBean);
        void getAppSupportApiFail(int errorCode, String msg);
        void getAppVersionInfoSuccess(AndroidAppVersionBean androidBean);
        void getAppVersionInfoFailed(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getRoomList(int area_type, boolean showLoading);

        void getDeviceList(boolean showLoading);

        void getDetail(long id, boolean showLoading);

        void checkToken(long id, String name);

        void getPermissions(int id);

        void getHomeList();

        void getSAToken(int userId, List<NameValuePair> requestData);

        void checkSaAddress();

        void uploadAvatar(List<NameValuePair> requestData);
        void updateMember(int id, String body);

        void getSACheck();
        void getSupportApi(List<NameValuePair> requestData);
        void getAppSupportApi(List<NameValuePair> requestData);
        void checkAppVersionInfo(List<NameValuePair> requestData);
    }
}
