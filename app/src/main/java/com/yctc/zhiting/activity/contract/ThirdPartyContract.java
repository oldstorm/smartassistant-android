package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.ThirdPartyBean;

import java.util.List;

public interface ThirdPartyContract {

    public interface Model {
        void getThirdPartyList (boolean sc,List<NameValuePair> requestData, RequestDataCallback<ThirdPartyBean> callback);
    }

    public interface View extends BaseView {
        void getThirdPartyListSuccess(ThirdPartyBean thirdPartyBean);
        void getThirdPartyListFail(int errorCode, String msg);
    }

    public interface Presenter extends BasePresenter<View> {
        void getThirdPartyList (boolean sc,List<NameValuePair> requestData, boolean showLoading);
    }
}
