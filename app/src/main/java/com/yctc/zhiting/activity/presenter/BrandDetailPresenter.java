package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.activity.contract.SupportBrandContract;
import com.yctc.zhiting.activity.model.BrandDetailModel;
import com.yctc.zhiting.activity.model.SupportBrandModel;
import com.yctc.zhiting.entity.mine.BrandDetailBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.BrandsBean;

/**
 * 添加设备
 */
public class BrandDetailPresenter extends BasePresenterImpl<BrandDetailContract.View> implements BrandDetailContract.Presenter {

    private BrandDetailModel model;

    @Override
    public void init() {
        model = new BrandDetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getDetail(String name) {
        mView.showLoadingView();
        model.getDetail(name, new RequestDataCallback<BrandDetailBean>() {
            @Override
            public void onSuccess(BrandDetailBean obj) {
                super.onSuccess(obj);
                mView.hideLoadingView();
                mView.getDetailSuccess(obj);
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                mView.hideLoadingView();
                mView.getDetailFail(errorCode, errorMessage);
            }
        });
    }
}
