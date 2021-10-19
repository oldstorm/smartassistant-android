package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.HCDetailContract;
import com.yctc.zhiting.activity.model.HCDetailModel;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.InvitationCodeBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.MembersBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;

import java.util.List;

/**
 * 家庭/公司详情
 */
public class HCDetailPresenter extends BasePresenterImpl<HCDetailContract.View> implements HCDetailContract.Presenter {

    HCDetailModel model;

    @Override
    public void init() {
        model = new HCDetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 家庭详情
     *
     * @param id
     */
    @Override
    public void getDetail(int id) {
        if (mView != null)
            mView.showLoadingView();
        model.getDetail(id, new RequestDataCallback<HomeCompanyBean>() {
            @Override
            public void onSuccess(HomeCompanyBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getDataSuccess(obj);
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
     * 成员列表
     */
    @Override
    public void getMembers() {
        model.getMembers(new RequestDataCallback<MembersBean>() {
            @Override
            public void onSuccess(MembersBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getMembersData(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                mView.getFail(errorCode, errorMessage);
            }
        });
    }

    /**
     * 修改家庭名称
     *
     * @param id
     * @param name
     */
    @Override
    public void updateName(int id, String name) {
        mView.showLoadingView();
        String body = "{\"name\":\"" + name + "\"}";
        model.updateName(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.updateNameSuccess();
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
     * 获取用户权限
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
     * 退出家庭
     *
     * @param id
     * @param user_id
     */
    @Override
    public void exitHomeCompany(int id, int user_id) {
        model.exitHomeCompany(id, user_id, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.exitHomeCompanySuccess();
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
     * 删除家庭
     *
     * @param id
     */
    @Override
    public void delHomeCompany(int id) {
        model.delHomeCompany(id, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.delHomeCompanySuccess();
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
     * 角色列表
     */
    @Override
    public void getRoleList() {
//        mView.showLoadingView();
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
                    mView.getFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 生成二维码
     *
     * @param id
     * @param body
     */
    @Override
    public void generateCode(int id, String body) {
        mView.showLoadingView();
        model.generateCode(id, body, new RequestDataCallback<InvitationCodeBean>() {
            @Override
            public void onSuccess(InvitationCodeBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.generateCodeSuccess(obj);
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
     * 用户详情
     *
     * @param id
     */
    @Override
    public void getMemberDetail(int id) {
        mView.showLoadingView();
        model.getMemberDetail(id, new RequestDataCallback<MemberDetailBean>() {
            @Override
            public void onSuccess(MemberDetailBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.getMemberDetailSuccess(obj);
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
