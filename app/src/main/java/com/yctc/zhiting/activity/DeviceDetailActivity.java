package com.yctc.zhiting.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.imageutil.GlideUtil;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DeviceDetailContract;
import com.yctc.zhiting.activity.presenter.DeviceDetailPresenter;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.JsBean;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.event.FinishDeviceDetailEvent;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.JsMethodConstant;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

import static com.yctc.zhiting.config.Constant.CurrentHome;

/**
 * 设备详情
 */
public class DeviceDetailActivity extends MVPBaseActivity<DeviceDetailContract.View, DeviceDetailPresenter> implements DeviceDetailContract.View {

    private int id;
    private int locationId;
    private boolean is_sa;
    private String name;
    private String logoUrl;
    private String plugin_url;
    private String raName;

    private String urlPath;
    private DeviceMultipleBean mDeviceMultipleBean;
    private JsMethodConstant jsMethodConstant;

    @BindView(R.id.ivSetting)
    ImageView ivSetting;
    @BindView(R.id.ivCover)
    ImageView ivCover;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.llSA)
    LinearLayout llSA;
    @BindView(R.id.llCommonDevice)
    LinearLayout llCommonDevice;
    @BindView(R.id.rlSoftWareUpgrade)
    RelativeLayout rlSoftWareUpgrade;
    @BindView(R.id.rlFirmWareUpgrade)
    RelativeLayout rlFirmwareUpgrade;
    @BindView(R.id.rlTitle)
    RelativeLayout rlTitle;
    @BindView(R.id.line)
    View line;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_detail;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
        WebSettings webSettings = webView.getSettings();
        //设置支持javaScript脚步语言
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + "; " + Constant.ZHITING_USER_AGENT);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.addJavascriptInterface(new JsInterface(), Constant.ZHITING_APP);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        jsMethodConstant = new JsMethodConstant(webView);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.getPermissions(CurrentHome.getUser_id());
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mDeviceMultipleBean = (DeviceMultipleBean) intent.getSerializableExtra(IntentConstant.BEAN);
        urlPath = intent.getStringExtra(IntentConstant.PLUGIN_PATH);
        if (mDeviceMultipleBean != null) {
            id = mDeviceMultipleBean.getId();
            locationId = mDeviceMultipleBean.getLocation_id();
            is_sa = mDeviceMultipleBean.isIs_sa();
            name = mDeviceMultipleBean.getName();
            logoUrl = mDeviceMultipleBean.getLogo_url();
            plugin_url = mDeviceMultipleBean.getPlugin_url();
            raName = mDeviceMultipleBean.getLocation_name();

            if (is_sa) {
                llSA.setVisibility(View.VISIBLE);
                llCommonDevice.setVisibility(View.GONE);
            } else {
                llSA.setVisibility(View.GONE);
                llCommonDevice.setVisibility(View.VISIBLE);
            }

            GlideUtil.load(logoUrl).into(ivCover);
            tvName.setText(name);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getDeviceDetailRestructure(id);
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FinishDeviceDetailEvent event) {
        finish();
    }

    /**
     * 返回
     */
    @OnClick(R.id.ivBack)
    void onClickBack() {
        onBackPressed();
    }

    @OnClick({R.id.rlSoftWareUpgrade, R.id.rlFirmWareUpgrade})
    public void onClick(View view) {
        if (view.getId() == R.id.rlSoftWareUpgrade) {
            bundle = new Bundle();
            bundle.putInt("type", 1);
            switchToActivity(SoftwareUpgradeActivity.class, bundle);
        } else if (view.getId() == R.id.rlFirmWareUpgrade) {
            ToastUtil.show("功能暂未开放");
//            bundle = new Bundle();
//            bundle.putInt("type", 2);
//            switchToActivity(SoftwareUpgradeActivity.class, bundle);
        }
    }

    /**
     * 设置
     */
    @OnClick(R.id.ivSetting)
    void onClickSetting() {
        Bundle bundle = new Bundle();
        bundle.putInt(IntentConstant.ID, id);
        bundle.putString(IntentConstant.NAME, name);
        bundle.putInt(IntentConstant.RA_ID, locationId);
        bundle.putString(IntentConstant.RA_NAME, raName);
        bundle.putString(IntentConstant.LOGO_URL, logoUrl);
        bundle.putBoolean(IntentConstant.IS_SA, is_sa);
        switchToActivity(DeviceSettingActivity.class, bundle);
    }

    /**
     * 获取权限成功
     *
     * @param permissionBean
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean != null) {
            PermissionBean.PermissionsBean permissionsBean = permissionBean.getPermissions();
            boolean showSetting = false;
            boolean isControlDevice = false;

            if (permissionsBean != null) {
                showSetting = permissionsBean.isUpdate_device() || permissionsBean.isDelete_device();
                isControlDevice = permissionsBean.isSa_firmware_upgrade() || permissionsBean.isSa_software_upgrade();
            }
            ivSetting.setVisibility(showSetting ? View.VISIBLE : View.GONE);

            line.setVisibility(isControlDevice ? View.VISIBLE : View.GONE);
            rlSoftWareUpgrade.setVisibility(permissionsBean.isSa_software_upgrade() ? View.VISIBLE : View.GONE);
            rlFirmwareUpgrade.setVisibility(permissionsBean.isSa_firmware_upgrade() ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 获取详情成功
     *
     * @param deviceDetailBean
     */
    @Override
    public void getDeviceDetailSuccess(DeviceDetailBean deviceDetailBean) {
        if (deviceDetailBean != null) {
            DeviceDetailBean.DeviceInfoBean deviceInfoBean = deviceDetailBean.getDevice_info();
            if (deviceDetailBean != null) {
                DeviceDetailBean.DeviceInfoBean.LocationBean locationBean = deviceInfoBean.getLocation();
                name = deviceInfoBean.getName();
                tvName.setText(deviceInfoBean.getName());
                GlideUtil.load(deviceInfoBean.getLogo_url()).into(ivCover);
                if (locationBean != null) {
                    locationId = locationBean.getId();
                    raName = locationBean.getName();
                }
                DeviceDetailBean.DeviceInfoBean.PluginBean pluginBean = deviceInfoBean.getPlugin();
                if (pluginBean != null) {
                    if (!is_sa && !TextUtils.isEmpty(pluginBean.getUrl())) {
                        webView.loadUrl(pluginBean.getUrl().replace("%23", "#"));
                    }
                }
                DeviceDetailBean.DeviceInfoBean.PermissionsBean permissionsBean = deviceInfoBean.getPermissions();
                if (permissionsBean != null) {
                    ivSetting.setVisibility((permissionsBean.isUpdate_device() || permissionsBean.isDelete_device()) ? View.VISIBLE : View.GONE);
                }
            }
        }
    }


    /**
     * 获取详情成功 重构
     *
     * @param deviceDetailResponseEntity
     */
    @Override
    public void getDeviceDetailRestructureSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity) {
        if (deviceDetailResponseEntity != null) {
            DeviceDetailEntity deviceDetailEntity = deviceDetailResponseEntity.getDevice_info();
            if (deviceDetailEntity != null) {
                name = deviceDetailEntity.getName();
                tvName.setText(name);
                GlideUtil.load(deviceDetailEntity.getLogo_url()).into(ivCover);
                DeviceDetailEntity.AreaAndLocationBean locationBean = deviceDetailEntity.getLocation();
                if (locationBean != null) {
                    locationId = locationBean.getId();
                    raName = locationBean.getName();
                }
                DeviceDetailEntity.PluginBean pluginBean = deviceDetailEntity.getPlugin();
                // 如果是从被本地插件加载，则关闭270-274
                if (pluginBean != null) {
                    if (!is_sa && !TextUtils.isEmpty(pluginBean.getUrl())) {
                        webView.loadUrl(pluginBean.getUrl().replace("%23", "#"));
                    }
                }
                DeviceDetailEntity.PermissionsBean permissionsBean = deviceDetailEntity.getPermissions();
                if (permissionsBean != null) {
                    ivSetting.setVisibility((permissionsBean.isUpdate_device() || permissionsBean.isDelete_device()) ? View.VISIBLE : View.GONE);
                }
            }
        }
    }

    @Override
    public void getFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        if (!is_sa) {
            webView.loadUrl(plugin_url);
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
            UiUtil.postDelayed(() -> webView.loadUrl(url), 200);
            LogUtil.e(TAG + "shouldOverrideUrlLoading");
            return true;
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressbar.setProgress(newProgress);
            if (newProgress == 100) {
                progressbar.setVisibility(View.GONE);
            }
        }
    }

    class JsInterface {

        @SuppressLint("JavascriptInterface")
        @JavascriptInterface
        public void entry(String json) {
            JsBean jsBean = new Gson().fromJson(json, JsBean.class);

            switch (jsBean.getFunc()) {
                case JsMethodConstant.GET_SOCKET_ADDRESS: // 获取插件websocket地址
                    jsMethodConstant.getSocketAddress(jsBean);
                    break;

                case JsMethodConstant.SET_TITLE:  // 设置标题属性
                    JsBean.JsSonBean jsSonBean = jsBean.getParams();
                    if (jsSonBean != null) {
                        UiUtil.runInMainThread(() -> rlTitle.setVisibility(jsSonBean.isIsShow() ? View.VISIBLE : View.GONE));
                    }
                    break;
            }
        }
    }
}