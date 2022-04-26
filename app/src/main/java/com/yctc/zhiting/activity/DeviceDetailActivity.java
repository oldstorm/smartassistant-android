package com.yctc.zhiting.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.config.HttpBaseUrl;
import com.app.main.framework.dialog.LoadingDialog;
import com.app.main.framework.entity.ChannelEntity;
import com.app.main.framework.fileutil.BaseFileUtil;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.DownloadFailListener;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.imageutil.GlideUtil;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DeviceDetailContract;
import com.yctc.zhiting.activity.presenter.DeviceDetailPresenter;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.dialog.DeleteSADialog;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.JsBean;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.mine.DeleteSARequest;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.event.FinishDeviceDetailEvent;
import com.yctc.zhiting.event.RefreshHome;
import com.yctc.zhiting.event.RefreshHomeEvent;
import com.yctc.zhiting.request.AddHCRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.JsMethodConstant;
import com.yctc.zhiting.utils.ZipUtils;
import com.yctc.zhiting.websocket.WSocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.lizhangqu.coreprogress.ProgressUIListener;

import static com.yctc.zhiting.config.Constant.CurrentHome;
import static com.yctc.zhiting.config.Constant.devDetailList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 设备详情
 */
public class DeviceDetailActivity extends MVPBaseActivity<DeviceDetailContract.View, DeviceDetailPresenter> implements DeviceDetailContract.View {

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
    @BindView(R.id.tvDelete)
    TextView tvDelete;

    private int id;
    private int locationId;
    private boolean is_sa;
    private boolean isDeleteCloudDisk;
    private boolean isCreateCloudHome;
    private String name;
    private String logoUrl;
    private String plugin_url;
    private String raName;
    private String iid;
    private String pluginId;

    private DeviceMultipleBean mDeviceMultipleBean;
    private JsMethodConstant jsMethodConstant;
    private IdBean mIdBean;


    // 0. 从首页或房间详情来； 1. 从设置设备位置来  2. 子设备
    private int from;

    private String mSonControl;  // 子设备control
    private String mSonPluginId; // 子设备插件id
    private int mSonDeviceId; // 子设备id
    private boolean isDelay;
    private boolean showLoading = true;

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
        Constant.devDetailList.add(this);
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
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        jsMethodConstant = new JsMethodConstant(webView);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.getPermissions(CurrentHome.getUser_id());
        mPresenter.getUserInfo(CurrentHome.getUser_id());
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mDeviceMultipleBean = (DeviceMultipleBean) intent.getSerializableExtra(IntentConstant.BEAN);
        from = intent.getIntExtra(IntentConstant.FROM, 0);
        if (from == 2) { // 子设备
            id = intent.getIntExtra(IntentConstant.ID, 0);
            pluginId = intent.getStringExtra(IntentConstant.PLUGIN_ID);
            LogUtil.e("网关子设备id：" + id);
            plugin_url = intent.getStringExtra(IntentConstant.PLUGIN_PATH);
            llSA.setVisibility(View.GONE);
            llCommonDevice.setVisibility(View.VISIBLE);
            System.out.println("插件路径：" + plugin_url);
//            webView.loadUrl(plugin_url);

        } else {
            if (mDeviceMultipleBean != null) {
                id = mDeviceMultipleBean.getId();
                locationId = Constant.AREA_TYPE == 2 ? mDeviceMultipleBean.getDepartment_id() : mDeviceMultipleBean.getLocation_id();
                is_sa = mDeviceMultipleBean.isIs_sa();
                name = mDeviceMultipleBean.getName();
                logoUrl = mDeviceMultipleBean.getLogo_url();
                iid = mDeviceMultipleBean.getIid();
                pluginId = mDeviceMultipleBean.getPlugin_id();
//            plugin_url = mDeviceMultipleBean.getPlugin_url();
                plugin_url = intent.getStringExtra(IntentConstant.PLUGIN_PATH);
                raName = Constant.AREA_TYPE == 2 ? mDeviceMultipleBean.getDepartment_name() : mDeviceMultipleBean.getLocation_name();
                System.out.println("插件路径：" + plugin_url);
                if (is_sa) {
                    llSA.setVisibility(View.VISIBLE);
                    llCommonDevice.setVisibility(View.GONE);
                } else {
                    llSA.setVisibility(View.GONE);
                    llCommonDevice.setVisibility(View.VISIBLE);
//                    webView.loadUrl(plugin_url);
                }

                GlideUtil.load(logoUrl).into(ivCover);
                tvName.setText(name);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
        if (!is_sa) {
            if (isDelay) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(plugin_url);
                    }
                }, 500);
            } else {
                webView.loadUrl(plugin_url);
            }
