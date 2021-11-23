package com.yctc.zhiting.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.TimeFormatUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BaseApplication;
import com.app.main.framework.entity.ChannelEntity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.espressif.provisioning.ESPDevice;
import com.espressif.provisioning.ESPProvisionManager;
import com.espressif.provisioning.WiFiAccessPoint;
import com.espressif.provisioning.listeners.WiFiScanListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.application.Application;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.JsBean;
import com.yctc.zhiting.utils.confignetwork.BlufiUtil;
import com.yctc.zhiting.utils.confignetwork.ConfigNetworkCallback;
import com.yctc.zhiting.utils.confignetwork.WifiUtil;
import com.yctc.zhiting.utils.confignetwork.softap.EspressifConfigNetworkUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import blufi.espressif.BlufiCallback;
import blufi.espressif.BlufiClient;
import blufi.espressif.params.BlufiConfigureParams;
import blufi.espressif.params.BlufiParameter;
import blufi.espressif.response.BlufiScanResult;
import blufi.espressif.response.BlufiStatusResponse;
import blufi.espressif.response.BlufiVersionResponse;

/**
 * 配网js交互方法
 */
public class JsMethodConstant {

    public static final int SUCCESS = 0; // 原生处理结果反馈h5
    public static final int FAIL = 1; // 原生处理结果反馈h5

    public static final String NETWORK_TYPE = "networkType"; // 查看app连网类型
    public static final String GET_USER_INFO = "getUserInfo";  // 获取用户信息
    public static final String SET_TITLE = "setTitle";  // 设置标题属性
    public static final String IS_PROFESSION = "isProfession";  // 是否是专业版app
    public static final String CONNECT_DEVICE_BY_BLUETOOTH = "connectDeviceByBluetooth";  // 通过蓝牙连接设备
    public static final String CONNECT_NETWORK_BY_BLUETOOTH = "connectNetworkByBluetooth";  // 通过蓝牙发送配网信息
    public static final String CONNECT_DEVICE_HOST_SPOT = "connectDeviceHotspot";  // 连接设备热点
    public static final String CREATE_DEVICE_BY_HOTSPOT = "createDeviceByHotspot";  // 通过设备热点创建设备
    public static final String CONNECT_DEVICE_BY_HOTSPOT = "connectDeviceByHotspot";  // 通过热点连接设备
    public static final String CONNECT_NETWORK_BY_HOTSPOT = "connectNetworkByHotspot";  // 通过热点发送配网信息
    public static final String GET_DEVICE_INFO = "getDeviceInfo";  // 通过热点发送配网信息
    public static final String GET_CONNECT_WIFI = "getConnectWifi";  // 获取设备当前连接wifi
    public static final String TO_SYSTEM_WLAN = "toSystemWlan";  // 去系统设置wlan页
    public static final String GET_SYSTEM_WIFI_LIST = "getSystemWifiList";  // 获取wifi列表页
    public static final String GET_SOCKET_ADDRESS = "getSocketAddress"; // 获取插件websocket地址

    private static String connectBleCallbackID;  // 连接蓝牙回调id
    private static String connectNetworkByBluetoothCallbackID; // 通过蓝牙配网id
    private static String connectDeviceHotspotCallbackID;  // 连接设备热点id
    private static String createDeviceByHotspotCallbackID;  // 通过设备热点创建设备id
    private static String connectDeviceByHotspotCallbackID;  // 通过热点连接设备id
    private static String connectNetworkByHotspotCallbackID;  // 通过热点连接设备id
    public static List<BluetoothDevice> mBlueLists; // 蓝牙设备列表

    private  WebView mWebView;
    // 蓝牙配网
    private  BlufiUtil mBlufiUtil;
    // ap配网
    private  ESPProvisionManager provisionManager;
    private static EspressifConfigNetworkUtil espressifConfigNetworkUtil;
    private static ArrayList<WiFiAccessPoint> mWifiAPList;
    private WifiUtil mWifiUtil;
    public static boolean dealConnectHotspot; // 是否需要处理连接设备热点结果
    public static String mHotspotName; // 要连接wifi热点名称

    public JsMethodConstant(WebView mWebView) {
        this.mWebView = mWebView;
    }

    public JsMethodConstant(WebView webView, BlufiUtil blufiUtil, WifiUtil wifiUtil){
        init(webView, blufiUtil, wifiUtil);
    }

