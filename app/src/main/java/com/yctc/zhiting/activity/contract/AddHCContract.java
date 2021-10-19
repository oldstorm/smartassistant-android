package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.LocationsBean;

/**
 * 添加家庭/公司等区域
 */
public interface AddHCContract {
    interface Model {
        void getDefaultRoom(RequestDataCallback<LocationsBean> callback);

        void postCreateSCHome(SynPost.AreaBean areaBean, RequestDataCallback<IdBean> callback);
    }

    interface View extends BaseView {
        void getDataSuccess(LocationsBean locationsBean);

        void getDataFail(String msg);

        void onCreateSCHomeSuccess(IdBean data);

        void onCreateSCHomeFail(String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getDefaultRoom();

        void postCreateSCHome(SynPost.AreaBean areaBean);
    }
}
