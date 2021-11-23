package com.yctc.zhiting.utils.confignetwork;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import blufi.espressif.BlufiCallback;
import blufi.espressif.BlufiClient;
import blufi.espressif.params.BlufiConfigureParams;
import blufi.espressif.params.BlufiParameter;

public class BlufiUtil {

    public static final int MIN_MTU_LENGTH = 23;
    public static final int MAX_MTU_LENGTH = 517;
    public static final int DEFAULT_MTU_LENGTH = 512;

    private Context mContext;
    private WifiManager mWifiManager;
    private BlufiClient mBlufiClient;

    public BlufiUtil(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 连接
     * @param device  要连接的设备
     * @param gattCallback  连接回调
     * @param blufiCallback blufi回调
     */
    public void connect(BluetoothDevice device, BluetoothGattCallback gattCallback, BlufiCallback blufiCallback){
        if (mBlufiClient != null) {
            mBlufiClient.close();
            mBlufiClient = null;
        }
        mBlufiClient = new BlufiClient(mContext, device);
        mBlufiClient.setGattCallback(gattCallback);
        if (blufiCallback!=null)
        mBlufiClient.setBlufiCallback(blufiCallback);
        mBlufiClient.connect();
    }

    /**
     * 断开连接
     */
    public void disconnect(){
        if (mBlufiClient != null) {
            mBlufiClient.requestCloseConnection();
        }
    }

    /**
     * Request to configure station or softap
     *
     * @param params configure params
     */
    public void configure(BlufiConfigureParams params) {
        if (mBlufiClient != null) {
            mBlufiClient.configure(params);
        }
    }

    /**
     * 创建配置信息实体
     * @param deviceMode       模式  sta/softap
     * @param ssid             wifi名称
     * @param password         wifi密码
     * @param channel          频道（softap才有  1-13）
     * @param maxConnection    最大连接数（softap才有 1-4）
     * @param security         安全级别（softap才有 SOFTAP_SECURITY_OPEN， SOFTAP_SECURITY_WEP， SOFTAP_SECURITY_WPA， SOFTAP_SECURITY_WPA2， SOFTAP_SECURITY_WPA_WPA2）
     * @return
     */
    public BlufiConfigureParams createParams(int deviceMode, String ssid, String password, int channel,  int maxConnection, int security) {
        BlufiConfigureParams params = new BlufiConfigureParams();
        params.setOpMode(deviceMode);
        switch (deviceMode) {
            case BlufiParameter.OP_MODE_NULL:
                return params;
            case BlufiParameter.OP_MODE_STA:
                if (checkSta(params, ssid, password)) {
                    return params;
                } else {
                    return null;
                }
            case BlufiParameter.OP_MODE_SOFTAP:
                if (checkSoftAP(params, ssid, password, channel, maxConnection, security)) {
                    return params;
                } else {
                    return null;
                }
            case BlufiParameter.OP_MODE_STASOFTAP:
                if (checkSoftAP(params, ssid, password, channel, maxConnection, security) && checkSta(params, ssid, password)) {
                    return params;
                } else {
                    return null;
                }
        }

        return null;
    }

    /**
     * 是否是 sta模式
     * @param params   配置参数
     * @param ssid     wifi名称
     * @param password wifi密码
     * @return
     */
    private boolean checkSta(BlufiConfigureParams params, String ssid, String password) {
        if (TextUtils.isEmpty(ssid)) {
            return false;
        }
        params.setStaSSIDBytes(ssid.getBytes());
        params.setStaPassword(password);
        return true;
    }

    /**
     *
     * @param params              配置参数
     * @param ssid                wifi名称
     * @param password            wifi密码
     * @param channel             频道（softap才有  1-13）
     * @param maxConnection       最大连接数（softap才有 1-4）
     * @param security            安全级别（softap才有 SOFTAP_SECURITY_OPEN， SOFTAP_SECURITY_WEP， SOFTAP_SECURITY_WPA， SOFTAP_SECURITY_WPA2， SOFTAP_SECURITY_WPA_WPA2）
     * @return
     */
    public boolean checkSoftAP(BlufiConfigureParams params, String ssid, String password, int channel,  int maxConnection, int security) {
        params.setSoftAPSSID(ssid);
        params.setSoftAPPAssword(password);
        if (channel>0)
        params.setSoftAPChannel(channel);
        if (maxConnection>0)
        params.setSoftAPMaxConnection(maxConnection);
        if (security>0) {
            params.setSoftAPSecurity(security);
//            // 校验密码长度
//            switch (security) {
//                case BlufiParameter.SOFTAP_SECURITY_OPEN:
//                    return true;
//                case BlufiParameter.SOFTAP_SECURITY_WEP:
//                case BlufiParameter.SOFTAP_SECURITY_WPA:
//                case BlufiParameter.SOFTAP_SECURITY_WPA2:
//                case BlufiParameter.SOFTAP_SECURITY_WPA_WPA2:
//                    if (TextUtils.isEmpty(password) || password.length() < 8) {
//                        Log.e("WWWW", "SoftAP 密码过短");
//                        return false;
//                    }
//
//                    return true;
//            }
        }

//        return false;
        return true;
    }

    /**
     * 连接的wifi名称
     * @return
     */
    private String getConnectionSSID() {
        if (!mWifiManager.isWifiEnabled()) {
            return null;
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return null;
        }

        String ssid = wifiInfo.getSSID();
        if (ssid.startsWith("\"") && ssid.endsWith("\"") && ssid.length() >= 2) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    /**
     * wifi频率
     * @return
     */
    private int getConnectionFrequncy() {
        if (!mWifiManager.isWifiEnabled()) {
            return -1;
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return -1;
        }

        return wifiInfo.getFrequency();
    }

    /**
     * 是否5g
     * @param freq
     * @return
     */
    private boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }

    public void releaseSource(){
        if (mBlufiClient!=null) {
            mBlufiClient.close();
        }
    }
}
