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
}
