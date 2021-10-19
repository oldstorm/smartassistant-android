package com.app.main.framework.baseutil;

public class FastClickUtil {
    private static long startTime = 0;

    public static boolean isFastClick(long doubleClickTime){
        long endTime = System.currentTimeMillis();
        if ((endTime - startTime) < doubleClickTime){
            startTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }
}
