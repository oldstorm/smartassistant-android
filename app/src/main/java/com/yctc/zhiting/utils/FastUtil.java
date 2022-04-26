package com.yctc.zhiting.utils;

/**
 * date : 2021/7/14 17:28
 * desc :
 */
public class FastUtil {
    private static long mLastClickTime; // 上次点击时间

    public static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean result = (currentTime - mLastClickTime) < 2000;
        mLastClickTime = currentTime;
        return result;
    }

    private static long mLastWifiConnectedTime; // 接收最后wifi连上广播时间
    private static long mLastWifiDisconnectedTime; // 接收最后wifi断开广播时间

    public static boolean isWifiConnectedOverOne() {
        long currentTime = System.currentTimeMillis();
        boolean result = (currentTime - mLastWifiConnectedTime) > 1000;
        mLastWifiConnectedTime = currentTime;
        return result;
    }

    public static boolean isWifiDisconnectedOverOne() {
        long currentTime = System.currentTimeMillis();
        boolean result = (currentTime - mLastWifiDisconnectedTime) > 1000;
        mLastWifiDisconnectedTime = currentTime;
        return result;
    }
}
