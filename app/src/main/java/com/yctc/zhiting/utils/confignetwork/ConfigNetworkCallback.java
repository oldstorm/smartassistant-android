package com.yctc.zhiting.utils.confignetwork;

/**
 * 配网结果回调
 */
public interface ConfigNetworkCallback {

    int CREATE_SESSION_FAILED = 1;
    int WIFI_CONFIG_FAILED = 2;
    int WIFI_CONFIG_APPLIED_FAILED = 3;
    int ERROR_AUTHENTICATION_FAILED = 4;
    int ERROR_NETWORK_NOT_FOUND = 5;
    int TO_PROV_DEVICE_FAILED = 6;

    void onSuccess();
    void onFailed(int errorCode, Exception e);
}
