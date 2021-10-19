package com.yctc.zhiting.config;

import android.net.wifi.WifiInfo;

import com.yctc.zhiting.entity.mine.HomeCompanyBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constant {
    public static final boolean ANTI_ALIAS = true;
    public static final int DEFAULT_SIZE = 150;
    public static final int DEFAULT_START_ANGLE = 270;
    public static final int DEFAULT_SWEEP_ANGLE = 360;
    public static final int DEFAULT_ANIM_TIME = 1000;
    public static final int DEFAULT_MAX_VALUE = 100;
    public static final int DEFAULT_VALUE = 50;
    public static final int DEFAULT_HINT_SIZE = 15;
    public static final int DEFAULT_UNIT_SIZE = 30;
    public static final int DEFAULT_VALUE_SIZE = 15;
    public static final int DEFAULT_ARC_WIDTH = 15;
    public static final int DEFAULT_WAVE_HEIGHT = 40;
    public static int USER_ID = 1;//用户id
    public static HomeCompanyBean CurrentHome = null;//当前家庭对象
    public static WifiInfo wifiInfo = null;//无线网络对象

    public static String EQUAL = "=";
    public static String LESS = "<";
    public static String GREATER = ">";

    public static String SWITCH = "switch";
    public static String set_bright = "set_bright";
    public static String set_color_temp = "set_color_temp";
    public static String ON = "on";
    public static String OFF = "off";
    public static String TOGGLE = "toggle";
    public static String POWER = "power";
    public static String color_temp = "color_temp";
    public static String brightness = "brightness";

    public static String START = "start";
    public static String SIZE = "size";

    // WebSocket 操作插件
    public static String PLUGIN = "plugin";
    // WebSocket 安装插件
    public static String INSTALL = "install";
    // WebSocket 更新插件
    public static String UPDATE = "update";
    // WebSocket 删除插件
    public static String REMOVE = "remove";

    public static String CN_CODE = "86";
    public static String CLOUD_USER = "cloud_user";

    public static String STR = "string";
    public static String INT_STR = "int";

    public static final String USER = "user";
    public static final String AREA = "area";

    /**
     * 智能设备类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface DeviceType {
        String TYPE_LIGHT = "light"; //灯
        String TYPE_SWITCH = "switch"; //开关
        String TYPE_SA = "sa"; //智能中心sa
    }

    /**
     * 添加设备状态
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConnectType {
        int TYPE_SUCCESS = 1; //成功
        int TYPE_CONNECTING = 0; //连接中
        int TYPE_FAILED = -1; //失败
    }

    /**
     * 开关的状态
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface PowerType {
        String TYPE_ON = "on"; //打开
        String TYPE_OFF = "off"; //关闭
        String TYPE_NULL = ""; //关闭
    }

    /**
     * domain 类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface DomainType {
        String TYPE_PLUGIN = "plugin"; //设备功能
        String TYPE_YEELIGHT = "yeelight"; //设备信息
    }

    /**
     * service 类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceType {
        String TYPE_GET_ACTIONS = "get_actions";
        String TYPE_SWITCH = "switch";
        String TYPE_STATE = "state";
    }

    /**
     * 指令类型
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface DirectType {
        int TYPE_FUNCTION = 1; //设备功能
        int TYPE_INFO = 2; //设备信息
    }


    /**
     * 属性
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface Attribute {
        String POWER = "power"; //灯
        String COLOR_TEMP = "color_temp"; //开关
        String BRIGHTNESS = "brightness"; //智能中心sa
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageType {
        String DEVICE_INFO = "device_info"; //设备信息
        String DEVICE_SWITCH = "device_switch"; //开关
    }

    public static String NETWORK_TYPE = "networkType";
    public static String GET_USER_INFO = "getUserInfo";
    public static String SET_TITLE = "setTitle";
    public static String IS_PROFESSION = "isProfession";

    /**
     * 专业版js代码
     */
    public static String professional_js = "var zhiting = {\n" +
            "\n" +
            "        invoke: function (funcName, params, callback) {\n" +
            "            var message;\n" +
            "            var timeStamp = new Date().getTime();\n" +
            "            var callbackID = funcName + '_' + timeStamp + '_' + 'callback';\n" +
            "            \n" +
            "            if (callback) {\n" +
            "                if (!WKBridgeEvent._listeners[callbackID]) {\n" +
            "                    WKBridgeEvent.addEvent(callbackID, function (data) {\n" +
            "\n" +
            "                        callback(data);\n" +
            "\n" +
            "                    });\n" +
            "                }\n" +
            "            }\n" +
            "\n" +
            "\n" +
            "\n" +
            "            if (callback) {\n" +
            "                message = { 'func': funcName, 'params': params, 'callbackID': callbackID };\n" +
            "\n" +
            "            } else {\n" +
            "                message = { 'func': funcName, 'params': params };\n" +
            "\n" +
            "            }\n" +
            "            zhitingApp.entry(JSON.stringify(message));\n" +
            "        },\n" +
            "\n" +
            "        callBack: function (callBackID, data) {\n" +
            "            WKBridgeEvent.fireEvent(callBackID, data);\n" +
            "            WKBridgeEvent.removeEvent(callBackID);\n" +
            "        },\n" +
            "\n" +
            "        removeAllCallBacks: function (data) {\n" +
            "            WKBridgeEvent._listeners = {};\n" +
            "        }\n" +
            "\n" +
            "    };\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "    var WKBridgeEvent = {\n" +
            "\n" +
            "        _listeners: {},\n" +
            "\n" +
            "        addEvent: function (callBackID, fn) {\n" +
            "            this._listeners[callBackID] = fn;\n" +
            "            return this;\n" +
            "        },\n" +
            "\n" +
            "\n" +
            "        fireEvent: function (callBackID, param) {\n" +
            "            var fn = this._listeners[callBackID];\n" +
            "            if (typeof callBackID === \"string\" && typeof fn === \"function\") {\n" +
            "                fn(JSON.parse(param));\n" +
            "            } else {\n" +
            "                delete this._listeners[callBackID];\n" +
            "            }\n" +
            "            return this;\n" +
            "        },\n" +
            "\n" +
            "        removeEvent: function (callBackID) {\n" +
            "            delete this._listeners[callBackID];\n" +
            "            return this;\n" +
            "        }\n" +
            "    };";
}
