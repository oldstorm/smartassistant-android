package com.yctc.zhiting.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.yctc.zhiting.listener.WebFileChoseListener;

public class WebViewInitUtil {
    private BaseActivity activity;
    private ProgressBar progressBar;

    private WebFileChoseListener webFileChoseListener;

    public WebViewInitUtil(BaseActivity activity) {
        this.activity = activity;
    }

    public WebViewInitUtil setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
        return this;
    }

    public WebViewInitUtil setWebFileChoseListener(WebFileChoseListener webFileChoseListener) {
        this.webFileChoseListener = webFileChoseListener;
        return this;
    }


    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(WebView webView) {
        webView.setHorizontalScrollBarEnabled(false);// 滚动条水平不显示
        webView.setVerticalScrollBarEnabled(false); // 垂直不显示
        webView.getSettings().setJavaScriptEnabled(true);// 允许 JS的使用
        webView.getSettings().setAllowFileAccess(false); //修复file域漏洞
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 可以使用JS打开新窗口
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setNeedInitialFocus(false);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 解决缓存问题
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new BankWebChromeClient());
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (activity != null && !activity.isFinishing())
                activity.hideLoadingView();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            LogUtil.i("errorCode = " + errorCode + " description = " + description + " failingUrl = " + failingUrl);
            view.loadUrl("file:///android_asset/html_error_page.html");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    class BankWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                if (activity != null && !activity.isFinishing())
                    activity.hideLoadingView();
            }
            if (progressBar != null) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100)
                    progressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }


        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message,
                                        JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean onJsTimeout() {
            return super.onJsTimeout();
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (webFileChoseListener != null) {
                webFileChoseListener.getFile(filePathCallback);
            }
            return true;
        }
    }

    public static void seleteH5Phone(Intent data, ValueCallback valueCallback) {
        if (valueCallback == null) {
            // todo valueCallback 为空的逻辑
            return;
        }
        try {
            Uri[] results = null;
            String dataString = data.getDataString();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
            }

            if (dataString != null) {
                results = new Uri[]{Uri.parse(dataString)};
                valueCallback.onReceiveValue(results);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        valueCallback = null;
    }
}
