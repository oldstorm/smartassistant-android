package com.yctc.zhiting.utils.confignetwork.softap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.espressif.provisioning.ESPConstants;
import com.espressif.provisioning.ESPDevice;
import com.espressif.provisioning.ESPProvisionManager;
import com.espressif.provisioning.listeners.ProvisionListener;
import com.yctc.zhiting.utils.confignetwork.ConfigNetworkCallback;

/**
 * espressif wifi方式配网工具类
 */
public class EspressifConfigNetworkUtil {

    private final String TAG = EspressifConfigNetworkUtil.class.getSimpleName();
    private ESPProvisionManager provisionManager;



    /**
     * 初始化 ESPProvisionManager
     * @param context
     */
    public EspressifConfigNetworkUtil(Context context) {
        provisionManager = ESPProvisionManager.getInstance(context);
    }

    /**
     * 创建 ESPDevice设备)
     */
    public ESPDevice createDevice(String proof){
        ESPDevice espDevice = provisionManager.createESPDevice(ESPConstants.TransportType.TRANSPORT_SOFTAP, ESPConstants.SecurityType.SECURITY_1); // 创建设备
        provisionManager.getEspDevice().setProofOfPossession(proof);  // 设置session
        return espDevice;
    }

    /**
     * 创建并连接 ESPDevice设备)
     */
    @SuppressLint("MissingPermission")
    public void connectDevice(){
        provisionManager.getEspDevice().connectWiFiDevice(); // 连接设备
    }

    /**
     * 配网
     * @param ssidValue  wifi 名称
     * @param passphraseValue wifi 密码
     * @param callback  回调
     */
    public void configNetwork(String ssidValue, String passphraseValue, ConfigNetworkCallback callback){

        provisionManager.getEspDevice().provision(ssidValue, passphraseValue, new ProvisionListener() {  // 配网操作

            @Override
            public void createSessionFailed(Exception e) {
                callback.onFailed(ConfigNetworkCallback.CREATE_SESSION_FAILED, e);
            }

            @Override
            public void wifiConfigSent() {
                Log.e(TAG, "==========发送配置");
            }

            @Override
            public void wifiConfigFailed(Exception e) {
                callback.onFailed(ConfigNetworkCallback.WIFI_CONFIG_FAILED, e);
            }

            @Override
            public void wifiConfigApplied() {

                Log.e(TAG, "==========申请配置成功");
            }

            @Override
            public void wifiConfigApplyFailed(Exception e) {

                callback.onFailed(ConfigNetworkCallback.WIFI_CONFIG_APPLIED_FAILED, e);
            }

            @Override
            public void provisioningFailedFromDevice(final ESPConstants.ProvisionFailureReason failureReason) {

                switch (failureReason) {
                    case AUTH_FAILED:
                        callback.onFailed(ConfigNetworkCallback.ERROR_AUTHENTICATION_FAILED, null);
                        break;
                    case NETWORK_NOT_FOUND:
                        callback.onFailed(ConfigNetworkCallback.ERROR_NETWORK_NOT_FOUND, null);
                        break;
                    case DEVICE_DISCONNECTED:
                    case UNKNOWN:
                        callback.onFailed(ConfigNetworkCallback.TO_PROV_DEVICE_FAILED, null);
                         break;
                }
            }

            @Override
            public void deviceProvisioningSuccess() {
                Log.e(TAG, "==========配网成功");
                callback.onSuccess();
            }

            @Override
            public void onProvisioningFailed(Exception e) {
                callback.onFailed(ConfigNetworkCallback.TO_PROV_DEVICE_FAILED, e);
            }
        });
    }

    /**
     * 断开连接
     */
    public void disconnectDevice(){
        if (provisionManager.getEspDevice() != null) {
            provisionManager.getEspDevice().disconnectDevice();
        }
    }
}