    /**
     * 初始化
     *
     * @param webView
     * @param blufiUtil
     */
    public void init(WebView webView, BlufiUtil blufiUtil, WifiUtil wifiUtil) {
        mBlueLists = new ArrayList<>();
        mWifiAPList = new ArrayList<>();
        mWebView = webView;
        mWebView.resumeTimers();
        mBlufiUtil = blufiUtil;
        mWifiUtil = wifiUtil;
        provisionManager = ESPProvisionManager.getInstance(Application.getContext());
        espressifConfigNetworkUtil = new EspressifConfigNetworkUtil(Application.getContext());
//        startWifiScan();
    }

    /**
     * 释放资源
     */
    public  void release() {
        if (mBlueLists!=null) {
            mBlueLists.clear();
            mBlueLists = null;
        }
        if (mWifiAPList!=null) {
            mWifiAPList.clear();
            mWifiAPList = null;
        }
        if (mBlufiUtil != null) {
            mBlufiUtil.disconnect();
            mBlufiUtil = null;
        }
        if (espressifConfigNetworkUtil != null) {
            espressifConfigNetworkUtil.disconnectDevice();
        }
        provisionManager = null;
        espressifConfigNetworkUtil = null;
        if (mWifiUtil != null) {
            mWifiUtil.release();
            mWifiUtil = null;
        }
        mHotspotName = null;
        dealConnectHotspot = false;
    }


