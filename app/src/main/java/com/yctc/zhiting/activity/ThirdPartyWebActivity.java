package com.yctc.zhiting.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.ThirdPartyWebContract;
import com.yctc.zhiting.activity.presenter.ThirdPartyWebPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.RevokeAuthorizationDialog;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.ThirdPartyBean;
import com.yctc.zhiting.request.UnbindRequest;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.utils.WebViewInitUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 第三方平台--解除绑定
 */
public class ThirdPartyWebActivity extends MVPBaseActivity<ThirdPartyWebContract.View, ThirdPartyWebPresenter> implements ThirdPartyWebContract.View {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.tvRevoke)
    TextView tvRevoke;

    private ThirdPartyBean.AppsBean appsBean;
    private String mThirdPartyName;
    private Integer appId;
    private boolean isBind;
    private RevokeAuthorizationDialog mRevokeAuthorizationDialog;
    private boolean unbindSuccess;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_third_party_web;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
        WebViewInitUtil webViewInitUtil = new WebViewInitUtil(this);
        webViewInitUtil.initWebView(webView);
        webViewInitUtil.setProgressBar(progressbar);
        webView.setWebViewClient(new MyWebViewClient());
        if (HomeUtil.isSAEnvironment() || UserUtils.isLogin()) {  // 在SA环境下或登录的情况下取获取用户信息
            mPresenter.getMemberDetail(Constant.CurrentHome.getUser_id());
        }
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        appsBean = (ThirdPartyBean.AppsBean) intent.getSerializableExtra(IntentConstant.BEAN);
        if (appsBean != null) {
            webView.loadUrl(appsBean.getLink());
            appId = appsBean.getApp_id();
            mThirdPartyName = appsBean.getName();
            isBind = appsBean.isIs_bind();
        }
    }

    /**
     * 返回
     */
    @Override
    public void onBackPressed() {
        if (unbindSuccess) {
            setResult(RESULT_OK);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick({R.id.ivBack, R.id.tvRevoke})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivBack:  // 返回
                onBackPressed();
                break;

            case R.id.tvRevoke:  // 解除绑定
                if (UserUtils.isLogin()) {  // 登录了
                    revoke();
                } else {  // 没登录去登录
                    switchToActivity(LoginActivity.class);
                }
                break;
        }
    }

    /**
     * 解除绑定
     */
    private void revoke() {
        if (mRevokeAuthorizationDialog == null) {
            mRevokeAuthorizationDialog = RevokeAuthorizationDialog.getInstance(mThirdPartyName);
            mRevokeAuthorizationDialog.setmRevokeAuthorizationListener(new RevokeAuthorizationDialog.RevokeAuthorizationListener() {
                @Override
                public void revokeAuthorization() {
                    mRevokeAuthorizationDialog.setLoading(true);
                    long areaId = Constant.CurrentHome.getId() > 0 ?  Constant.CurrentHome.getId() : Constant.CurrentHome.getArea_id();
                    mPresenter.unbindThirdParty(String.valueOf(appId), String.valueOf(areaId));
                }
            });
        }
        mRevokeAuthorizationDialog.show(this);
    }

    /**
     * 加载之后
     */
    private void finishLoading() {
        if (mRevokeAuthorizationDialog != null && mRevokeAuthorizationDialog.isShowing()) {
            mRevokeAuthorizationDialog.setLoading(false);
            mRevokeAuthorizationDialog.dismiss();
        }
    }

    /**
     * 解绑第三方平台成功
     */
    @Override
    public void unbindThirdPartySuccess() {
        ToastUtil.show(UiUtil.getString(R.string.mine_authorization_has_been_revoked));
        unbindSuccess = true;
        tvRevoke.setVisibility(View.GONE);
        finishLoading();
    }

    /**
     * 解绑第三方平台失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void unbindThirdPartyFail(int errorCode, String msg) {
        ToastUtil.show(UiUtil.getString(R.string.mine_failed_to_revoke_authorization));
        finishLoading();
    }

    /**
     * 成员详情成功
     * @param memberDetailBean
     */
    @Override
    public void getMemberDetailSuccess(MemberDetailBean memberDetailBean) {
        if (memberDetailBean != null) {
            boolean isOwner = memberDetailBean.isIs_owner();
            tvRevoke.setVisibility(isBind && isOwner ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 成员详情失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getMemberDetailFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showLoadingDialogInAct();
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            dismissLoadingDialogInAct();
            progressbar.setVisibility(View.GONE);
            LogUtil.e(TAG + "onPageFinished");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            LogUtil.e(TAG + "onReceivedError=errorCode=" + errorCode + ",description=" + description + ",failingUrl=" + failingUrl);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            LogUtil.e(TAG + "shouldOverrideUrlLoading");
            return true;
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }
    }
}