package com.yctc.zhiting.activity.presenter;

import android.util.Log;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.ScanContract;
import com.yctc.zhiting.activity.model.ScanModel;
import com.yctc.zhiting.entity.GenerateCodeJson;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.request.BindCloudRequest;

import java.util.ArrayList;

/**
 * 扫描二维码
 */
public class ScanPresenter extends BasePresenterImpl<ScanContract.View> implements ScanContract.Presenter {

    private ScanModel model;

    @Override
    public void init() {
        model = new ScanModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    @Override
    public void invitationCheck(String body, GenerateCodeJson qrCode) {
        mView.showLoadingView();
        model.invitationCheck(body, qrCode, new RequestDataCallback<InvitationCheckBean>() {
            @Override
            public void onSuccess(InvitationCheckBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.invitationCheckSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.hideLoadingView();
                    mView.invitationCheckFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 创建云端家庭
     *
     * @param homeName
     */
    @Override
    public void createHomeSC(String homeName) {
        SynPost.AreaBean areaBean = new SynPost.AreaBean(homeName, new ArrayList<>());//家庭
        model.createHomeSC(areaBean, new RequestDataCallback<IdBean>() {
            @Override
            public void onSuccess(IdBean obj) {
                super.onSuccess(obj);
                if (mView != null) {
                    mView.createHomeSCSuccess(obj.getId());
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView != null) {
                    mView.createHomeSCFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 绑定SC
     * @param request
     */
    @Override
    public void bindCloudSC(BindCloudRequest request) {
        Log.e("bindCloudSC","bindCloudSC0=");
        model.bindCloudSC(request, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                Log.e("bindCloudSC","bindCloudSC1=");
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                Log.e("bindCloudSC","bindCloudSC2="+errorCode+",errorMessage="+errorMessage);
            }
        });
    }
}
