package com.yctc.zhiting.activity.presenter;

import android.text.TextUtils;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.HCDetailContract;
import com.yctc.zhiting.activity.model.HCDetailModel;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.InvitationCodeBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.MembersBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;
import com.yctc.zhiting.entity.mine.VerificationCodeBean;
import com.yctc.zhiting.request.AddHCRequest;

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
    public void getDetail(long id) {
        if (mView != null)
            mView.showLoadingView();
        if (model != null) {
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
                        mView.getDetailFail(errorCode, errorMessage);
                    }
                }
            });
        }
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
                        mView.getMembersData(obj);
                    }
                }

                @Override
                public void onFailed(int errorCode, String errorMessage) {
                    super.onFailed(errorCode, errorMessage);
                    if (mView != null)
                        mView.getFail(errorCode, errorMessage);
                }
            });
        }
    }

    @Override
    public void getVerificationCode() {
        model.getVerificationCode(new RequestDataCallback<VerificationCodeBean>() {
            @Override
            public void onSuccess(VerificationCodeBean obj) {
                super.onSuccess(obj);
                if (mView != null && obj != null && !TextUtils.isEmpty(obj.getCode())) {
                    mView.onVerificationCodeSuccess(obj.getCode());
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null)
                    mView.onVerificationCodeFail(errorCode, errorMessage);
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
    public void updateName(long id, String name) {
        mView.showLoadingView();
        String body = "{\"name\":\"" + name + "\"}";
        if (model != null) {
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
                        mView.getFail(errorCode, errorMessage);
                    }
                }
            });
        }
    }

    /**
     * 退出家庭
     *
     * @param id
     * @param user_id
     */
    @Override
    public void exitHomeCompany(long id, int user_id) {
        if (model != null) {
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
    }

    /**
     * 删除家庭
     *
     * @param id
     */
    @Override
    public void delHomeCompany(long id, String body) {
        if (model != null) {
            model.delHomeCompany(id, body, new RequestDataCallback<Object>() {
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
    }

    /**
     * 角色列表
     */
    @Override
    public void getRoleList() {
        if (model != null) {
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
        if (model != null) {
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
    }

    /**
     * 用户详情
     *
     * @param id
     */
    @Override
    public void getMemberDetail(int id) {
        if (model != null) {
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

    /**
     * 通过sc找回sa的用户凭证
     *
     * @param userId
     * @param requestData
     */
    @Override
    public void getSAToken(int userId, List<NameValuePair> requestData) {
        model.getSATokenBySC(userId, requestData, new RequestDataCallback<FindSATokenBean>() {
            @Override
            public void onSuccess(FindSATokenBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getSATokenSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getSATokenFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 添加云端家庭
     *
     * @param addHCRequest
     */
    @Override
    public void addScHome(AddHCRequest addHCRequest) {
        model.addScHome(addHCRequest, new RequestDataCallback<IdBean>() {
            @Override
            public void onSuccess(IdBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.addScHomeSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.addScHomeFail(errorCode, errorMessage);
                }
            }
        });
    }
}
