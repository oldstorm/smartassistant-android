package com.yctc.zhiting.activity.contract;
import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.entity.HttpResult;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.request.AddRoomRequest;

public interface MainContract {
    interface Model {
        void postOrderCheck(AddRoomRequest request, RequestDataCallback<HttpResult> callback);
    }

    interface View extends BaseView {
        void postOrderCheckSuccess(HttpResult msg);
    }

    interface Presenter extends BasePresenter<View> {
        void postOrderCheck(AddRoomRequest request);
    }
}
