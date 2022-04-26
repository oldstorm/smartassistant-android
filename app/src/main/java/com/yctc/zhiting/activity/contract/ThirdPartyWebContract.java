package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.RemoveHCBean;

public interface ThirdPartyWebContract {
    interface Model {
        void unbindThirdParty(String id, String areaId, RequestDataCallback<Object> callback);
        void getMemberDetail(int id, RequestDataCallback<MemberDetailBean> callback);
    }

    interface View extends BaseView {
        void unbindThirdPartySuccess();
        void unbindThirdPartyFail(int errorCode, String msg);
        void getMemberDetailSuccess(MemberDetailBean memberDetailBean);
        void getMemberDetailFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void unbindThirdParty(String id, String areaId);
        void getMemberDetail(int id);
    }
}
