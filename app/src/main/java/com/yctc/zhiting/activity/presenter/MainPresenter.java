package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.entity.HttpResult;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.MainContract;
import com.yctc.zhiting.activity.model.MainModel;
import com.yctc.zhiting.request.AddRoomRequest;

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {
    MainModel model;

    @Override
    public void init() {
        model = new MainModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 获取订单是否 未到时，准时，超时
     *
     * @param request
     */
    @Override
    public void postOrderCheck(AddRoomRequest request) {
        mView.showLoadingView();
        model.postOrderCheck(request, new RequestDataCallback<HttpResult>() {
            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                ToastUtil.showBottom(errorMessage);
                if (null == mView) return;
                mView.hideLoadingView();
            }

            @Override
            public void onSuccess(HttpResult datas) {
                super.onSuccess(datas);
                if (null == mView) return;
                mView.hideLoadingView();
            }
        });
    }
}
