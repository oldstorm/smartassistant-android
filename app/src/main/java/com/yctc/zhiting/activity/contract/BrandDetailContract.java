package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.BrandDetailBean;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.mine.BrandsBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;


/**
 * 品牌详情
 */
public interface BrandDetailContract {
    interface Model {
        void getDetail(String name, RequestDataCallback<BrandDetailBean> callback);
    }

    interface View extends BaseView {
        void getDetailSuccess(BrandDetailBean brandsBean);
        void getDetailFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getDetail(String name);
    }
}
