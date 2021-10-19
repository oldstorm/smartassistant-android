package com.yctc.zhiting.utils;

public class UsernameUtil {

    public static String getUsername(){
        String strRand = "User_";
        strRand += StringUtil.getUUid().substring(0, 6);
        return strRand;
    }
}
