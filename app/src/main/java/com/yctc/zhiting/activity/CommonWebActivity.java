package com.yctc.zhiting.activity;

import static com.yctc.zhiting.config.Constant.CurrentHome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.config.HttpBaseUrl;
import com.app.main.framework.entity.ChannelEntity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.TempChannelUtil;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.CommonWebContract;
import com.yctc.zhiting.activity.presenter.CommonWebPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.JsBean;
import com.yctc.zhiting.event.FinishWebActEvent;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.JsMethodConstant;
import com.yctc.zhiting.utils.WebViewInitUtil;
import com.yctc.zhiting.utils.statusbarutil.StatusBarUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.tvWebTitle)
    TextView tvWebTitle;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.llSettingNamePass)
    LinearLayout llSettingNamePass;
    @BindView(R.id.llClose)
    LinearLayout llClose;

    /**
     * 1. 第三方平台
     * 2. crm系统
     * 3. scm系统
     * 4. 离线帮助
     * default 专业版
     */
    private int webUrlType;
    private String webUrl = HttpUrlConfig.baseSAUrl;
    private final String thirdPartyUrl = HttpBaseUrl.baseSCUrl + Constant.THIRD_PLATFORM;
    private final String offlineUrl = HttpBaseUrl.baseSCUrl + Constant.OFFLINE_HELP;

    private ValueCallback mValueCallback;
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
        webViewInitUtil.setProgressBar(progressbar)
                .setWebFileChoseListener(valueCallback -> {
                    mValueCallback = valueCallback;
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, Constant.OPEN_FILE);
                });
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + "; " + Constant.ZHITING_USER_AGENT);
        webView.addJavascriptInterface(new JsInterface(), Constant.ZHITING_APP);
        webView.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.OPEN_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                WebViewInitUtil.seleteH5Phone(data, mValueCallback);
            } else {
                mValueCallback.onReceiveValue(null);
                mValueCallback = null;
            }
        }
    }

    private String crmUrl; // 客户管理系统
    private String scmUrl; // 供应链管理

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        webUrlType = intent.getIntExtra(IntentConstant.WEB_URL_TYPE, 0);
        StatusBarUtil.setStatusBarDarkTheme(this, webUrlType == 1);
        // 如果不是第三方平台过来且不在SA环境下
        saveTempChannel();
        if (webUrlType == 1) { // 第三方平台
            ivBack.setVisibility(View.VISIBLE);
            tvWebTitle.setText(getResources().getText(R.string.mine_third_party));
            tvWebTitle.setVisibility(View.VISIBLE);
            rlTitle.setVisibility(View.VISIBLE);
            webView.loadUrl(thirdPartyUrl);
        } else if (webUrlType == 2) {  // crm系统
            tvTitle.setText(getResources().getText(R.string.mine_crm_system));
            llClose.setBackgroundResource(R.drawable.shape_stroke_7e849b_c14);
            clTop.setVisibility(View.VISIBLE);
            crmUrl = webUrl + Constant.CRM_MIDDLE_URL + CurrentHome.getSa_user_token();
            webView.loadUrl(crmUrl);
        } else if (webUrlType == 3) {  // 供应链系统
            tvTitle.setText(getResources().getText(R.string.mine_supply_chain));
            llClose.setBackgroundResource(R.drawable.shape_stroke_7e849b_c14);
            clTop.setVisibility(View.VISIBLE);
            scmUrl = webUrl + Constant.SCM_MIDDLE_URL + CurrentHome.getSa_user_token();
            LogUtil.e("供应链："+scmUrl);
            webView.loadUrl(scmUrl);
        } else if (webUrlType == 4) {  // 离线帮助
            ivBack.setVisibility(View.VISIBLE);
            tvWebTitle.setText(getResources().getText(R.string.home_offline_help));
            tvWebTitle.setVisibility(View.VISIBLE);
            rlTitle.setVisibility(View.VISIBLE);
            webView.loadUrl(offlineUrl);
        } else { // 专业版
            tvTitle.setText(getResources().getText(R.string.mine_professional));
            llSettingNamePass.setVisibility(View.VISIBLE);
            clTop.setVisibility(View.VISIBLE);
            webView.loadUrl(webUrl);
        }
    }

    private void saveTempChannel() {
        if (webUrlType != 1 && webUrlType != 4 && !HomeUtil.isSAEnvironment()) {
            long homeId = 0;
            if (CurrentHome != null) {
                homeId = CurrentHome.getArea_id() == 0 ? CurrentHome.getId() : CurrentHome.getArea_id();
            }

            HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(homeId));
            HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, CurrentHome.getSa_user_token());

            String tempJson = SpUtil.get(CurrentHome.getSa_user_token());
            ChannelEntity channel = GsonConverter.getGson().fromJson(tempJson, ChannelEntity.class);
            List<NameValuePair> requestData = new ArrayList<>();
            requestData.add(new NameValuePair(TempChannelUtil.TEMP_CHANNEL_PARAM, HttpBaseUrl.HTTPS));
            if (channel == null) {
                HTTPCaller.getInstance().get(ChannelEntity.class, TempChannelUtil.CHANNEL_URL, requestData,
                        new RequestDataCallback<ChannelEntity>() {
                            @Override
                            public void onSuccess(ChannelEntity obj) {
                                super.onSuccess(obj);
                                if (obj != null) {
                                    Log.e(TAG, "checkTemporaryUrl=onSuccess123=");
                                    webUrl = Constant.HTTPS_HEAD + obj.getHost();
                                    TempChannelUtil.saveTempChannelUrl(obj);
                                    if (webUrlType == 2) {  // crm系统
                                        crmUrl = webUrl + Constant.CRM_MIDDLE_URL + CurrentHome.getSa_user_token();
                                        webView.loadUrl(crmUrl);
                                    } else if (webUrlType == 3) {  // 供应链系统
                                        scmUrl = webUrl + Constant.SCM_MIDDLE_URL + CurrentHome.getSa_user_token();
                                        webView.loadUrl(scmUrl);
                                    } else { // 专业版
                                        webView.loadUrl(webUrl);
                                    }
                                }
                            }

                            @Override
                            public void onFailed(int errorCode, String errorMessage) {
                                super.onFailed(errorCode, errorMessage);
                                Log.e(TAG, "checkTemporaryUrl=onFailed123=");

                            }
                        });
            } else {
                webUrl = Constant.HTTPS_HEAD + channel.getHost();
            }
        }
    }

    @OnClick(R.id.ivBack)
    void clickBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (webUrlType == 0) {
            super.onBackPressed();
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showLoadingDialogInAct();
            webView.loadUrl("javascript:" + Constant.professional_js);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            flag = true;
            dismissLoadingDialogInAct();
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
        webView.clearHistory();
        webView.removeAllViews();
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
        String js = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{\"userId\":" + CurrentHome.getUser_id() + ",\"token\":\"" + CurrentHome.getSa_user_token() + "\"}')";
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
        UiUtil.runInMainThread(() -> webView.loadUrl("javascript:" + js));
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