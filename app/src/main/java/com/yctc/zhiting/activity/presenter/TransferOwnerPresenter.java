package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.HCDetailContract;
import com.yctc.zhiting.activity.contract.TransferOwnerContract;
import com.yctc.zhiting.activity.model.TransferOwnerModel;
import com.yctc.zhiting.entity.mine.MembersBean;

public class TransferOwnerPresenter extends BasePresenterImpl<TransferOwnerContract.View> implements TransferOwnerContract.Presenter{

    private TransferOwnerModel model;

    @Override
    public void init() {
        model = new TransferOwnerModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 成员列表
     */
    @Override
    public void getMembers() {
        model.getMembers(new RequestDataCallback<MembersBean>() {
            @Override
            public void onSuccess(MembersBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.getMembersSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                mView.getMemberFail(errorCode, errorMessage);
            }
        });
    }

    /**
     * 转移拥有者
     * @param userId
     */
    @Override
    public void transferOwner(int userId) {
        model.transferOwner(userId, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.transferSuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.transferFail(errorCode, errorMessage);
                }
            }
        });
    }


}
