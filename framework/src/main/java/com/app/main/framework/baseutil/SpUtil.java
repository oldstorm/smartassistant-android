package com.app.main.framework.baseutil;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {

    private static final String DEFAULT_SHARE_NAME = "zhiting";
    private static SharedPreferences sharedPreferences;
    public static void init(Context context){
        sharedPreferences = context.getSharedPreferences(DEFAULT_SHARE_NAME,Context.MODE_PRIVATE);
    }

    public static void put(String key,String value){
        sharedPreferences.edit().putString(key,value).apply();
    }

    public static String get(String key){
        return sharedPreferences.getString(key,"");
    }

    public static void put(String key, int value){
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static void putLong(String key, long value){
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public static long getLong(String key){
        return sharedPreferences.getLong(key, 0);
    }

    public static int getInt(String key){
        return sharedPreferences.getInt(key, 0);
    }

    public static void put(String key,boolean value){
        sharedPreferences.edit().putBoolean(key,value).apply();
    }

    public static boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }
}
