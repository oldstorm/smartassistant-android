package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.home.BrandBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;

import java.util.List;

/**
 * 家庭/公司
 */
public interface HomeCompanyContract {
    interface Model {
        void getHomeCompanyList(RequestDataCallback<HomeCompanyListBean> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
    }

    interface View extends BaseView {

        void getHomeCompanyListSuccess(HomeCompanyListBean data);
        void getHomeCompanyListFailed(String errorMessage);
        void getPermissionsSuccess(PermissionBean permissionBean);
        void getFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<HomeCompanyContract.View> {
        void getHomeCompanyList(boolean showLoading);
        void getPermissions(int id);
    }
}
