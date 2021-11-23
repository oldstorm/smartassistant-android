package com.yctc.zhiting.config;

import android.net.wifi.WifiInfo;
import android.os.Environment;

import com.yctc.zhiting.entity.mine.HomeCompanyBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constant {
    public static int USER_ID = 1;//用户id
    public static String EQUAL = "=";
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
    public static String PLUGIN = "plugin";// WebSocket 操作插件
    public static String INSTALL = "install"; // WebSocket 安装插件
    public static String UPDATE = "update";// WebSocket 更新插件
    public static String REMOVE = "remove";// WebSocket 删除插件
    public static String CN_CODE = "86";
    public static String CLOUD_USER = "cloud_user";
    public static String CLOUD_USER_ID = "cloud_user_id";
    public static String STR = "string";
    public static String INT_STR = "int";

    public static final String USER = "user";
    public static final String AREA = "area";
    public static final String ONLY_SC = "&type=only_sc";//有这个标签的不要请求临时通道的接口
    public static final String NO_AREA_ID = "&type=no_area_id";//去掉area_id

    public static WifiInfo wifiInfo = null;//无线网络对象
    public static HomeCompanyBean CurrentHome = null;//当前家庭对象
    public static final String HTTPS = "https";
    public static final String HTTP = "http";
    public static final String HTTP_HEAD = HTTP+"://";
    public static final String HTTPS_HEAD = HTTPS+"://";

    public static final String HOMEKIT = "homekit";
    public static final String PIN = "pin";

    public static final String SMART_ASSISTANT = "smart_assistant";

    public static final String PLUGIN_PATH =  Environment.getExternalStorageDirectory().getAbsolutePath()+ "/zhiting/plugins/";

    public static final String FIND_DEVICE_URL = "255.255.255.255";
    public static final int FIND_DEVICE_PORT = 54321;
    public static final String CONFIG_DEVICE_TO_SERVER_ADDRESS = "sacloudgz.zhitingtech.com";
    public static final String ZHITING_APP = "zhitingApp";
    public static final String ZHITING_USER_AGENT = "zhitingua";

    public static final String AGREED = "agreed"; // 同意用户协议和隐私政策

    public static final String ABOUT_US_URL = "https://www.baidu.com";
    public static final String AGREEMENT_URL = "https://scgz.zhitingtech.com/smartassitant/protocol/user";
    public static final String POLICY_URL = "https://scgz.zhitingtech.com/smartassitant/protocol/privacy";
    public static final String DOT_ZIP = ".zip";

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
     * 属性
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface Attribute {
        String POWER = "power"; //灯
        String COLOR_TEMP = "color_temp"; //开关
        String BRIGHTNESS = "brightness"; //智能中心sa
    }

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
