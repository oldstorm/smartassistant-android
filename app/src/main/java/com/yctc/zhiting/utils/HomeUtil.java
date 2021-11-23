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

    public static boolean tokenIsInvalid; // saToken是否失效

    //获取家庭名字
    public static String getHomeName() {
        mHome = Constant.CurrentHome;
        if (mHome != null && !TextUtils.isEmpty(mHome.getName())) {
            return mHome.getName();
        }
        return "";
    }

    //获取家庭id
    public static long getHomeId() {
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
     * 判断家庭是否有id,云端有虚拟sa
     *
     * @return
     */
    public static boolean isHomeIdThanZero() {
        mHome = Constant.CurrentHome;
        if (mHome == null)
            return false;
        return (mHome.getId() > 0 && UserUtils.isLogin()) || mHome.isIs_bind_sa();
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
        return isSAEnvironment(Constant.CurrentHome);
    }

    //判断当前的家庭是否SA环境
    public static boolean isSAEnvironment(HomeCompanyBean home) {
        WifiInfo wifiInfo = Constant.wifiInfo;
        if (home != null && wifiInfo != null && home.getMac_address() != null && wifiInfo.getBSSID() != null &&
                home.getMac_address().equalsIgnoreCase(wifiInfo.getBSSID())) {
            return true;
        }
        return false;
    }

    public static boolean notLoginAndSAEnvironment() {
        if (UserUtils.isLogin()) { // 登录了
            return false;
        } else { // 没登录
            return !HomeUtil.isSAEnvironment(); // 是否在sa
        }
    }
}
