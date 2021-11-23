package com.yctc.zhiting.fragment.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.fragment.contract.HomeItemFragmentContract;
import com.yctc.zhiting.fragment.model.HomeItemFragmentModel;

public class HomeItemFragmentPresenter extends BasePresenterImpl<HomeItemFragmentContract.View> implements HomeItemFragmentContract.Presenter {
    HomeItemFragmentModel model;

    @Override
    public void init() {
        model = new HomeItemFragmentModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getBrandList() {
//        mView.showLoadingView();
//        model.getBrandList(new RequestDataCallback<BrandBean>() {
//            @Override
//            public void onSuccess(BrandBean obj) {
//                if (mView != null) {
//                    mView.hideLoadingView();
//                    ToastUtil.showCenter("成功");
//                }
//            }
//
//            @Override
//            public void onFailed(int errorCode, String errorMessage) {
//                if (null == mView) return;
//                mView.hideLoadingView();
//                ToastUtil.showCenter("失败="+errorCode+",errorMessage="+errorMessage);
//            }
//        });
    }

    @Override
    public void postAddDevice() {
        mView.showLoadingView();
//        model.postAddDevice(new RequestAddDeviceBean(),new RequestDataCallback<DeviceBean>() {
//            @Override
//            public void onSuccess(DeviceBean obj) {
//                if (mView != null) {
//                    mView.hideLoadingView();
//                    ToastUtil.toastLong("成功"+obj.getDevice_id()+",url="+obj.getPlugin_url());
//                }
//            }
//
//            @Override
//            public void onFailed(int errorCode, String errorMessage) {
//                if (null == mView) return;
//                mView.hideLoadingView();
//                ToastUtil.toastLong("失败=code="+errorCode+"="+errorMessage);
//            }
//        });
    }

    /**
     * 插件详情
     * @param id
     */
    @Override
    public void getPluginDetail(String id) {
        model.getPluginDetail(id, new RequestDataCallback<PluginDetailBean>() {
            @Override
            public void onSuccess(PluginDetailBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getPluginDetailSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.getPluginDetailFail(errorCode,errorMessage);
                }
            }
        });
    }
}
