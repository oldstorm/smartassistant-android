package com.app.main.framework.baseutil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.app.main.framework.gsonutils.GsonConverter;

import java.io.IOException;
import java.net.InetAddress;

public class NetworkUtil {
    /**
     * 判断网络连接状态
     *
     * @return 返回true为连接
     */
    public static boolean isNetworkerConnect() {
        return getNetworkerStatus() != -1;
    }

    /**
     * 获取网络状态
     *
     * @return 1为wifi连接，2为2g网络，3为3g网络，-1为无网络连接
     */
    @SuppressLint("MissingPermission")
    public static int getNetworkerStatus() {
        ConnectivityManager conMan = (ConnectivityManager) UiUtil.getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conMan.getActiveNetworkInfo();
        if (null != info && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case 1:
                    case 2:
                    case 4:
                        // 2G网络
                        return 2;
                    default:
                        // 3G及其以上网络
                        return 3;
                }
            } else {
                // wifi网络
                return 1;
            }
        } else {
            // 无网络
            return -1;
        }
    }

    /**
     * 网络是否可用
     *
     * @return
     */
    public static boolean isNetworkAvailable() {
        Context context = LibLoader.getApplication();
        // 获取手机所有连接管理对象（包括对wifi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查地址是否ping得通
     *
     * @param ip
     * @return
     */
    public static boolean pingIPResult(String ip, int millisSecond) {
        try {
            return InetAddress.getByName(ip).isReachable(millisSecond);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isWifi() {
        return getNetworkerStatus() == 1;
    }
}
