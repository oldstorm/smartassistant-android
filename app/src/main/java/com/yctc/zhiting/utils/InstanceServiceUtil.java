package com.yctc.zhiting.utils;

import android.text.TextUtils;

import com.yctc.zhiting.entity.ws_request.WSConstant;

/**
 * WS service类型归类
 */
public class InstanceServiceUtil {

    public static final int SWITCH_SERVICE = 1; // 开关
    public static final int TH_SERVICE = 2; // 温湿度
    public static final int LEAK_SERVICE = 3; // 水浸
    public static final int WS_SERVICE = 4; // 门窗
    public static final int MS_SERVICE = 5; // 人体
    public static final int CURTAIN_SERVICE = 6; // 窗帘

    /**
     * 是否有开关
     *
     * @param type
     * @return
     */
    public static boolean isSwitch(String type) {
        if (!TextUtils.isEmpty(type) && ((type.equals(WSConstant.LIGHT_BULB) || type.equals(WSConstant.LIGHT)
                || type.equals(WSConstant.OUT_LET) || type.equals(WSConstant.WALL_PLUG) || type.equals(WSConstant.SWITCH)
                || type.equals(WSConstant.THREE_KEY_SWITCH) || type.equals(WSConstant.WIRELESS_SWITCH)))) {
            return true;
        }
        return false;
    }

    /**
     * service的类型
     *
     * @param serviceType
     * @return
     */
    public static int getServiceType(String serviceType) {
        switch (serviceType) {
            case WSConstant.LIGHT_BULB:
            case WSConstant.LIGHT:
            case WSConstant.OUT_LET:
            case WSConstant.WALL_PLUG:
            case WSConstant.SWITCH:
            case WSConstant.THREE_KEY_SWITCH:
            case WSConstant.WIRELESS_SWITCH:
                return SWITCH_SERVICE;

            case WSConstant.TEMP_SENSOR:
            case WSConstant.HUMIDITY_SENSOR:
                return TH_SERVICE;

            case WSConstant.WATER_LEAK_SENSOR:
                return LEAK_SERVICE;

            case WSConstant.WINDOW_DOOR_SENSOR:
                return WS_SERVICE;

            case WSConstant.MOTION_SENSOR:
                return MS_SERVICE;

            case WSConstant.CURTAIN:
                return CURTAIN_SERVICE;
        }
        return 0;
    }
}
