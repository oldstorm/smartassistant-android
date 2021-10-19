package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;

/**
 * 家庭/公司详情
 */
public interface MemberDetailContract {
    interface Model {
        void getMemberDetail(int id, RequestDataCallback<MemberDetailBean> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
        void getRoleList(RequestDataCallback<RolesBean> callback);
        void updateMember(int id, String body, RequestDataCallback<Object> callback);
        void delMember(int id, RequestDataCallback<Object> callback);

    }

    interface View extends BaseView {
        void getDataSuccess(MemberDetailBean memberDetailBean);
        void getPermissionsSuccess(PermissionBean permissionBean);
        void getRoleListSuccess(RolesBean rolesBean);
        void updateSuccess();
        void requestFail(int errorCode, String msg);
        void delMemberSuccess();
    }

    interface Presenter extends BasePresenter<View> {
        void getMemberDetail(int id);
        void getPermissions(int id);
        void getRoleList();
        void updateMember(int id, String body);
        void delMember(int id);
    }
}
