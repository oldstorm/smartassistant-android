package com.yctc.zhiting.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.CommonWebContract;
import com.yctc.zhiting.activity.presenter.CommonWebPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.JsBean;
import com.yctc.zhiting.event.FinishWebActEvent;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.JsMethodConstant;
import com.yctc.zhiting.utils.WebViewInitUtil;
import com.yctc.zhiting.utils.statusbarutil.StatusBarUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * 加载 CommonWebView
 */
public class CommonWebActivity extends MVPBaseActivity<CommonWebContract.View, CommonWebPresenter> implements CommonWebContract.View {

    @BindView(R.id.clTop)
    ConstraintLayout clTop;
    @BindView(R.id.rlTitle)
    RelativeLayout rlTitle;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    private int webUrlType;
    private String webUrl = HttpUrlConfig.baseSAUrl;
    private final String thirdPartyUrl = "https://sc.zhitingtech.com/#/third-platform";

//    private String webUrl = "http://192.168.22.91/doc/test.html";
    boolean flag;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_web;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected boolean isSetStateBar() {
        return false;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void initUI() {
        super.initUI();

        WebViewInitUtil webViewInitUtil = new WebViewInitUtil(this);
        webViewInitUtil.initWebView(webView);
        webViewInitUtil.setProgressBar(progressbar);
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + "; "+Constant.ZHITING_USER_AGENT);
        webView.addJavascriptInterface(new JsInterface(), Constant.ZHITING_APP);
        webView.setWebViewClient(new MyWebViewClient());

    }


    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        webUrlType = intent.getIntExtra(IntentConstant.WEB_URL_TYPE, 0);
        StatusBarUtil.setStatusBarDarkTheme(this, webUrlType == 1);
        if (webUrlType == 1){ // 第三方平台
            rlTitle.setVisibility(View.VISIBLE);
            webView.loadUrl(thirdPartyUrl);
        }else { // 专业版
            clTop.setVisibility(View.VISIBLE);
            webView.loadUrl(webUrl);
        }
    }

    @OnClick(R.id.ivBack)
    void clickBack(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }else {
            super.onBackPressed();
        }

    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            webView.loadUrl("javascript:" + Constant.professional_js);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            flag = true;
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

    @OnClick({R.id.llSettingNamePass, R.id.llClose})
    public void onClick(View view) {
        if (view.getId() == R.id.llSettingNamePass) {
            switchToActivity(SettingActivity.class);
        } else if (view.getId() == R.id.llClose) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        webView.clearCache(true);
        webView.destroy();
    }

    /**
     * 更新HomeBean
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FinishWebActEvent event) {
        finish();
    }

    /**
     * 网络类型
     *
     * @param jsBean
     */
    void networkType(JsBean jsBean) {
        String js = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{}')";
        runOnMainUi(js);
    }

    /**
     * 用户信息
     *
     * @param jsBean
     */
    void getUserInfo(JsBean jsBean) {
        String js = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{\"userId\":" + Constant.CurrentHome.getUser_id() + ",\"token\":\"" + Constant.CurrentHome.getSa_user_token() + "\"}')";
        runOnMainUi(js);
    }

    /**
     * 设置标题
     *
     * @param jsBean
     */
    void setTitle(JsBean jsBean) {
        String js = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{}')";
        runOnMainUi(js);
    }

    /**
     * 是否专业版
     *
     * @param jsBean
     */
    void isProfession(JsBean jsBean) {
        String js = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{\"result\":true}')";
        runOnMainUi(js);
    }

    private void runOnMainUi(String js) {
        UiUtil.runInMainThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + js);
            }
        });
    }

    class JsInterface {

        @SuppressLint("JavascriptInterface")
        @JavascriptInterface
        public void entry(String json) {
            JsBean jsBean = new Gson().fromJson(json, JsBean.class);
            switch (jsBean.getFunc()) {
                case JsMethodConstant.NETWORK_TYPE:  // 网络类型
                    networkType(jsBean);

                    break;

                case JsMethodConstant.GET_USER_INFO:  // 用户信息
                    getUserInfo(jsBean);
                    break;

                case JsMethodConstant.SET_TITLE:  // 标题属性
                    setTitle(jsBean);
                    break;

                case JsMethodConstant.IS_PROFESSION:  // 是否专业版
                    isProfession(jsBean);
                    break;
            }

        }
    }
}