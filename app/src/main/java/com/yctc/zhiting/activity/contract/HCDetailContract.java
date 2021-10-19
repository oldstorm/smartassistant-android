package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.InvitationCodeBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.MembersBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;

import java.util.List;

/**
 * 家庭/公司详情
 */
public interface HCDetailContract {
    interface Model {
        void getDetail(int id, RequestDataCallback<HomeCompanyBean> callback);
        void getMembers(RequestDataCallback<MembersBean> callback);
        void updateName(int id, String name, RequestDataCallback<Object> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
        void exitHomeCompany(int id, int user_id, RequestDataCallback<Object> callback);
        void delHomeCompany(int id, RequestDataCallback<Object> callback);
        void getRoleList(RequestDataCallback<RolesBean> callback);
        void generateCode(int id, String body, RequestDataCallback<InvitationCodeBean> callback);
        void getMemberDetail(int id, RequestDataCallback<MemberDetailBean> callback);
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

    }

    interface Presenter extends BasePresenter<View> {
        void getDetail(int id);
        void getMembers();
        void updateName(int id, String name);
        void getPermissions(int id);
        void exitHomeCompany(int id, int user_id);
        void delHomeCompany(int id);
        void getRoleList();
        void generateCode(int id, String body);
        void getMemberDetail(int id);
    }
}
