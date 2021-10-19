package com.yctc.zhiting.activity.presenter;

import com.app.main.framework.baseview.BasePresenterImpl;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.activity.contract.BindCloudContract;
import com.yctc.zhiting.activity.model.BindCloudModel;
import com.yctc.zhiting.entity.mine.CaptchaBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

import java.util.List;


/**
 * 绑定云
 */
public class BindCloudPresenter extends BasePresenterImpl<BindCloudContract.View> implements BindCloudContract.Presenter {

    private BindCloudModel model;

    @Override
    public void init() {
        model = new BindCloudModel();
    }

    @Override
    public void clear() {
        model = null;
    }

    /**
     * 验证码
     * @param requestData
     */
    @Override
    public void getCaptcha(List<NameValuePair> requestData) {
        if (mView!=null) {
            mView.showLoadingView();
        }
        model.getCaptcha(requestData, new RequestDataCallback<CaptchaBean>() {
            @Override
            public void onSuccess(CaptchaBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getCaptchaSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.hideLoadingView();
                    mView.getCaptchaFail(errorCode, errorMessage);
                }
            }
        });
    }

    /**
     * 注册
     * @param body
     */
    @Override
    public void register(String body) {
        model.register(body, new RequestDataCallback<MemberDetailBean>() {
            @Override
            public void onSuccess(MemberDetailBean obj) {
                super.onSuccess(obj);
                if (mView!=null){
                    mView.registerSuccess(obj);
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                if (mView!=null){
                    mView.registerFail(errorCode, errorMessage);
                }
            }
        });
    }
}
