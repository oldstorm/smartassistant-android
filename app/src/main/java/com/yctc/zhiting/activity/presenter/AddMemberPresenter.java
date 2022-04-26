package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AboutContract;
import com.yctc.zhiting.activity.contract.AddMemberContract;
import com.yctc.zhiting.activity.model.AboutModel;
import com.yctc.zhiting.activity.model.AddMemberModel;
import com.yctc.zhiting.entity.mine.DepartmentDetail;
import com.yctc.zhiting.entity.mine.MembersBean;

public class AddMemberPresenter extends BasePresenterImpl<AddMemberContract.View> implements AddMemberContract.Presenter {

    private AddMemberModel model;

    @Override
    public void init() {
        model = new AddMemberModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 成员列表
     */
    @Override
    public void getMembers() {
        if (model != null) {
            model.getMembers(new RequestDataCallback<MembersBean>() {
                @Override
                public void onSuccess(MembersBean obj) {
                    super.onSuccess(obj);
                    if (mView != null) {
                        mView.getMembersSuccess(obj);
                    }
                }

                @Override
                public void onFailed(int errorCode, String errorMessage) {
                    super.onFailed(errorCode, errorMessage);
                    if (mView != null)
                        mView.getMembersFail(errorCode, errorMessage);
                }
            });
        }
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
     * 添加部门成员
     * @param id
     * @param json
     */
    @Override
    public void addDMDepartmentMember(int id, String json) {
        if (mView!=null)
            mView.showLoadingView();
        model.addDMDepartmentMember(id, json, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.addDMDepartmentMemberSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.addDMDepartmentMemberFail(errorCode, errorMessage);
                }
            }
        });
    }
}
