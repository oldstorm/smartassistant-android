package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.CompanyDetailContract;
import com.yctc.zhiting.activity.contract.DepartmentDetailContract;
import com.yctc.zhiting.activity.model.CompanyDetailModel;
import com.yctc.zhiting.activity.model.DepartmentDetailModel;
import com.yctc.zhiting.entity.mine.DepartmentDetail;
import com.yctc.zhiting.entity.mine.PermissionBean;

public class DepartmentDetailPresenter extends BasePresenterImpl<DepartmentDetailContract.View> implements DepartmentDetailContract.Presenter {

    private DepartmentDetailModel model;

    @Override
    public void init() {
        model = new DepartmentDetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }


    /**
     * 部门详情
     * @param id
     */
    @Override
    public void getDepartmentDetail(int id) {
        if (mView!=null){
            mView.showLoadingView();
        }
        model.getDepartmentDetail(id, new RequestDataCallback<DepartmentDetail>() {
            @Override
            public void onSuccess(DepartmentDetail obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getDepartmentDetailSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.hideLoadingView();
                    mView.getDepartmentDetailFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 获取用户权限
     *
     * @param id
     */
    @Override
    public void getPermissions(int id) {
        if (model != null) {
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
                        mView.getPermissionFail(errorCode, errorMessage);
                    }
                }
            });
        }
    }
}
