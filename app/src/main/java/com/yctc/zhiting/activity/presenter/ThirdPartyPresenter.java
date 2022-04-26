package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ThirdPartyContract;
import com.yctc.zhiting.activity.model.ThirdPartyModel;
import com.yctc.zhiting.entity.mine.ThirdPartyBean;

import java.util.List;

public class ThirdPartyPresenter extends BasePresenterImpl<ThirdPartyContract.View> implements ThirdPartyContract.Presenter {

    private ThirdPartyModel model;

    @Override
    public void init() {
        model = new ThirdPartyModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 第三方云绑定列表
     *
     * @param requestData
     */
    @Override
    public void getThirdPartyList(boolean sc,List<NameValuePair> requestData, boolean showLoading) {
        if (showLoading && mView != null) {
            mView.showLoadingView();
        }

        model.getThirdPartyList(sc, requestData, new RequestDataCallback<ThirdPartyBean>() {
            @Override
            public void onSuccess(ThirdPartyBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getThirdPartyListSuccess(obj);
                    mView.hideLoadingView();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getThirdPartyListFail(errorCode, errorMessage);
                    mView.hideLoadingView();
                }
            }
        });
    }
}
