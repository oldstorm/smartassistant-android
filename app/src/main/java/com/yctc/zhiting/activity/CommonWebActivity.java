package com.yctc.zhiting.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
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
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.CommonWebContract;
import com.yctc.zhiting.activity.presenter.CommonWebPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.JsBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.event.FinishWebActEvent;
import com.yctc.zhiting.event.HomeEvent;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.utils.WebViewInitUtil;
import com.yctc.zhiting.utils.statusbarutil.StatusBarUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 加载 CommonWebView
 */
public class CommonWebActivity extends MVPBaseActivity<CommonWebContract.View, CommonWebPresenter> implements CommonWebContract.View {

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    private String webUrl = HttpUrlConfig.baseUrl+"#/";
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
        StatusBarUtil.setStatusBarDarkTheme(this, false);
        WebViewInitUtil webViewInitUtil = new WebViewInitUtil(this);
        webViewInitUtil.initWebView(webView);
        webViewInitUtil.setProgressBar(progressbar);
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + "; zhitingua");
        webView.addJavascriptInterface(new JsInterface(), "zhitingApp");
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(webUrl);
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
        System.out.println("结果：" + js);
        runOnMainUi(js);
    }

    /**
     * 用户信息
     *
     * @param jsBean
     */
    void getUserInfo(JsBean jsBean) {
        String js = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{\"userId\":" + Constant.CurrentHome.getUser_id() + ",\"token\":\"" + Constant.CurrentHome.getSa_user_token() + "\"}')";
        System.out.println("结果：" + js);
        runOnMainUi(js);
    }

    /**
     * 设置标题
     *
     * @param jsBean
     */
    void setTitle(JsBean jsBean) {
        String js = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{}')";
        System.out.println("结果：" + js);
        runOnMainUi(js);
    }

    /**
     * 是否专业版
     *
     * @param jsBean
     */
    void isProfession(JsBean jsBean) {
        String js = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{\"result\":true}')";
        System.out.println("结果：" + js);
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
            System.out.println("json结果：" + json);
            JsBean jsBean = new Gson().fromJson(json, JsBean.class);
            switch (jsBean.getFunc()) {
                case "networkType":
                    networkType(jsBean);

                    break;

                case "getUserInfo":
                    getUserInfo(jsBean);
                    break;

                case "setTitle":
                    setTitle(jsBean);
                    break;

                case "isProfession":
                    isProfession(jsBean);
                    break;
            }

        }
    }
}