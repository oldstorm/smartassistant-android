package com.yctc.zhiting.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.NormalWebContract;
import com.yctc.zhiting.activity.presenter.CommonWebPresenter;
import com.yctc.zhiting.activity.presenter.NormalWebPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.WebViewInitUtil;

import butterknife.BindView;

public class NormalWebActivity extends MVPBaseActivity<NormalWebContract.View, NormalWebPresenter> implements NormalWebContract.View {

    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.webView)
    WebView webView;

    private String title;
    private String url;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_normal_web;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        title = intent.getStringExtra(IntentConstant.TITLE);
        url = intent.getStringExtra(IntentConstant.URL);
        setTitleCenter(title);
        WebViewInitUtil webViewInitUtil = new WebViewInitUtil(this);
        webViewInitUtil.setProgressBar(progressbar);
        webViewInitUtil.initWebView(webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
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