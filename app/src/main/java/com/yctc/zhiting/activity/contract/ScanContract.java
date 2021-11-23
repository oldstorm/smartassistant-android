package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.GenerateCodeJson;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.entity.mine.RolesBean;
import com.yctc.zhiting.request.BindCloudRequest;


/**
 * 扫描二维码
 */
public interface ScanContract {
    interface Model {
        void invitationCheck(String body, GenerateCodeJson qrCode, String tempChannelUrl, RequestDataCallback<InvitationCheckBean> callback);

        void createHomeSC(SynPost.AreaBean request, RequestDataCallback<IdBean> callback);

        void bindCloudSC(BindCloudRequest request, RequestDataCallback<Object> callback);
    }

    interface View extends BaseView {
        void invitationCheckSuccess(InvitationCheckBean invitationCheckBean);

        void invitationCheckFail(int errorCode, String msg);

        void createHomeSCSuccess(IdBean idBean);

        void createHomeSCFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void invitationCheck(String name, GenerateCodeJson qrCode, String tempChannelUrl);

        void createHomeSC(String homeName);

        void bindCloudSC(BindCloudRequest request);
    }
}
