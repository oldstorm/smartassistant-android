package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.AddHCContract;
import com.yctc.zhiting.activity.model.AddHCModel;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.LocationsBean;
import com.yctc.zhiting.request.AddHCRequest;

/**
 * 添加家庭/公司等区域
 */
public class AddHCPresenter extends BasePresenterImpl<AddHCContract.View> implements AddHCContract.Presenter {

    private AddHCModel model;

    @Override
    public void init() {
        model = new AddHCModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getDefaultRoom() {
        //mView.showLoadingView();
        model.getDefaultRoom(new RequestDataCallback<LocationsBean>() {
            @Override
            public void onSuccess(LocationsBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    //mView.hideLoadingView();
                    mView.getDataSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    //mView.hideLoadingView();
                    mView.getDataFail(errorMessage);
                }
            }
        });
    }

    @Override
    public void postCreateSCHome(SynPost.AreaBean areaBean) {
        mView.showLoadingView();
        model.postCreateSCHome(areaBean, new RequestDataCallback<IdBean>() {
            @Override
            public void onSuccess(IdBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    if (obj != null)
                        mView.onCreateSCHomeSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.onCreateSCHomeFail(errorMessage);
                }
            }
        });
    }

    /**
     * 添加云端家庭
     * @param addHCRequest
     */
    @Override
    public void addScHome(AddHCRequest addHCRequest) {
        if (mView!=null){
            mView.showLoadingView();
        }
        model.addScHome(addHCRequest, new RequestDataCallback<IdBean>() {
            @Override
            public void onSuccess(IdBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.addScHomeSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.addScHomeFail(errorCode, errorMessage);
                }
            }
        });
    }
}
