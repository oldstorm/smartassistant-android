
package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ThirdPartyWebContract;
import com.yctc.zhiting.activity.model.ThirdPartyWebModel;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

public class ThirdPartyWebPresenter extends BasePresenterImpl<ThirdPartyWebContract.View> implements ThirdPartyWebContract.Presenter {

    private ThirdPartyWebModel model;

    @Override
    public void init() {
        model = new ThirdPartyWebModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 解绑第三方
     * @param id 第三方id
     * @param areaId 家庭id
     */
    @Override
    public void unbindThirdParty(String id, String areaId) {
        model.unbindThirdParty(id, areaId, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (mView!=null) {
                    mView.unbindThirdPartySuccess();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null) {
                    mView.unbindThirdPartyFail(errorCode, errorMessage);
                }
            }
        });
    }
    /**
     * 成员详情
     * @param id
     */
    @Override
    public void getMemberDetail(int id) {
        mView.showLoadingView();
        model.getMemberDetail(id, new RequestDataCallback<MemberDetailBean>() {
            @Override
            public void onSuccess(MemberDetailBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getMemberDetailSuccess(obj);
                }

            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getMemberDetailFail(errorCode, errorMessage);
                }
            }
        });
    }

}
