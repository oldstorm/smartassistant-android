package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.DepartmentDetail;
import com.yctc.zhiting.entity.mine.MembersBean;

public interface AddMemberContract {
    interface Model {
        void getMembers(RequestDataCallback<MembersBean> callback);
        void getDepartmentDetail(int id, RequestDataCallback<DepartmentDetail> callback);
        void addDMDepartmentMember(int id, String json, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void getMembersSuccess(MembersBean membersBean);
        void getMembersFail(int errorCode, String msg);
        void getDepartmentDetailSuccess(DepartmentDetail userBean);
        void getDepartmentDetailFail(int errorCode, String msg);
        void addDMDepartmentMemberSuccess();
        void addDMDepartmentMemberFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getMembers();
        void getDepartmentDetail(int id);
        void addDMDepartmentMember(int id, String json);
    }
}
