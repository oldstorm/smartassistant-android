package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.HomeCompanyContract;
import com.yctc.zhiting.activity.model.HomeCompanyModel;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.PermissionBean;

import java.util.List;


/**
 * 家庭/公司
 */
public class HomeCompanyPresenter extends BasePresenterImpl<HomeCompanyContract.View> implements HomeCompanyContract.Presenter {

    HomeCompanyModel model;

    @Override
    public void init() {
        model = new HomeCompanyModel();
    }

    @Override
    public void clear() {
        model = null;
    }


    @Override
    public void getHomeCompanyList(boolean showLoading) {
        if (showLoading) {
            mView.showLoadingView();
        }
        model.getHomeCompanyList(new RequestDataCallback<HomeCompanyListBean>() {
            @Override
            public void onSuccess(HomeCompanyListBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getHomeCompanyListSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getHomeCompanyListFailed(errorMessage);
                }
            }
        });
    }

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
}
