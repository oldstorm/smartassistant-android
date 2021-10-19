package com.yctc.zhiting.activity.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.BrandListBean;
import com.yctc.zhiting.entity.scene.LogBean;

import java.util.List;


/**
 * 支持品牌
 */
public interface SupportBrandContract {
    interface Model {
        void getBrandList(List<NameValuePair> requestData, RequestDataCallback<BrandListBean> callback);
    }

    interface View extends BaseView {
        void getBrandListSuccess(BrandListBean brandListBean);
        void getBrandListFail(int errorCode, String msg);

    }

    interface Presenter extends BasePresenter<View> {
        void getBrandList(List<NameValuePair> requestData, boolean showLoading);
    }
}
