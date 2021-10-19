package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.RADetailContract;
import com.yctc.zhiting.activity.model.RADetailModel;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RADetailBean;

/**
 * 房间/区域详情
 */
public class RADetailPresenter extends BasePresenterImpl<RADetailContract.View> implements RADetailContract.Presenter {

    RADetailModel model;

    @Override
    public void init() {
        model = new RADetailModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 房间/区域详情
     * @param id
     */
    @Override
    public void getDetail(int id) {
        mView.showLoadingView();
        model.getDetail(id, new RequestDataCallback<RADetailBean>() {
            @Override
            public void onSuccess(RADetailBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getDataSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 修改房间名称
     * @param id
     * @param name
     */
    @Override
    public void updateName(int id, String name) {
        mView.showLoadingView();
        String body = "{\"name\":\""+ name+"\"}";
        model.updateName(id, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.updateNameSuccess();
                }

            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }

    @Override
    public void delRoom(int id) {
        mView.showLoadingView();
        model.delRoom(id, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.delRoomSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }

    @Override
    public void getPermissions(int id) {
        model.getPermissions(id, new RequestDataCallback<PermissionBean>() {
            @Override
            public void onSuccess(PermissionBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.getPermissionsSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }
}
