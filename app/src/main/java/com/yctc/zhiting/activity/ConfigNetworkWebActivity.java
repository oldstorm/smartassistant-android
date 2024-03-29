package com.yctc.zhiting.activity;

import static com.yctc.zhiting.config.Constant.CurrentHome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.NetworkUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.config.HttpBaseUrl;
import com.app.main.framework.entity.ChannelEntity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.espressif.provisioning.DeviceConnectionEvent;
import com.espressif.provisioning.ESPConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.ConfigNetworkWebContract;
import com.yctc.zhiting.activity.presenter.ConfigNetworkWebPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.entity.ConfigServerResultBean;
import com.yctc.zhiting.entity.GetDeviceInfoBean;
import com.yctc.zhiting.entity.JsBean;
import com.yctc.zhiting.entity.ScanDeviceByUDPBean;
import com.yctc.zhiting.entity.ServerConfigBean;
import com.yctc.zhiting.entity.home.AccessTokenBean;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.DeviceTypeDeviceBean;
import com.yctc.zhiting.entity.ws_request.WSDeviceRequest;
import com.yctc.zhiting.entity.ws_request.WSRequest;
import com.yctc.zhiting.entity.ws_request.WSConstant;
import com.yctc.zhiting.entity.ws_response.WSDeviceResponseBean;
import com.yctc.zhiting.entity.ws_response.WSBaseResponseBean;
import com.yctc.zhiting.entity.ws_response.WSDeviceBean;
import com.yctc.zhiting.receiver.WifiReceiver;
import com.yctc.zhiting.utils.AESUtil;
import com.yctc.zhiting.utils.BluetoothUtil;
import com.yctc.zhiting.utils.ByteConstant;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.JsMethodConstant;
import com.yctc.zhiting.utils.Md5Util;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.utils.WebViewInitUtil;
import com.yctc.zhiting.utils.confignetwork.BlufiUtil;
import com.yctc.zhiting.utils.confignetwork.ConfigNetworkCallback;
import com.yctc.zhiting.utils.confignetwork.WifiUtil;
import com.yctc.zhiting.utils.statusbarutil.StatusBarUtil;
import com.yctc.zhiting.utils.udp.ByteUtil;
import com.yctc.zhiting.utils.udp.UDPSocket;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import blufi.espressif.BlufiCallback;
import blufi.espressif.BlufiClient;
import blufi.espressif.response.BlufiScanResult;
import blufi.espressif.response.BlufiStatusResponse;
import blufi.espressif.response.BlufiVersionResponse;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.WebSocket;

/**
 * 配网
 */
public class ConfigNetworkWebActivity extends MVPBaseActivity<ConfigNetworkWebContract.View, ConfigNetworkWebPresenter> implements ConfigNetworkWebContract.View {

    private static final int REQUEST_PERMISSION = 0x01;

    @BindView(R.id.rlTitle)
    RelativeLayout rlTitle;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    private String webUrl = "http://192.168.22.109/doc/test.html";
    private DeviceTypeDeviceBean mDeviceTypeDeviceBean;

    private BlufiUtil blufiUtil;
    private WifiUtil mWifiUtil;
    private BluetoothScanCallback mBluetoothScanCallback;
    private List<DeviceBean> mScanLists = new ArrayList<>();
    private Set<String> blueDeviceAdded = new HashSet<>(); // 存储已存储的蓝牙设备
    private Set<String> updAddressSet; // 存储已获取upd设备
    private UDPSocket udpSocket;
    private String mDeviceId;
    private String mAccessToken;
    private ConcurrentHashMap<String, ScanDeviceByUDPBean> scanMap = new ConcurrentHashMap<>();  // 存储udp扫描的设备信息
    private JsMethodConstant mJsMethodConstant;
    private String registerDeviceCallbackID;
    private String type; // 设备类型
    private long sendId = 0; // 发送upd id
    private CountDownTimer mCountDownTimer;

    private IWebSocketListener mIWebSocketListener;

    private ConcurrentHashMap<String, WSRequest> requestHashMap = new ConcurrentHashMap<>();

    private CountDownTimer mAddDeviceCountDownTimer;  // 添加设备扫描倒计时