    private  void startWifiScan() {
        if (provisionManager == null || mWifiAPList == null) {
            return;
        }
        Log.d("startWifiScan====", "Start Wi-Fi Scan");
        mWifiAPList.clear();


        provisionManager.getEspDevice().scanNetworks(new WiFiScanListener() {
            @Override
            public void onWifiListReceived(final ArrayList<WiFiAccessPoint> wifiList) {
                mWifiAPList.addAll(wifiList);
            }

            @Override
            public void onWiFiScanFailed(Exception e) {

                // TODO
                Log.e("onWiFiScanFailed", "onWiFiScanFailed");
                e.printStackTrace();
                UiUtil.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("Failed to get Wi-Fi scan list");
                    }
                });
            }
        });
    }

    /**
     * 拼接回调给h5的js代码
     *
     * @param callbackId
     * @param status
     * @param error
     * @return
     */
    public static String dealJsCallback(String callbackId, int status, String error) {
        String callbackJs = "zhiting.callBack('" + callbackId + "'," + "'{\"status\":" + status + ",\"error\":\"" + error + "\"}')";
        return callbackJs;
    }

    /**
     * 把结果回调给h5
     *
     * @param js
     */
    private  void runOnMainUi(String js) {
        UiUtil.runInMainThread(new Runnable() {
            @Override
            public void run() {
                if (mWebView != null)
                    mWebView.loadUrl("javascript:" + js);
            }
        });
    }

    /**
     * 通过蓝牙连接设备
     *
     * @param jsBean
     */
    public  void connectDeviceByBluetooth(JsBean jsBean, BlufiCallback blufiCallback) {
        if (mBlufiUtil == null) {
            delayBluetoothFail();
            return;
        }
        if (jsBean != null) {
            JsBean.JsSonBean jsSonBean = jsBean.getParams();
            connectBleCallbackID = jsBean.getCallbackID();
            if (jsSonBean != null) {
                String bluetoothName = jsSonBean.getBluetoothName();
                if (!TextUtils.isEmpty(bluetoothName)) {
                    BluetoothDevice bluetoothDevice = null;
                    if (mBlueLists!=null) {
                        for (BluetoothDevice bd : mBlueLists) {
                            String name = bd.getName();
                            if (name != null && name.equals(bluetoothName)) {
                                bluetoothDevice = bd;
                                break;
                            }
                        }
                        if (bluetoothDevice != null) { // 蓝牙设备不为空
                            mBlufiUtil.connect(bluetoothDevice, new GattCallback(), blufiCallback);
                        } else { // 蓝牙设备为空
                            delayBluetoothFail();
                        }
                    }else {
                        delayBluetoothFail();
                    }
                }else {
                    delayBluetoothFail();
                }
            }
        }
    }

    private void delayBluetoothFail(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectBluetoothResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.bluetooth_is_not_found));
            }
        }, 10000);
    }

    /**
     * 连接蓝牙结果
     *
     * @param status
     * @param error
     */
    private  void connectBluetoothResult(int status, String error) {
        if (!TextUtils.isEmpty(connectBleCallbackID)) {
            String connectBleJs = dealJsCallback(connectBleCallbackID, status, error);
            runOnMainUi(connectBleJs);
            connectBleCallbackID = "";
        }
    }

    /**
     * 通过蓝牙发送配网信息
     *
     * @param jsBean
     */
    public  void connectNetworkByBluetooth(JsBean jsBean) {
        if (mBlufiUtil == null) {
            delayBluetoothFail();
            return;
        }
        if (jsBean != null) {
            JsBean.JsSonBean jsSonBean = jsBean.getParams();
            connectNetworkByBluetoothCallbackID = jsBean.getCallbackID();
            if (jsSonBean != null) {
                String wifiName = jsSonBean.getWifiName();
                String wifiPwd = jsSonBean.getWifiPass();
                if (TextUtils.isEmpty(wifiName)) {
                    connectNetworkByBluetoothResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.home_please_input_wifi_name));
                    return;
                }
                if (TextUtils.isEmpty(wifiPwd)) {
                    connectNetworkByBluetoothResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.home_please_input_wifi_name));
                    return;
                }
                BlufiConfigureParams blufiConfigureParams = mBlufiUtil.createParams(BlufiParameter.OP_MODE_STA, wifiName, wifiPwd, 0, 0, 0);
                mBlufiUtil.configure(blufiConfigureParams);
            }
        }
    }

    /**
     * 通过蓝牙发送配网信息结果
     *
     * @param status
     * @param error
     */
    public  void connectNetworkByBluetoothResult(int status, String error) {
        if (!TextUtils.isEmpty(connectNetworkByBluetoothCallbackID)) {
            String connectNetJs = dealJsCallback(connectNetworkByBluetoothCallbackID, status, error);
            runOnMainUi(connectNetJs);
            connectNetworkByBluetoothCallbackID = "";
        }
    }

    /**
     * 连接设备热点
     *
     * @param jsBean
     */
    public  void connectDeviceHotspot(JsBean jsBean) {
        if (jsBean != null) {
            JsBean.JsSonBean jsSonBean = jsBean.getParams();
            connectDeviceHotspotCallbackID = jsBean.getCallbackID();
            if (jsSonBean != null) {
                String hotspotName = jsSonBean.getHotspotName();
                mHotspotName = hotspotName;
                if (TextUtils.isEmpty(hotspotName)) {
                    connectDeviceHotspotResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.please_input_hotspotname));
                    return;
                }
                if (mWifiUtil != null) {
                    if (mWifiUtil.isWifiEnabled()) {
                        dealConnectHotspot = true;
                        mWifiUtil.connectWifiPwd(hotspotName, "");
                    } else {
                        connectDeviceHotspotResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.wifi_disable));
                    }
                }else {
                    connectDeviceHotspotResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.wifi_disable));
                }

            }
        }
    }

    /**
     * 检查要连接的热点是否
     *
     * @param hotspotName
     * @return
     */
    public static boolean findHotspot(String hotspotName) {
        for (WiFiAccessPoint wiFiAccessPoint : mWifiAPList) {
            String name = wiFiAccessPoint.getWifiName();
            if (name != null && name.equals(hotspotName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 连接设备热点结果
     *
     * @param status
     * @param error
     */
    public  void connectDeviceHotspotResult(int status, String error) {
        String connectDeviceHotspot = dealJsCallback(connectDeviceHotspotCallbackID, status, error);
        System.out.println("连接设备热点回调："+connectDeviceHotspot);
        runOnMainUi(connectDeviceHotspot);
        connectDeviceHotspotCallbackID = "";
    }

    /**
     * 通过设备热点创建设备
     *
     * @param jsBean
     */
    public  void createDeviceByHotspot(JsBean jsBean) {
        if (jsBean != null) {
            JsBean.JsSonBean jsSonBean = jsBean.getParams();
            createDeviceByHotspotCallbackID = jsBean.getCallbackID();
            if (jsSonBean != null) {
                String ownership = jsSonBean.getOwnership();
                if (TextUtils.isEmpty(ownership)) {
                    createDeviceByHotspotResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.please_input_ownership));
                    return;
                }
                ESPDevice espDevice = espressifConfigNetworkUtil.createDevice(ownership);
                int status = espDevice == null ? FAIL : SUCCESS;
                String error = espDevice == null ? UiUtil.getString(R.string.failed) : UiUtil.getString(R.string.success);
                createDeviceByHotspotResult(status, error);
            }
        }
    }

    /**
     * 通过设备热点创建设备结果
     *
     * @param status
     * @param error
     */
    private  void createDeviceByHotspotResult(int status, String error) {
        String connectDeviceHotspot = dealJsCallback(createDeviceByHotspotCallbackID, status, error);
        runOnMainUi(connectDeviceHotspot);
        createDeviceByHotspotCallbackID = "";
    }

    /**
     * 通过设备热点创建设备
     *
     * @param jsBean
     */
    public static void connectDeviceByHotspot(JsBean jsBean) {
        if (jsBean != null) {
            connectDeviceByHotspotCallbackID = jsBean.getCallbackID();
            espressifConfigNetworkUtil.connectDevice();
        }
    }

    /**
     * 通过热点连接设备结果
     *
     * @param status
     * @param error
     */
    public  void connectDeviceByHotspotResult(int status, String error) {
        String connectDeviceHotspot = dealJsCallback(connectDeviceByHotspotCallbackID, status, error);
        runOnMainUi(connectDeviceHotspot);
        connectDeviceByHotspotCallbackID = "";
    }

    /**
     * 通过设备热点创建设备
     *
     * @param jsBean
     */
    public  void connectNetworkByHotspot(JsBean jsBean, ConfigNetworkCallback callback) {
        if (jsBean != null) {
            JsBean.JsSonBean jsSonBean = jsBean.getParams();
            connectNetworkByHotspotCallbackID = jsBean.getCallbackID();
            if (jsSonBean != null) {
                String wifiName = jsSonBean.getWifiName();
                String pwd = jsSonBean.getWifiPass();
                if (TextUtils.isEmpty(wifiName)) {
                    connectNetworkByHotspotResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.home_please_input_wifi_name));
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    connectNetworkByHotspotResult(JsMethodConstant.FAIL, UiUtil.getString(R.string.home_please_input_wifi_password));
                    return;
                }
                espressifConfigNetworkUtil.configNetwork(wifiName, pwd, callback);
            }
        }
    }

    /**
     * 通过热点连接设备结果
     *
     * @param status
     * @param error
     */
    public  void connectNetworkByHotspotResult(int status, String error) {
        String connectDeviceHotspot = dealJsCallback(connectNetworkByHotspotCallbackID, status, error);
        runOnMainUi(connectDeviceHotspot);
        connectNetworkByHotspotCallbackID = "";
    }

    /**
     * 获取配网的设备信息
     * @param jsBean
     * @param json
     */
    public  void getDeviceInfo(JsBean jsBean, String json){
        if (jsBean != null) {
            String getDeviceInfoDCallbackID = jsBean.getCallbackID();
            String devInfoJs = "zhiting.callBack('" + getDeviceInfoDCallbackID + "'," + json+")";
            LogUtil.e(devInfoJs);
            runOnMainUi(devInfoJs);
        }
    }

    /**
     * 当前wifi名称
     * @param jsBean
     */
    public  void getConnectWifi(JsBean jsBean){
        if (jsBean!=null){
            int status = mWifiUtil.isConnectWifi() ? SUCCESS : FAIL;
            String wifiName = mWifiUtil.isConnectWifi() ? mWifiUtil.getCurrentWifiName() : "";
            String wifiNameJs = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{\"status\":" + status + ",\"wifiName\":\"" + wifiName + "\"}')";
            runOnMainUi(wifiNameJs);
        }
    }

    /**
     * 获取wifi列表页
     * @param jsBean
     */
    public  void getSystemWifiList(JsBean jsBean){
        if (jsBean!=null){
            int status = 0;
            String wifiJson = "\\[]";
            if (GpsUtil.isEnabled(BaseApplication.getContext())) {
                List<WifiUtil.WifiNameSignBean> wifiNames = mWifiUtil.getWifiNameSignList();
                status = 0;
                 wifiJson = GsonConverter.getGson().toJson(wifiNames);
            }else {
                status = 1;
                LogUtil.e("请打开Gps");
            }
            String wifiNameJs = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{\"status\":" + status + ",\"list\":" + wifiJson + "}')";
            System.out.println("wifi列表：" + wifiNameJs);
            runOnMainUi(wifiNameJs);
        }
    }

    /**
     * 获取插件websocket地址
     * @param jsBean
     */
    public void getSocketAddress(JsBean jsBean){
        if (jsBean!=null){
            int status = 0;
            String address = "";
            if (Constant.CurrentHome!=null && !TextUtils.isEmpty(Constant.CurrentHome.getSa_lan_address())) {
               status = 0;
               address = getUrl();
            }else {
                status = 1;
            }
            System.out.println("websoket地址："+address);
            String socketAddrJs = "zhiting.callBack('" + jsBean.getCallbackID() + "'," + "'{\"status\":" + status + ",\"address\":\"" + address + "\"}')";
            runOnMainUi(socketAddrJs);
        }
    }

    /**
     * 获取地址
     *
     * @return
     */
    private String getUrl() {
        String urlSA = "";
        if (Constant.CurrentHome!=null && !TextUtils.isEmpty(Constant.CurrentHome.getSa_lan_address())) {
            String url = "ws://"+Constant.CurrentHome.getSa_lan_address().replace("http://", "")+"/ws";
            urlSA = url;
        }
        if (HomeUtil.isSAEnvironment()) {
            return urlSA;
        } else {
            if (UserUtils.isLogin()) {
                return getSCUrl();
            } else {
                return urlSA;
            }
        }
    }

    private String getSCUrl() {
        String newUrlSC = "wss://sc.zhitingtech.com/ws";
        long currentTime = TimeFormatUtil.getCurrentTime();
        String tokenKey = SpUtil.get(SpConstant.SA_TOKEN);
        String json = SpUtil.get(tokenKey);

        if (!TextUtils.isEmpty(json)) {
            ChannelEntity channel = GsonConverter.getGson().fromJson(json, ChannelEntity.class);
            if (channel != null) {
                LogUtil.e("WSocketManager=getSCUrl=" + GsonConverter.getGson().toJson(channel));
                if ((currentTime - channel.getCreate_channel_time()) < channel.getExpires_time()) {
                    String mHostSC = "sc.zhitingtech.com";
                    newUrlSC = newUrlSC.replace(mHostSC, channel.getHost());
                    newUrlSC=newUrlSC.replace("wss","ws");
                    LogUtil.e("WSocketManager=getSCUrl=" + newUrlSC);
                }
            }
        }
        return newUrlSC;
    }

    /**
     * 蓝牙连接结果回调
     * mBlufiClient call onCharacteristicWrite and onCharacteristicChanged is required
     */
    private class GattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String devAddr = gatt.getDevice().getAddress();
            LogUtil.d(String.format(Locale.ENGLISH, "onConnectionStateChange addr=%s, status=%d, newState=%d",
                    devAddr, status, newState));
            int st = JsMethodConstant.FAIL;
            String error = "";
            if (status == BluetoothGatt.GATT_SUCCESS) {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:  // 蓝牙连接成功
                        LogUtil.e("蓝牙连接成功");
                        st = JsMethodConstant.SUCCESS;
                        error = UiUtil.getString(R.string.bluetooth_connect_success);
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED: // 蓝牙连接失败
                        gatt.close();
                        st = JsMethodConstant.FAIL;
                        LogUtil.e("蓝牙断开连接");
                        error = UiUtil.getString(R.string.bluetooth_disconnect);
                        break;
                }
            } else { // 蓝牙连接失败
                gatt.close();
                st = JsMethodConstant.FAIL;
                LogUtil.e("蓝牙连接失败");
                error = UiUtil.getString(R.string.bluetooth_connect_fail);
            }
            connectBluetoothResult(st, error);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            LogUtil.d(String.format(Locale.ENGLISH, "onMtuChanged status=%d, mtu=%d", status, mtu));
            if (status == BluetoothGatt.GATT_SUCCESS) {

            } else {

            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LogUtil.d(String.format(Locale.ENGLISH, "onServicesDiscovered status=%d", status));
            if (status != BluetoothGatt.GATT_SUCCESS) {
                gatt.disconnect();

            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            LogUtil.d(String.format(Locale.ENGLISH, "onDescriptorWrite status=%d", status));
            if (descriptor.getUuid().equals(BlufiParameter.UUID_NOTIFICATION_DESCRIPTOR) &&
                    descriptor.getCharacteristic().getUuid().equals(BlufiParameter.UUID_NOTIFICATION_CHARACTERISTIC)) {
                String msg = String.format(Locale.ENGLISH, "Set notification enable %s", (status == BluetoothGatt.GATT_SUCCESS ? " complete" : " failed"));

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                gatt.disconnect();
                LogUtil.d(String.format(Locale.ENGLISH, "WriteChar error status %d", status));
            }
        }
    }


}
