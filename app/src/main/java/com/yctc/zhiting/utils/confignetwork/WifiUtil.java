package com.yctc.zhiting.utils.confignetwork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.PatternMatcher;
import android.provider.Settings;
import android.text.TextUtils;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.dialog.CenterAlertDialog;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class WifiUtil {
    // 上下文Context对象
    private BaseActivity mActivity;
    // WifiManager对象
    private WifiManager mWifiManager;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;


    public WifiUtil(BaseActivity activity) {
        mActivity = activity;
        mWifiManager = (WifiManager) mActivity.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    public void release(){
        if (connectivityManager!=null && networkCallback!=null){
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    /**
     * wifi是否打开
     * @return
     */
    public boolean isWifiEnabled(){
        boolean isEnabled = false;
        if (mWifiManager!=null){
            if (mWifiManager.isWifiEnabled()){
                isEnabled = true;
            }
        }
        return isEnabled;
    }

    /**
     * 打开WiFi
     */
    public void openWifi(){
        if (mWifiManager!=null && !isWifiEnabled()){
            CenterAlertDialog alertDialog = CenterAlertDialog.newInstance(UiUtil.getString(R.string.home_blue_tooth_disabled),UiUtil.getString(R.string.home_guide_user_to_open_bluetooth),
                    UiUtil.getString(R.string.home_cancel), UiUtil.getString(R.string.home_setting), false);
            alertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
                @Override
                public void onConfirm(boolean del) {
//                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(intent);
                    alertDialog.dismiss();
                }
            });
            alertDialog.show(mActivity);

        }
    }

    /**
     * 关闭wifi
     */
    public void closeWifi(){
        if(mWifiManager!=null && isWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 判断手机是否连接在Wifi上
     */
    public boolean isConnectWifi() {
        // 获取ConnectivityManager对象
        ConnectivityManager conMgr = (ConnectivityManager) mActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取NetworkInfo对象
        NetworkInfo info = conMgr.getActiveNetworkInfo();
        // 获取连接的方式为wifi
        NetworkInfo.State wifi = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (info != null && info.isAvailable() && wifi == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 获取当前手机所连接的wifi信息
     */
    public WifiInfo getCurrentWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    public String getCurrentWifiName(){
        String name = "";
        if (isConnectWifi()){
            name = getCurrentWifiInfo().getSSID();
            name = name.substring(1, name.length()-1);
        }
        return name;
    }

    /**
     * 添加一个网络并连接 传入参数：WIFI发生配置类WifiConfiguration
     */
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        return mWifiManager.enableNetwork(wcgID, true);
    }
    /**
     * 搜索附近的热点信息，并返回所有热点为信息的SSID集合数据
     */
    public List<ScanResult> getScanWifiResult() {
        // 扫描的热点数据
        List<ScanResult> resultList;
        // 开始扫描热点
        mWifiManager.startScan();
        resultList = mWifiManager.getScanResults();

        return resultList;
    }

    /**
     * wifi列表名称
     * @return
     */
    public List<String> getWifiNames(){
        List<String> wifiNames = new ArrayList<>();
        for (ScanResult scanResult : getScanWifiResult()){
            wifiNames.add(scanResult.BSSID);
        }
        return wifiNames;
    }

    /**
     * wifi和信号列表
     * @return
     */
    public List<WifiNameSignBean> getWifiNameSignList(){
        List<WifiNameSignBean> wifiNameSignList = new ArrayList<>();
        // 扫描的热点数据
        List<ScanResult> resultList;
        // 开始扫描热点
        mWifiManager.startScan();
        resultList = mWifiManager.getScanResults();
        if (resultList != null) {
            for (ScanResult scan : resultList) {
                if (!TextUtils.isEmpty(scan.SSID))
                wifiNameSignList.add(new WifiNameSignBean(scan.SSID));// 遍历数据，取得ssid和level数据集
            }
        }
        return wifiNameSignList;
    }

    /**
     * 连接WiFi
     * @param ssid
     * @param pwd
     */
    public void connectWifiPwd(String ssid, String pwd){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) { // Android10以上
            NetworkSpecifier specifier =
                    new WifiNetworkSpecifier.Builder()
                            .setSsidPattern(new PatternMatcher(ssid, PatternMatcher.PATTERN_PREFIX))
                            .setWpa2Passphrase("")
                            .build();

            NetworkRequest request =
                    new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .setNetworkSpecifier(specifier)
                            .build();

            connectivityManager = (ConnectivityManager)
                    mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    // do success processing here..

                }

                @Override
                public void onUnavailable() {
                    // do failure processing here..

                }
            };
            connectivityManager.requestNetwork(request, networkCallback);
            // Release the request when done.
//             connectivityManager.unregisterNetworkCallback(networkCallback);
        }else {
            mWifiManager.disableNetwork(mWifiManager.getConnectionInfo().getNetworkId());
            int netId = mWifiManager.addNetwork(getWifiConfig(ssid, pwd));
            mWifiManager.enableNetwork(netId, true);
        }
    }

    /**
     * wifi设置
     * @param ssid
     * @param pwd
     * @return
     */
    private WifiConfiguration getWifiConfig(String ssid, String pwd){
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\""+ssid+"\"";
        WifiConfiguration tempConfig = isExist(ssid);
        if (tempConfig!=null){
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        if (TextUtils.isEmpty(pwd)){
            config.wepKeys[0] = "";
            config.wepTxKeyIndex = 0;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        }else {
            config.preSharedKey = "\""+pwd+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 得到配置好的网络连接
     * @param ssid
     * @return
     */
    private WifiConfiguration isExist(String ssid){
        @SuppressLint("MissingPermission")
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs){
            if (config.SSID.equals("\""+ssid+"\"")){
                return config;
            }
        }
        return null;
    }

    public interface ConnectWifiCallback{
        void onSuccess();
        void onFail();
    }

    public static class WifiNameSignBean{
        private String wifiName;

        public WifiNameSignBean(String wifiName) {
            this.wifiName = wifiName;
        }

        public String getWifiName() {
            return wifiName;
        }

        public void setWifiName(String wifiName) {
            this.wifiName = wifiName;
        }

    }
}
