package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
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

public interface CompanyDetailContract {
    interface Model {
        void getDetail(long id, RequestDataCallback<HomeCompanyBean> callback);
        void getMembers(RequestDataCallback<MembersBean> callback);
        void updateName(long id, String name, RequestDataCallback<Object> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
        void exitHomeCompany(long id, int user_id, RequestDataCallback<Object> callback);
        void delHomeCompany(long id, String body, RequestDataCallback<Object> callback);
        void getRoleList(RequestDataCallback<RolesBean> callback);
        void generateCode(int id, String body, RequestDataCallback<InvitationCodeBean> callback);
        void getMemberDetail(int id, RequestDataCallback<MemberDetailBean> callback);
        void getSATokenBySC(int userId, List<NameValuePair> requestData, RequestDataCallback<FindSATokenBean> callback);
        void addScHome(AddHCRequest addHCRequest, RequestDataCallback<IdBean> callback);
        void getVerificationCode(RequestDataCallback<VerificationCodeBean> callback);
    }

    interface View extends BaseView {
        void getDataSuccess(HomeCompanyBean homeCompanyBean);
        void getMembersData(MembersBean membersBean);
        void updateNameSuccess();
        void getPermissionsSuccess(PermissionBean permissionBean);
        void exitHomeCompanySuccess();
        void delHomeCompanySuccess();
        void getRoleListSuccess(RolesBean rolesBean);
        void generateCodeSuccess(InvitationCodeBean invitationCodeBean);
        void getMemberDetailSuccess(MemberDetailBean memberDetailBean);
        void getFail(int errorCode, String msg);
        void getDetailFail(int errorCode, String msg);
        void getSATokenSuccess(FindSATokenBean findSATokenBean);
        void getSATokenFail(int errorCode, String msg);
        void addScHomeSuccess(IdBean idBean);
        void addScHomeFail(int errorCode, String msg);
        void onVerificationCodeSuccess(String code);
        void onVerificationCodeFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getDetail(long id);
        void getMembers();
        void getVerificationCode();
        void updateName(long id, String name);
        void getPermissions(int id);
        void exitHomeCompany(long id, int user_id);
        void delHomeCompany(long id, String body);
        void getRoleList();
        void generateCode(int id, String body);
        void getMemberDetail(int id);
        void getSAToken(int userId, List<NameValuePair> requestData);
        void addScHome(AddHCRequest addHCRequest);
    }
}
