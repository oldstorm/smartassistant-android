package com.yctc.zhiting.utils;

import android.net.wifi.WifiInfo;
import android.text.TextUtils;

import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;

/**
 * date : 2021/6/1710:40
 * desc : 当前家庭信息获取
 */
public class HomeUtil {
    private static HomeCompanyBean mHome = new HomeCompanyBean();

    //获取家庭名字
    public static String getHomeName() {
        mHome = Constant.CurrentHome;
        if (mHome != null && !TextUtils.isEmpty(mHome.getName())) {
            return mHome.getName();
        }
        return "";
    }

    //获取家庭id
    public static int getHomeId() {
        mHome = Constant.CurrentHome;
        if (mHome != null) {
            return mHome.getId();
        }
        return 0;
    }

    //获取家庭SaToken
    public static String getSaToken() {
        mHome = Constant.CurrentHome;
        if (mHome != null && !TextUtils.isEmpty(mHome.getSa_user_token())) {
            return mHome.getSa_user_token();
        }
        return "";
    }

    /**
     * 判断是否有SA
     *
     * @return
     */
    public static boolean isBindSA() {
        mHome = Constant.CurrentHome;
        if (mHome == null)
            return false;
        return mHome.isIs_bind_sa();
    }

    /**
     * 用户user_id
     *
     * @return
     */
    public static int getUserId() {
        mHome = Constant.CurrentHome;
        if (mHome != null)
            return mHome.getUser_id();
        return -1;
    }

    //判断当前的家庭是否SA环境
    public static boolean isSAEnvironment() {
        WifiInfo wifiInfo = Constant.wifiInfo;
        HomeCompanyBean home = Constant.CurrentHome;
        if (home != null && wifiInfo != null && home.getMac_address() != null && wifiInfo.getBSSID() != null &&
                home.getMac_address().equalsIgnoreCase(wifiInfo.getBSSID())) {
            return true;
        }
        return false;
    }

    //判断当前的家庭是否SA环境
    public static boolean isHomeSAEnvironment(HomeCompanyBean homeCompanyBean) {
        WifiInfo wifiInfo = Constant.wifiInfo;
        HomeCompanyBean home = Constant.CurrentHome;
        if (homeCompanyBean != null && wifiInfo != null && homeCompanyBean.getMac_address() != null && wifiInfo.getBSSID() != null &&
                homeCompanyBean.getMac_address().equalsIgnoreCase(wifiInfo.getBSSID())) {
            return true;
        }
        return false;
    }
}
