package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.CompanyMemberDetailContract;
import com.yctc.zhiting.activity.model.CompanyMemberDetailModel;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.DepartmentListBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;

public class CompanyMemberDetailPresenter extends BasePresenterImpl<CompanyMemberDetailContract.View> implements CompanyMemberDetailContract.Presenter {

    private CompanyMemberDetailModel model;

    @Override
    public void init() {
        model = new CompanyMemberDetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }


    /**
     * 成员详情
     * @param id
     */
    @Override
    public void getMemberDetail(int id) {
        mView.showLoadingView();
        model.getMemberDetail(id, new RequestDataCallback<MemberDetailBean>() {
            @Override
            public void onSuccess(MemberDetailBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getDataSuccess(obj);
                }

            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
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
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 角色列表
     */
    @Override
    public void getRoleList() {
        mView.showLoadingView();
        model.getRoleList(new RequestDataCallback<RolesBean>() {
            @Override
            public void onSuccess(RolesBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getRoleListSuccess(obj);
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
     * 修改成员
     * @param id
     * @param body
     */
    @Override
    public void updateMember(int id, String body) {
        mView.showLoadingView();
        model.updateMember(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateSuccess();
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
     * 删除成员
     * @param id
     */
    @Override
    public void delMember(int id) {
        mView.showLoadingView();
        model.delMember(id, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.delMemberSuccess();
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
     * 部门列表
     */
    @Override
    public void getDepartments() {
        mView.showLoadingView();
        model.getDepartments(new RequestDataCallback<DepartmentListBean>() {
            @Override
            public void onSuccess(DepartmentListBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getDepartmentsSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getDepartmentsFail(errorCode, errorMessage);
                }
            }
        });
    }
}