//            webView.setWebViewClient(new MyWebViewClient());
//            webView.setWebChromeClient(new MyWebChromeClient());
            webView.loadUrl(plugin_url);
            isDelay = true;

        }
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

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if (from == 1) { // 如果是从设备位置过来
                switchToActivity(MainActivity.class);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
//            webView.setWebChromeClient(null);
//            webView.setWebViewClient(null);

            webView.clearCache(true);
        }
        webView.pauseTimers();
        webView.loadUrl("about:blank");
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
//            webView.clearCache(true);
//            webView.removeAllViews();
            Constant.devDetailList.remove(this);
            if (devDetailList.size() == 0) {
                webView.clearHistory();
                ViewGroup viewGroup = (ViewGroup) webView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView(webView);
                }
                webView.destroy();
            }
        }

        if (jsMethodConstant != null) {
            jsMethodConstant.release();
            jsMethodConstant.closeWS();
        }
    }

    /**
     * 删除设备
     */
    @OnClick(R.id.tvDelete)
    public void onClickDeleteDevice() {
        mPresenter.getExtensions();
    }

    @OnClick({R.id.rlSoftWareUpgrade, R.id.rlFirmWareUpgrade})
    public void onClick(View view) {
        if (view.getId() == R.id.rlSoftWareUpgrade) {
            bundle = new Bundle();
            bundle.putInt("type", 1);
            switchToActivity(SoftwareUpgradeActivity.class, bundle);
        } else if (view.getId() == R.id.rlFirmWareUpgrade) {
            bundle = new Bundle();
            bundle.putInt("type", 2);
            switchToActivity(SoftwareUpgradeActivity.class, bundle);
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
        bundle.putString(IntentConstant.IID, iid);
        bundle.putString(IntentConstant.PLUGIN_ID, pluginId);
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
                DeviceDetailBean.DeviceInfoBean.LocationBean locationBean = Constant.AREA_TYPE == 2 ? deviceInfoBean.getDepartment() : deviceInfoBean.getLocation();
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
                // 如果是从被本地插件加载，则关闭
//                if (pluginBean != null) {
//                    if (!is_sa && !TextUtils.isEmpty(pluginBean.getUrl())) {
//                        webView.loadUrl(pluginBean.getUrl().replace("%23", "#"));
//                    }
//                }
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

    @Override
    public void getUserInfoSuccess(MemberDetailBean data) {
        if (data != null) {
            tvDelete.setVisibility(data.isIs_owner() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void getExtensionsSuccess(List<String> list) {
        boolean isDisk = false;
        if (CollectionUtil.isNotEmpty(list)) {
            for (String name : list) {
                if (name.equalsIgnoreCase("wangpan")) {
                    isDisk = true;
                    break;
                }
            }
        }
        showDeleteSADialog(isDisk);
    }

    /**
     * @param isHasDisk
     */
    private void showDeleteSADialog(boolean isHasDisk) {
        DeleteSADialog deleteDialog = DeleteSADialog.newInstance(isHasDisk);
        deleteDialog.setConfirmListener((isCreateCloudHome, isDeleteCloudDisk) -> {
            this.isDeleteCloudDisk = isDeleteCloudDisk;
            this.isCreateCloudHome = isCreateCloudHome;
            if (isCreateCloudHome) {
                createHomeOrCompany();
            } else {
                deleteSA();
            }
            deleteDialog.dismiss();
        });
        deleteDialog.show(this);
    }

    /**
     * 创建家庭
     */
    private void createHomeOrCompany() {
        AddHCRequest request = new AddHCRequest(CurrentHome.getName(), CurrentHome.getArea_type());
        request.setLocation_names(new ArrayList<>());
        request.setDepartment_names(new ArrayList<>());
        mPresenter.addScHome(request);
    }

    /**
     * 删除SA
     */
    private void deleteSA() {
        DeleteSARequest request = new DeleteSARequest(isCreateCloudHome, isDeleteCloudDisk);
        if (mIdBean != null) {
            request.setCloud_area_id(String.valueOf(mIdBean.getId()));
            if (mIdBean.getCloud_sa_user_info() != null) {
                request.setCloud_access_token(mIdBean.getCloud_sa_user_info().getToken());
            }
        }
        HttpConfig.addHeader(HttpConfig.AREA_ID, String.valueOf(CurrentHome.getArea_id()));
        HttpConfig.addHeader(HttpConfig.TOKEN_KEY, CurrentHome.getSa_user_token());
        mPresenter.deleteSa(request);
    }

    @Override
    public void getExtensionsFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void deleteSaFailed(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void deleteSaSuccess(int remove_status) {
        //1 正在移除| 2 移除出错| 3 移除成功
        if (1 == remove_status) {
            ToastUtil.show(UiUtil.getString(R.string.home_removing_sa));
        } else if (2 == remove_status) {
            ToastUtil.show(UiUtil.getString(R.string.home_remove_sa_error));
        } else if (3 == remove_status) {
            ToastUtil.show(UiUtil.getString(R.string.home_remove_sa_success));
        }
        EventBus.getDefault().post(new RefreshHomeEvent());
        finish();
    }

    @Override
    public void createHomeOrCompanySuccess(IdBean idBean) {
        mIdBean = idBean;
        deleteSA();
    }

    @Override
    public void createHomeOrCompanyFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            LogUtil.e("开始加载js");
            if (showLoading) {
                showLoadingDialogInAct();
                showLoading = false;
            }
//            webView.loadUrl("javascript:" + Constant.professional_js);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            dismissLoadingDialogInAct();
            webView.clearHistory();
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
//            UiUtil.postDelayed(() -> webView.loadUrl(url), 200);
            webView.loadUrl(url);
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
            LogUtil.e("js参数：" + json);
            JsBean jsBean = new Gson().fromJson(json, JsBean.class);
            switch (jsBean.getFunc()) {
                case JsMethodConstant.GET_SOCKET_ADDRESS: // 获取插件websocket地址
                    jsMethodConstant.getSocketAddress(jsBean);
                    break;

                case JsMethodConstant.CONNECT_SOCKET: // 创建一个websocket连接
                    jsMethodConstant.connectSocket(jsBean);
                    break;

                case JsMethodConstant.SEND_SOCKET_MESSAGE:  //通过 WebSocket 连接发送数据
                    jsMethodConstant.sendSocketMessage(jsBean);
                    break;

                case JsMethodConstant.ON_SOCKET_OPEN:  // 监听 WebSocket 连接打开事件
                    jsMethodConstant.onSocketOpen(jsBean);
                    break;

                case JsMethodConstant.ON_SOCKET_MESSAGE:  // 监听 WebSocket 接受到服务器的消息事件
                    jsMethodConstant.onSocketMessage(jsBean);
                    break;

                case JsMethodConstant.ON_SOCKET_ERROR:  //监听 WebSocket 错误事件
                    jsMethodConstant.onSocketError(jsBean);
                    break;

                case JsMethodConstant.ON_SOCKET_CLOSE: // 监听 WebSocket 连接关闭事件
                    jsMethodConstant.onSocketClose(jsBean);
                    break;

                case JsMethodConstant.Close_Socket:  // 关闭 WebSocket 连接
                    jsMethodConstant.closeSocket(jsBean);
                    break;

                case JsMethodConstant.SET_TITLE:  // 设置标题属性
                    JsBean.JsSonBean jsSonBean = jsBean.getParams();
                    if (jsSonBean != null) {
                        Boolean isShow = jsSonBean.isIsShow();
                        UiUtil.runInMainThread(() -> rlTitle.setVisibility(isShow != null && isShow == true ? View.VISIBLE : View.GONE));
                    }
                    break;

                case JsMethodConstant.ROTOR_DEVICE: // 由app跳转子设备
                    JsBean.JsSonBean rotorDeviceJsonBean = jsBean.getParams();
                    if (rotorDeviceJsonBean != null) {
                        mSonControl = rotorDeviceJsonBean.getControl();
                        mSonPluginId = rotorDeviceJsonBean.getPlugin_id();
                        mSonDeviceId = (int) rotorDeviceJsonBean.getId();
                        LogUtil.e("网关子设备id：" + mSonDeviceId);
                        mPresenter.getPluginDetail(mSonPluginId);

                    }
                    break;

                case JsMethodConstant.GET_LOCAL_HOST: // 提供http端口

                    String localhost = HttpUrlConfig.baseSAUrl;
                    if (!CurrentHome.isSAEnvironment() && !HomeUtil.isInLAN) {
                        String tokenKey = SpUtil.get(SpConstant.AREA_ID);
                        String channelJson = SpUtil.get(tokenKey);
                        ChannelEntity channel = GsonConverter.getGson().fromJson(channelJson, ChannelEntity.class);
                        if (channel != null) {
                            localhost = HttpBaseUrl.HTTPS + "://" + channel.getHost();
                        }
                    }
                    jsMethodConstant.getLocalhost(jsBean, localhost);
                    break;
            }
        }
    }

    /**
     * 插件详情成功
     *
     * @param pluginDetailBean
     */
    @Override
    public void getPluginDetailSuccess(PluginDetailBean pluginDetailBean) {
        if (pluginDetailBean != null) {
            PluginsBean pluginsBean = pluginDetailBean.getPlugin();
            if (pluginsBean != null) {
                String pluginId = pluginsBean.getId();
                String pluginFilePath = "";
                pluginFilePath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + Constant.PLUGINS_PATH + mSonPluginId;
                String downloadUrl = pluginsBean.getDownload_url();
                String cachePluginJson = SpUtil.get(pluginId);
                PluginsBean cachePlugin = GsonConverter.getGson().fromJson(cachePluginJson, PluginsBean.class);
                String cacheVersion = "";
                if (cachePlugin != null) {
                    cacheVersion = cachePlugin.getVersion();
                }
                String version = pluginsBean.getVersion();
                if (mDeviceMultipleBean != null && BaseFileUtil.isFileExist(pluginFilePath) &&
                        !TextUtils.isEmpty(cacheVersion) && !TextUtils.isEmpty(version) && cacheVersion.equals(version)) {  // 如果缓存存在且版本相同
                    String urlPath = Constant.FILE_HEAD + pluginFilePath + Constant.SEPARATOR + mSonControl;
                    toDeviceDetail(urlPath);
                } else {
                    if (!TextUtils.isEmpty(downloadUrl)) {
                        String suffix = downloadUrl.substring(downloadUrl.lastIndexOf(".") + 1);
                        BaseFileUtil.deleteFolderFile(pluginFilePath, true);
                        String fileZipPath = pluginFilePath + "." + suffix;
                        File file = new File(fileZipPath);
                        BaseFileUtil.deleteFile(file);
                        List<Header> headers = new ArrayList<>();
                        headers.add(new Header("Accept-Encoding", "identity"));
                        headers.add(new Header(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken()));
                        String path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + Constant.PLUGINS_PATH;
                        String finalPath = path;
                        String finalPluginFilePath = pluginFilePath;
                        String tip = UiUtil.getString(R.string.home_download_plugin_package_fail);
                        HTTPCaller.getInstance().downloadFile(downloadUrl, path, pluginId, headers.toArray(new Header[headers.size()]), UiUtil.getString(R.string.home_download_plugin_package_fail), new ProgressUIListener() {
                            @Override
                            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                                LogUtil.e("进度：" + percent);
                                if (percent == 1) {
                                    LogUtil.e("下载完成");
                                    try {
                                        ZipUtils.decompressFile(new File(fileZipPath), finalPath, true);
                                        String pluginsBeanToJson = GsonConverter.getGson().toJson(pluginsBean);
                                        SpUtil.put(pluginId, pluginsBeanToJson);
                                        String urlPath = Constant.FILE_HEAD + finalPluginFilePath + mSonControl;
                                        toDeviceDetail(urlPath);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        dismissLoadingDialogInAct();
                                        ToastUtil.show(UiUtil.getString(R.string.unzip_file_exception));
                                    }
                                }
                            }
                        }, new DownloadFailListener() {
                            @Override
                            public void downloadFailed() {
                                dismissLoadingDialogInAct();
                                UiUtil.runInMainThread(() -> ToastUtil.show(tip));
                            }
                        });
                    } else {
                        dismissLoadingDialogInAct();
                        ToastUtil.show(UiUtil.getString(R.string.home_download_plugin_package_fail));
                    }
                }
            }
        }
    }

    private void toDeviceDetail(String urlPath) {
        Bundle bundle = new Bundle();
        bundle.putString(IntentConstant.PLUGIN_PATH, urlPath);
        bundle.putInt(IntentConstant.FROM, 2);
        bundle.putInt(IntentConstant.ID, mSonDeviceId);
        bundle.putString(IntentConstant.PLUGIN_ID, mSonPluginId);
        LogUtil.e("网关子设备id：" + mSonDeviceId);
        switchToActivity(DeviceDetailActivity.class, bundle);
    }

    /**
     * 插件详情失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getPluginDetailFail(int errorCode, String msg) {
        dismissLoadingDialogInAct();
        ToastUtil.show(msg);
    }
}