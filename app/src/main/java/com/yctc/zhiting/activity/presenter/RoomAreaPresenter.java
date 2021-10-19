package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.HCDetailContract;
import com.yctc.zhiting.activity.contract.RoomAreaContract;
import com.yctc.zhiting.activity.model.HCDetailModel;
import com.yctc.zhiting.activity.model.RoomAreaModel;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;

/**
 * 家庭/公司详情
 */
public class RoomAreaPresenter extends BasePresenterImpl<RoomAreaContract.View> implements RoomAreaContract.Presenter {

    RoomAreaModel model;

    @Override
    public void init() {
        model = new RoomAreaModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void getRoomList(boolean showLoading) {
        if (showLoading)
        mView.showLoadingView();
        model.getRoomList(new RequestDataCallback<RoomListBean>() {
            @Override
            public void onSuccess(RoomListBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getRoomListSuccess(obj);
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
    public void addRoom(String name) {
        mView.showLoadingView();
        String body = "{\"name\":\""+ name+"\"}";
        model.addRoom(body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.addRoomSuccess();
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
     * 排序
     * @param locations_id
     */
    @Override
    public void orderRoom(String locations_id) {
        mView.showLoadingView();
        model.orderRoom(locations_id, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.orderRoomSuccess();
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
                    mView.requestFail(errorCode, errorMessage);
                }
            }
        });
    }
}