    /**
     * Wifi 状态接收器
     */
    private final WifiReceiver mWifiReceiver = new WifiReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {

                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (JsMethodConstant.dealConnectHotspot) {
                        if (mWifiUtil != null && JsMethodConstant.mHotspotName != null) {
                            LogUtil.e("热点名称：" + JsMethodConstant.mHotspotName);
                            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                            String wifiName = wifiManager.getConnectionInfo().getSSID();
                            wifiName = wifiName.substring(1, wifiName.length() - 1);
                            LogUtil.e("wifi名称：" + wifiName);
                            boolean result = wifiName.equals(JsMethodConstant.mHotspotName);
                            if (result) {
                                mDeviceId = wifiManager.getConnectionInfo().getBSSID().replace(":", "");
                                if (mJsMethodConstant != null)
                                    mJsMethodConstant.connectDeviceHotspotResult(JsMethodConstant.SUCCESS, UiUtil.getString(R.string.success));
                            } else {
                                if (mJsMethodConstant != null)
                                    mJsMethodConstant.connectDeviceHotspotResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.failed));
                            }
                        }
                        JsMethodConstant.dealConnectHotspot = false;
                    }
                } else if (info.getState().equals(NetworkInfo.State.UNKNOWN)) {
                    if (JsMethodConstant.dealConnectHotspot) {
                        mJsMethodConstant.connectDeviceHotspotResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.failed));
                        JsMethodConstant.dealConnectHotspot = false;
                    }
                }
            }
        }
    };

    /**
     * 获取配网accessToken
     */
    private void getAccessToken() {
        if (UserUtils.isLogin() && !CurrentHome.isIs_bind_sa()) {
            UiUtil.postDelayed(() -> mPresenter.getAccessToken(), 500);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_config_network_web;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    webView.removeAllViews();
                    webView.clearCache(true);
                    webView.clearHistory();
                }
            }, 50);
            webView.loadUrl(webUrl);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        registerWifiReceiver();
        initWebSocket();
        StatusBarUtil.setStatusBarDarkTheme(this, false);
        blufiUtil = new BlufiUtil(getApplicationContext());
        mBluetoothScanCallback = new BluetoothScanCallback();
        updAddressSet = new HashSet<>();
        webView.clearHistory();
        webView.clearCache(true);
        WebViewInitUtil webViewInitUtil = new WebViewInitUtil(this);
        webViewInitUtil.initWebView(webView);
        webViewInitUtil.setProgressBar(progressbar);
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + "; " + Constant.ZHITING_USER_AGENT);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.addJavascriptInterface(new JsInterface(), Constant.ZHITING_APP);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        mWifiUtil = new WifiUtil(this);
        mJsMethodConstant = new JsMethodConstant(webView, blufiUtil, mWifiUtil);
        initBlueToothScan();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        webUrl = intent.getStringExtra(IntentConstant.PLUGIN_PATH);
        type = intent.getStringExtra(IntentConstant.TYPE);
        mDeviceTypeDeviceBean = (DeviceTypeDeviceBean) intent.getSerializableExtra(IntentConstant.BEAN);
        UiUtil.postDelayed(() -> webView.loadUrl(webUrl), 200);
        initCountDownTimer();
    }

    /**
     * WebSocket初始化、添加监听
     */
    private void initWebSocket() {
        mIWebSocketListener = new IWebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if (TextUtils.isEmpty(text)) return;
                LogUtil.e("添加设备结果：" + text);
                WSBaseResponseBean<WSDeviceResponseBean> responseBean = GsonConverter.getGson().fromJson(text, new TypeToken<WSBaseResponseBean<WSDeviceResponseBean>>() {
                }.getType());
                if (responseBean != null && requestHashMap.containsKey(String.valueOf(responseBean.getId()))) {
                    if (responseBean.isSuccess()) {
                        WSDeviceResponseBean addDeviceResponseBean = responseBean.getData();
                        if (addDeviceResponseBean != null) {
                            WSDeviceBean deviceBean = addDeviceResponseBean.getDevice();
                            if (deviceBean != null) {
                                String addSuccess = registerDeviceJsCallback(0, "success");
                                mJsMethodConstant.runOnMainUi(addSuccess);
                                bundle = new Bundle();
                                bundle.putInt(IntentConstant.ID, deviceBean.getId());
                                bundle.putString(IntentConstant.PLUGIN_ID, deviceBean.getPlugin_id());
                                bundle.putString(IntentConstant.CONTROL, deviceBean.getControl());
                                bundle.putString(IntentConstant.NAME, mDeviceTypeDeviceBean.getName());
                                switchToActivity(SetDevicePositionActivity.class, bundle);
                                mAddDeviceCountDownTimer.cancel();
                                finish();
                            }
                        }
                    } else {
                        addDeviceFail(-1, "添加设备失败");
                    }
                }
            }
        };
        WSocketManager.getInstance().addWebSocketListener(mIWebSocketListener);
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    /**
     * 计时
     */
    private void initCountDownTimer() {
        mCountDownTimer = new CountDownTimer(10_000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.e("initCountDownTimer=" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                LogUtil.e("initCountDownTimer=onFinish=");
                if (udpSocket != null && udpSocket.isRunning()) {
                    udpSocket.stopUDPSocket();
                    scanMap.clear();
                    updAddressSet.clear();
                    addDeviceFail(-1, "device not found");
                }

            }
        };

        mAddDeviceCountDownTimer = new CountDownTimer(20_000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.e("mAddDeviceCountDownTimer=" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                LogUtil.e("mAddDeviceCountDownTimer=onFinish=");
                addDeviceFail(-1, "device not response");
            }
        };
    }

    /**
     * upd发现设备
     */
    private void initUDPScan() {
        if (udpSocket == null) {
            udpSocket = new UDPSocket(Constant.FIND_DEVICE_URL, Constant.FIND_DEVICE_PORT,
                    new UDPSocket.OnReceiveCallback() {
                        @Override
                        public void onReceiveByteData(String address, int port, byte[] data, int length) {
                            scanDeviceSuccessByUDP(address, port, data, length);

                        }

                        @Override
                        public void onReceive(String msg) {

                        }
                    });
        }
        udpSocket.startUDPSocket();
        udpSocket.sendMessage(ByteConstant.SEND_HELLO_DATA, Constant.FIND_DEVICE_URL);
        mCountDownTimer.start();
    }

    /**
     * 处理udp发现的设备
     *
     * @param address
     * @param port
     * @param data
     * @param length
     */
    private void scanDeviceSuccessByUDP(String address, int port, byte[] data, int length) {
        byte[] deviceIdData = Arrays.copyOfRange(data, 6, 12);  // 设备id
        try {
            String deviceId = ByteUtil.bytesToHex(deviceIdData);
            if (scanMap.containsKey(deviceId)) {  // 已经获取到hello数据包信息
                ScanDeviceByUDPBean sdb = scanMap.get(deviceId);
                String token = sdb.getToken();
                byte[] dealData = Arrays.copyOfRange(data, 32, length);
                if (TextUtils.isEmpty(token)) { // 没有获取过token信息，处理获取token信息，并发送获取设备信息
                    String key = sdb.getPassword();
                    String decryptKeyMD5 = Md5Util.getMD5(key);
                    byte[] decryptKeyDta = ByteUtil.md5Str2Byte(decryptKeyMD5);
                    byte[] ivData = ByteUtil.byteMergerAll(decryptKeyDta, key.getBytes());
                    byte[] ivEncryptedData = Md5Util.getMD5(ivData);
                    String tokenFromServer = AESUtil.decryptAES(dealData, decryptKeyDta, ivEncryptedData, AESUtil.PKCS7, true);
                    if (!TextUtils.isEmpty(tokenFromServer)) {
                        sdb.setToken(tokenFromServer);
                        sdb.setId(sendId);
                        String deviceStr = "{\"method\":\"get_prop.info\",\"params\":[],\"id\":" + sendId + "}";  // 获取设备信息体
                        sendId++;
                        byte[] bodyData = AESUtil.encryptAES(deviceStr.getBytes(), tokenFromServer, AESUtil.PKCS7); // 获取设备信息体转字节加密
                        int len = bodyData.length + 32;  // 包长
                        byte[] lenData = ByteUtil.intToByte2(len);  // 包长用占两位字节
                        byte[] headData = ByteConstant.GET_DEV_INFO_HEAD_Data; // 包头固定
                        byte[] preData = ByteConstant.GET_DEV_INFO_PRE_Data; // 预留固定
                        byte[] serData = ByteConstant.SER_DATA; // 序列号固定
                        byte[] tokenData = sdb.getPassword().getBytes();  // 之前获取设备信息时生成的16位随机密码
                        byte[] getDeviceInfoData = ByteUtil.byteMergerAll(headData, lenData, preData, deviceIdData, serData, tokenData, bodyData); //  拼接获取设备信息包
                        LogUtil.e(address + "获取设备信息发送数据：" + Arrays.toString(getDeviceInfoData));
                        udpSocket.sendMessage(getDeviceInfoData, address);
                    }
                } else { // 获取过token信息，处理获取到的设备信息
                    GetDeviceInfoBean deviceInfoBean = sdb.getDeviceInfoBean();
                    byte[] decryptDeviceData = Md5Util.getMD5(ByteUtil.md5Str2Byte(token));
                    byte[] ivDeviceData = ByteUtil.byteMergerAll(decryptDeviceData, ByteUtil.md5Str2Byte(token));
                    byte[] ivEncryptedDeviceData = Md5Util.getMD5(ivDeviceData);
                    LogUtil.e("设备信息字节：" + Arrays.toString(dealData));
                    String infoJson = AESUtil.decryptAES(dealData, decryptDeviceData, ivEncryptedDeviceData, AESUtil.PKCS7, false);
                    LogUtil.e("设备信息：" + infoJson);
                    GetDeviceInfoBean getDeviceInfoBean = new Gson().fromJson(infoJson, GetDeviceInfoBean.class);
                    if (sdb.getServerConfigBean() == null) {  // 还没发送设置服务器配置，发送
                        if (CurrentHome != null && mDeviceId != null && sdb.getDeviceId().equalsIgnoreCase(mDeviceId)) {
                            ServerConfigBean serverConfigBean = new ServerConfigBean(HttpBaseUrl.baseSCHost, Constant.FIND_DEVICE_PORT, mAccessToken, String.valueOf(CurrentHome.getId()));
                            if (mDeviceTypeDeviceBean != null) {
                                String protocol = mDeviceTypeDeviceBean.getProtocol();
                                if (protocol != null && protocol.equals(Constant.MQTT)) {
                                    serverConfigBean.setMode(Constant.CLOUD);
                                    serverConfigBean.setMqtt_server(HttpBaseUrl.baseSCHost + Constant.CONFIG_SERVER_PORT);
                                    serverConfigBean.setMqtt_password("");
                                }
                            }
                            sdb.setServerConfigBean(serverConfigBean);
                            String param = GsonConverter.getGson().toJson(serverConfigBean);
                            sdb.setId(sendId);
                            String sendConfig = "{\"method\":\"set_prop.server\",\"params\":" + param + ",\"id\":" + sendId + "}"; // 配置服务器
                            sendId++;
                            LogUtil.e("发送配置服务器：" + sendConfig);
                            byte[] bodyData = AESUtil.encryptAES(sendConfig.getBytes(), sdb.getToken(), AESUtil.PKCS7); // 获取设备信息体转字节加密
                            int len = bodyData.length + 32;  // 包长
                            byte[] lenData = ByteUtil.intToByte2(len);  // 包长用占两位字节
                            byte[] headData = ByteConstant.GET_DEV_INFO_HEAD_Data; // 包头固定
                            byte[] preData = ByteConstant.GET_DEV_INFO_PRE_Data; // 预留固定
                            byte[] serData = ByteConstant.SER_DATA; // 序列号固定
                            byte[] tokenData = sdb.getPassword().getBytes();  // 之前获取设备信息时生成的16位随机密码
                            byte[] configServerData = ByteUtil.byteMergerAll(headData, lenData, preData, deviceIdData, serData, tokenData, bodyData); //  拼接获取设备信息包
                            LogUtil.e(address + "获取设备信息发送数据：" + Arrays.toString(configServerData));
                            if (mCountDownTimer != null) {
                                mCountDownTimer.cancel();
                            }
                            udpSocket.sendMessage(configServerData, address);
                        }
                    } else {  // 已经发送过设置服务器配置，处理设置服务器配置结果
                        byte[] decryptConfigServerData = Md5Util.getMD5(ByteUtil.md5Str2Byte(token));
                        byte[] ivConfigServerData = ByteUtil.byteMergerAll(decryptDeviceData, ByteUtil.md5Str2Byte(token));
                        byte[] ivEncryptedConfigServerData = Md5Util.getMD5(ivConfigServerData);
                        LogUtil.e("设置服务器字节：" + Arrays.toString(dealData));
                        String configServerJson = AESUtil.decryptAES(dealData, decryptConfigServerData, ivEncryptedConfigServerData, AESUtil.PKCS7, false);
                        LogUtil.e("设置服务器结果：" + configServerJson);
                        ConfigServerResultBean configServerResultBean = new Gson().fromJson(infoJson, ConfigServerResultBean.class);
                        if (configServerResultBean != null && configServerResultBean.getId() == sdb.getId()) { // 处理结果
                            String sw_ver = "";
                            if (getDeviceInfoBean != null) {
                                GetDeviceInfoBean.ResultBean resultBean = getDeviceInfoBean.getResult();
                                if (resultBean != null)
                                    sw_ver = resultBean.getSw_ver();
                            }
                            addDevice(sw_ver);
                        }
                    }
                }
            } else {  // 获取到hello数据包信息
                ScanDeviceByUDPBean scanDeviceByUDPBean = new ScanDeviceByUDPBean(address, port, deviceId);
                String password = StringUtil.getUUid().substring(0, 16);
                scanDeviceByUDPBean.setPassword(password);
                scanMap.put(deviceId, scanDeviceByUDPBean);
                getDeviceToken(address, data, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String udpFail = registerDeviceJsCallback(1, "");
            mJsMethodConstant.runOnMainUi(udpFail);
        }
    }

    /**
     * 添加设备
     */
    private void addDevice(String sw_ver) {
        DeviceBean deviceBean = new DeviceBean();
        if (TextUtils.isEmpty(mDeviceId)) {
            addDeviceFail(-1, "Device Id is empty");
        }
        deviceBean.setIdentity(mDeviceId.toLowerCase());
        deviceBean.setModel(mDeviceTypeDeviceBean.getModel());
        deviceBean.setType(type);
        if (!TextUtils.isEmpty(sw_ver)) {
            deviceBean.setSwVersion(sw_ver);
        }
        deviceBean.setManufacturer(mDeviceTypeDeviceBean.getManufacturer());
        deviceBean.setPluginId(mDeviceTypeDeviceBean.getPlugin_id());
        if (!NetworkUtil.isNetworkAvailable()) {
            addDeviceFail(-1, "Network unavailable");
        } else {
//            mPresenter.addDevice(deviceBean);
            Constant.mSendId = Constant.mSendId + 1;
            WSRequest<WSDeviceRequest> wsRequest = new WSRequest<>();
            wsRequest.setId(Constant.mSendId);
            wsRequest.setDomain(mDeviceTypeDeviceBean.getPlugin_id());
            wsRequest.setService(WSConstant.SERVICE_CONNECT);
            WSDeviceRequest wsAddDeviceRequest = new WSDeviceRequest(mDeviceId.toLowerCase());
            wsRequest.setData(wsAddDeviceRequest);
            String deviceJson = GsonConverter.getGson().toJson(wsRequest);
            LogUtil.e("添加设备发送的数据：" + deviceJson);
            requestHashMap.put(String.valueOf(Constant.mSendId), wsRequest);
            mAddDeviceCountDownTimer.start();
            WSocketManager.getInstance().sendMessage(deviceJson);
        }
    }

    /**
     * 获取设备token
     *
     * @param address
     * @param data
     */
    private void getDeviceToken(String address, byte[] data, String password) {
        if (!updAddressSet.contains(address)) {
            updAddressSet.add(address);
            byte[] tokenHeadData = ByteConstant.TOKEN_HEAD_DATA; // 包头，包长，预留字节固定
            byte[] deviceIdData = Arrays.copyOfRange(data, 6, 12);  // 设备id
            byte[] passwordData = password.getBytes();  // 密码
            byte[] serData = ByteConstant.SER_DATA; // 序列号 固定
            byte[] tokenData = ByteUtil.byteMergerAll(tokenHeadData, deviceIdData, serData, passwordData);  // 拼接获取token数据包
            udpSocket.sendMessage(tokenData, address);
        }
    }

    /**
     * Wifi 状态监听注册
     */
    private void registerWifiReceiver() {
        if (mWifiReceiver == null) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mWifiReceiver, filter);
    }

    /**
     * Wifi 状态监听注销
     */
    public void unRegisterWifiReceiver() {
        if (mWifiReceiver == null) return;
        unregisterReceiver(mWifiReceiver);
    }

    /**
     * 定位权限
     */
    private void checkLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
    }

    /**
     * 检查蓝牙
     */
    private void initBlueToothScan() {
        if (BluetoothUtil.hasBlueTooth()) {
            if (BluetoothUtil.isEnabled()) {
                checkLocationPermission();
            } else {
                String title = getResources().getString(R.string.home_blue_tooth_disabled);
                String tip = getResources().getString(R.string.home_guide_user_to_open_bluetooth);
                CenterAlertDialog alertDialog = CenterAlertDialog.newInstance(
                        title, tip, getResources().getString(R.string.home_cancel),
                        getResources().getString(R.string.home_setting), false);

                alertDialog.setConfirmListener(del -> {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    alertDialog.dismiss();
                });
                alertDialog.show(this);
            }
        }
    }

    /**
     * 扫描蓝牙设备
     */
    private void scanBluetoothDevice() {
        BluetoothUtil.startScanBluetooth(mBluetoothScanCallback);
    }

    /**
     * 权限结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int size = permissions.length;
        for (int i = 0; i < size; ++i) {
            String permission = permissions[i];
            int grant = grantResults[i];

            if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grant == PackageManager.PERMISSION_GRANTED) {
                    scanBluetoothDevice();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearCache(true);
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
        if (udpSocket != null)
            udpSocket.stopUDPSocket();
        BluetoothUtil.stopScanBluetooth(mBluetoothScanCallback);
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        if (mJsMethodConstant != null)
            mJsMethodConstant.release();
        unRegisterWifiReceiver();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        if (udpSocket != null) {
            udpSocket.stopUDPSocket();
        }

        WSocketManager.getInstance().removeWebSocketListener(mIWebSocketListener);
    }

    /**
     * 获取设备access_token成功
     *
     * @param accessTokenBean
     */
    @Override
    public void getAccessTokenSuccess(AccessTokenBean accessTokenBean) {
        if (accessTokenBean != null) {
            mAccessToken = accessTokenBean.getAccess_token();
            initUDPScan();
        }
    }

    /**
     * 获取设备access_token失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getAccessTokenFail(int errorCode, String msg) {
        String accessTokenFail = registerDeviceJsCallback(1, msg);
        mJsMethodConstant.runOnMainUi(accessTokenFail);
    }

    /**
     * 添加设备成功
     *
     * @param data
     */
    @Override
    public void addDeviceSuccess(AddDeviceResponseBean data) {
        String addSuccess = registerDeviceJsCallback(0, "success");
        mJsMethodConstant.runOnMainUi(addSuccess);
        Bundle bundle = new Bundle();
        bundle.putInt(IntentConstant.ID, data.getDevice_id());
        bundle.putString(IntentConstant.NAME, mDeviceTypeDeviceBean.getName());
        switchToActivity(SetDevicePositionActivity.class, bundle);
        finish();
    }

    /**
     * 添加设备失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void addDeviceFail(int errorCode, String msg) {
        String accessTokenFail = registerDeviceJsCallback(1, msg);
        mJsMethodConstant.runOnMainUi(accessTokenFail);
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showLoadingDialogInAct();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
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
            UiUtil.postDelayed(() -> webView.loadUrl(webUrl), 200);
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

    @OnClick(R.id.ivBack)
    void back() {
        onBackPressed();
    }

    /**
     * 连接设备结果
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeviceConnectionEvent event) {
        Log.d(TAG, "On Device Prov Event RECEIVED : " + event.getEventType());
        switch (event.getEventType()) {
            case ESPConstants.EVENT_DEVICE_CONNECTED:
                mJsMethodConstant.connectDeviceByHotspotResult(JsMethodConstant.SUCCESS, UiUtil.getString(R.string.success));
                break;

            case ESPConstants.EVENT_DEVICE_CONNECTION_FAILED:
                mJsMethodConstant.connectDeviceByHotspotResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.failed));
                break;
        }
    }

    class JsInterface {

        @SuppressLint("JavascriptInterface")
        @JavascriptInterface
        public void entry(String json) {
            JsBean jsBean = new Gson().fromJson(json, JsBean.class);
            LogUtil.e("js交互："+json);
            switch (jsBean.getFunc()) {
                case JsMethodConstant.CONNECT_DEVICE_BY_BLUETOOTH: // 通过蓝牙连接设备
                    mJsMethodConstant.connectDeviceByBluetooth(jsBean, new BlufiCallbackMain());
                    break;

                case JsMethodConstant.CONNECT_NETWORK_BY_BLUETOOTH: // 通过蓝牙发送配网信息
                    mJsMethodConstant.connectNetworkByBluetooth(jsBean);
                    break;

                case JsMethodConstant.CONNECT_DEVICE_HOST_SPOT:  //连接设备热点
                    mJsMethodConstant.connectDeviceHotspot(jsBean);
                    break;

                case JsMethodConstant.CREATE_DEVICE_BY_HOTSPOT:  //通过设备热点创建设备
                    mJsMethodConstant.createDeviceByHotspot(jsBean);
                    break;

                case JsMethodConstant.CONNECT_DEVICE_BY_HOTSPOT:  //通过热点连接设备
                    JsMethodConstant.connectDeviceByHotspot(jsBean);
                    break;

                case JsMethodConstant.CONNECT_NETWORK_BY_HOTSPOT:  //通过热点发送配网信息
                    mJsMethodConstant.connectNetworkByHotspot(jsBean, new ConfigNetworkCallback() {
                        @Override
                        public void onSuccess() {
                            mJsMethodConstant.connectNetworkByHotspotResult(JsMethodConstant.SUCCESS, UiUtil.getString(R.string.success));
                        }

                        @Override
                        public void onFailed(int errorCode, Exception e) {
                            mJsMethodConstant.connectNetworkByHotspotResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.failed));
                        }

                    });
                    break;

                case JsMethodConstant.REGISTER_DEVICE_BY_HOTSPOT:
                case JsMethodConstant.REGISTER_DEVICE_BY_BLUETOOTH:
                    // 通过热点发送设备注册
                    registerDeviceCallbackID = jsBean.getCallbackID();
                    LogUtil.logE("registerDeviceCallbackID", registerDeviceCallbackID);
                    if (NetworkUtil.isNetworkAvailable()) {
                        if (HomeUtil.isBindSA()) {
                            LogUtil.e("isSAEnvironment", "在sa");
                            addDevice("");
                        } else {
                            LogUtil.e("isSAEnvironment", "在云");
                            getAccessToken();
                        }
                    } else {
                        addDeviceFail(-1, "Network unavailable");
                    }
                    break;

                case JsMethodConstant.GET_DEVICE_INFO:  // // 获取配网的设备信息
                    String deviceJson = GsonConverter.getGson().toJson(mDeviceTypeDeviceBean);
                    String backJson = "'" + deviceJson + "'";
                    mJsMethodConstant.getDeviceInfo(jsBean, backJson);
                    break;

                case JsMethodConstant.GET_CONNECT_WIFI:  // // 获取设备当前连接wifi
                    mJsMethodConstant.getConnectWifi(jsBean);
                    break;

                case JsMethodConstant.TO_SYSTEM_WLAN:  // // 去系统设置wlan页
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;

                case JsMethodConstant.SET_TITLE:  // 设置标题属性
                    JsBean.JsSonBean jsSonBean = jsBean.getParams();
                    if (jsSonBean != null) {
                        UiUtil.runInMainThread(() -> {
                            tvTitle.setText(jsSonBean.getTitle());
                            Boolean isShow = jsSonBean.isIsShow();
                            rlTitle.setVisibility(isShow != null && isShow == true ? View.VISIBLE : View.GONE);
                        });
                    }
                    break;

                case JsMethodConstant.GET_SYSTEM_WIFI_LIST: //获取wifi列表页
                    mJsMethodConstant.getSystemWifiList(jsBean);
                    break;

                case JsMethodConstant.ROTOR_DEVICE_SET:
                    JsBean.JsSonBean deviceIdBean = jsBean.getParams();
                    if (deviceIdBean != null) {
                        int deviceId = (int) deviceIdBean.getId();
                        bundle = new Bundle();
                        bundle.putInt(IntentConstant.ID, deviceId);
                        switchToActivity(SetDevicePositionActivity.class, bundle);
                        finish();
                    }
                    break;

                case JsMethodConstant.GET_LOCAL_HOST: // 提供http端口

                    String localhost = HttpUrlConfig.baseSAUrl;
                    if(!CurrentHome.isSAEnvironment() && !HomeUtil.isInLAN) {
                        String tokenKey = SpUtil.get(SpConstant.AREA_ID);
                        String channelJson = SpUtil.get(tokenKey);
                        LogUtil.e("临时通道Json："+channelJson);
                        ChannelEntity channel = GsonConverter.getGson().fromJson(channelJson, ChannelEntity.class);
                        if (channel != null) {
                            localhost = HttpBaseUrl.HTTPS + "://" + channel.getHost();
                        }
                    }
                    mJsMethodConstant.getLocalhost(jsBean, localhost);
                    break;

                case JsMethodConstant.GET_SOCKET_ADDRESS: // 获取插件websocket地址
                    mJsMethodConstant.getSocketAddress(jsBean);
                    break;

                case JsMethodConstant.CONNECT_SOCKET: // 创建一个websocket连接
                    mJsMethodConstant.connectSocket(jsBean);
                    break;

                case JsMethodConstant.SEND_SOCKET_MESSAGE:  //通过 WebSocket 连接发送数据
                    mJsMethodConstant.sendSocketMessage(jsBean);
                    break;

                case JsMethodConstant.ON_SOCKET_OPEN:  // 监听 WebSocket 连接打开事件
                    mJsMethodConstant.onSocketOpen(jsBean);
                    break;

                case JsMethodConstant.ON_SOCKET_MESSAGE:  // 监听 WebSocket 接受到服务器的消息事件
                    mJsMethodConstant.onSocketMessage(jsBean);
                    break;

                case JsMethodConstant.ON_SOCKET_ERROR:  //监听 WebSocket 错误事件
                    mJsMethodConstant.onSocketError(jsBean);
                    break;

                case JsMethodConstant.ON_SOCKET_CLOSE: // 监听 WebSocket 连接关闭事件
                    mJsMethodConstant.onSocketClose(jsBean);
                    break;

                case JsMethodConstant.Close_Socket:  // 关闭 WebSocket 连接
                    mJsMethodConstant.closeSocket(jsBean);
                    break;
            }
        }
    }

    /**
     * 处理通过热点发送设备注册结果
     *
     * @param status
     * @param error
     * @return
     */
    private String registerDeviceJsCallback(int status, String error) {
        String callbackJs = "zhiting.callBack('" + registerDeviceCallbackID + "'," + "'{\"status\":" + status + ",\"error\":\"" + error + "\"}')";
        return callbackJs;
    }

    /**
     * 蓝牙扫描回调
     */
    private class BluetoothScanCallback extends ScanCallback {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                onLeScan(result);
            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            onLeScan(result);
        }

        /**
         * 处理扫描到的蓝牙
         *
         * @param scanResult
         */
        private void onLeScan(ScanResult scanResult) {
            if (scanResult != null) {
                BluetoothDevice bluetoothDevice = scanResult.getDevice();
                if (bluetoothDevice != null) {
                    String name = bluetoothDevice.getName();
                    if (TextUtils.isEmpty(name)) {
                        return;
                    }
                    blueDeviceAdded.add(bluetoothDevice.getName());
                    DeviceBean deviceBean = new DeviceBean();
                    deviceBean.setName(scanResult.getDevice().getName());
                    deviceBean.setBluetoothDevice(bluetoothDevice);
                    if (JsMethodConstant.mBlueLists != null)
                        JsMethodConstant.mBlueLists.add(bluetoothDevice);
                    mScanLists.add(deviceBean);
                }
            }
        }
    }

    /**
     * 蓝牙配网回调
     */
    private class BlufiCallbackMain extends BlufiCallback {
        @Override
        public void onGattPrepared(BlufiClient client, BluetoothGatt gatt, BluetoothGattService service,
                                   BluetoothGattCharacteristic writeChar, BluetoothGattCharacteristic notifyChar) {
            if (service == null) {
                LogUtil.w("Discover service failed");
                gatt.disconnect();
                LogUtil.e("Discover service failed");
                return;
            }
            if (writeChar == null) {
                LogUtil.w("Get write characteristic failed");
                gatt.disconnect();
                LogUtil.e("Get write characteristic failed");
                return;
            }
            if (notifyChar == null) {
                LogUtil.w("Get notification characteristic failed");
                gatt.disconnect();
                LogUtil.e("Get notification characteristic failed");
                return;
            }

            LogUtil.e("Discover service and characteristics success");

            int mtu = BlufiUtil.DEFAULT_MTU_LENGTH;
            LogUtil.d("Request MTU " + mtu);
            boolean requestMtu = gatt.requestMtu(mtu);
            if (!requestMtu) {
                LogUtil.w("Request mtu failed");
                LogUtil.e(String.format(Locale.ENGLISH, "Request mtu %d failed", mtu));
            }
        }

        @Override
        public void onNegotiateSecurityResult(BlufiClient client, int status) {
            if (status == STATUS_SUCCESS) {
                LogUtil.e("Negotiate security complete");
            } else {
                LogUtil.e("Negotiate security failed， code=" + status);
            }
        }

        @Override
        public void onPostConfigureParams(BlufiClient client, int status) {
            if (status == STATUS_SUCCESS) {  // 发送配置参数成功

                LogUtil.e("Post configure params complete");
            } else { // 发送配置参数失败
                mJsMethodConstant.connectNetworkByBluetoothResult(JsMethodConstant.FAIL, String.format(Locale.ENGLISH, "Receive error code %d", status));
                LogUtil.e("Post configure params failed, code=" + status);
            }
        }

        @Override
        public void onDeviceStatusResponse(BlufiClient client, int status, BlufiStatusResponse response) {
            if (response.isStaConnectWifi()) { // 配网成功
                UiUtil.postDelayed(() -> mJsMethodConstant.connectNetworkByBluetoothResult(JsMethodConstant.SUCCESS, "configure params complete"), 1000);
            } else {
                mJsMethodConstant.connectNetworkByBluetoothResult(JsMethodConstant.FAIL, String.format(Locale.ENGLISH, "Receive error code %d", status));
            }

            if (status == STATUS_SUCCESS) {
                LogUtil.e(String.format("Receive device status response:\n%s", response.generateValidInfo()));
            } else {
                LogUtil.e("Device status response error, code=" + status);
            }
        }

        @Override
        public void onDeviceScanResult(BlufiClient client, int status, List<BlufiScanResult> results) {
            if (status == STATUS_SUCCESS) {
                StringBuilder msg = new StringBuilder();
                msg.append("Receive device scan result:\n");
                for (BlufiScanResult scanResult : results) {
                    msg.append(scanResult.toString()).append("\n");
                }
                LogUtil.e(msg.toString());
            } else {
                LogUtil.e("Device scan result error, code=" + status);
            }
        }

        @Override
        public void onDeviceVersionResponse(BlufiClient client, int status, BlufiVersionResponse response) {
            if (status == STATUS_SUCCESS) {
                LogUtil.e(String.format("Receive device version: %s", response.getVersionString()));
            } else {
                LogUtil.e("Device version error, code=" + status);
            }
        }

        @Override
        public void onPostCustomDataResult(BlufiClient client, int status, byte[] data) {
            String dataStr = new String(data);
            String format = "Post data %s %s";
            if (status == STATUS_SUCCESS) {
                LogUtil.e(String.format(format, dataStr, "complete"));
            } else {
                LogUtil.e(String.format(format, dataStr, "failed"));
            }
        }

        @Override
        public void onReceiveCustomData(BlufiClient client, int status, byte[] data) {
            if (status == STATUS_SUCCESS) {
                String customStr = null;
                try {
                    mDeviceId = new String(data, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                LogUtil.e(String.format("Receive custom data:\n%s", customStr));
            } else {
                LogUtil.e("Receive custom data error, code=" + status);
            }
        }

        @Override
        public void onError(BlufiClient client, int errCode) {
            mJsMethodConstant.connectNetworkByBluetoothResult(JsMethodConstant.FAIL, String.format(Locale.ENGLISH, "Receive error code %d", errCode));
            LogUtil.e(String.format(Locale.ENGLISH, "Receive error code %d", errCode));
        }
    }
}