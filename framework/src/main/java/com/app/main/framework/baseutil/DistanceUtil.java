package com.app.main.framework.baseutil;

import android.annotation.SuppressLint;

public class DistanceUtil {
    @SuppressLint("DefaultLocale")
    public static String formatDiatance(double distance){
        if (distance <= 999)
            return distance+"m";
        else
            return String.format("%.1f", distance/1000) + "km";
    }
}
