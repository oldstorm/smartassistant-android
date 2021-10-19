package com.yctc.zhiting.activity.presenter;


import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.SceneDetailDeviceContract;
import com.yctc.zhiting.activity.contract.SceneDevicesContract;
import com.yctc.zhiting.activity.model.SceneDetailDevicesModel;
import com.yctc.zhiting.activity.model.SceneDevicesModel;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.RoomListBean;

/**
 * 场景可控制设备列表
 */
public class SceneDetailDevicesPresenter extends BasePresenterImpl<SceneDetailDeviceContract.View> implements SceneDetailDeviceContract.Presenter {

    private SceneDetailDevicesModel model;

    @Override
    public void init() {
        model = new SceneDetailDevicesModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 设备列表
     */
    @Override
    public void getDeviceList() {
        model.getDeviceList(new RequestDataCallback<RoomDeviceListBean>() {
            @Override
            public void onSuccess(RoomDeviceListBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getDeviceListSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getDeviceFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 房间列表
     */
    @Override
    public void getRoomList() {
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
}
