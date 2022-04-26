package com.yctc.zhiting.config;

import android.net.wifi.WifiInfo;
import android.os.Environment;

import com.app.main.framework.config.HttpBaseUrl;
import com.yctc.zhiting.activity.DeviceDetailActivity;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Constant {
    public static int USER_ID = 1;//用户id
    public static final String EQUAL = "=";
    public static final String SWITCH = "switch";  // 开关
    public static final String set_bright = "set_bright"; // 设置亮度
    public static final String set_color_temp = "set_color_temp";  // 设置色温
    public static final String ON = "on";  // 打开
    public static final int DOU_ON = 1;  // 打开
    public static final String OFF = "off";  // 关闭
    public static final String TOGGLE = "toggle";  // 切换开关
    public static final String POWER = "power";  // 开关
    public static final String color_temp = "color_temp";  // 色温
    public static final String brightness = "brightness";  // 亮度
    public static final String window_door_close = "contact_sensor_state";  // 门窗 1 关闭变打开 0 打开变关闭
    public static final String detected = "motion_detected";  // 人体传感器
    public static final String powers_1 = "powers_1";  // 一键
    public static final String powers_2 = "powers_2";  // 二键
    public static final String powers_3 = "powers_3";  // 三键
    public static final String leak_detected = "leak_detected";  // 水浸
    public static final String temperature = "temperature";  // 温度
    public static final String humidity = "humidity";  // 湿度
    public static final String rgb = "rgb";  // 色彩
    public static final String target_state = "target_state";  // 色彩
    public static final String target_position = "target_position";  // 色彩
    public static final String switch_event = "switch_event";  // 色彩
    public static final String ON_OFF = "on_off";  // 色彩
    public static final int STATUE_1 = 1;
    public static final int STATUE_0 = 0;
    public static String START = "start";
    public static String SIZE = "size";
    public static String PLUGIN = "plugin";// WebSocket 操作插件
    public static String INSTALL = "install"; // WebSocket 安装插件
    public static String UPDATE = "update";// WebSocket 更新插件
    public static String REMOVE = "remove";// WebSocket 删除插件
    public static String CLOUD_USER = "cloud_user";
    public static String STR = "string";
    public static String INT_STR = "int";

    public static final String ONLY_SC = "&type=only_sc";//有这个标签的不要请求临时通道的接口
    public static final String NO_AREA_ID = "&type=no_area_id";//去掉area_id
    public static WifiInfo wifiInfo = null;//无线网络对象WifiInfo
    public static HomeCompanyBean CurrentHome = null;//当前家庭对象
    public static int AREA_TYPE = 1;//当前家庭对象 1家庭 2公司
    public static final String HTTPS = "https";
    public static final String HTTP = "http";
    public static final String HTTP_HEAD = HTTP + "://";
    public static final String HTTPS_HEAD = HTTPS + "://";
    public static final String HOMEKIT = "homekit";
    public static final String SMART_ASSISTANT = "smart_assistant";
    public static final String PLUGIN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/zhiting/plugins/"; // 下周插件地址
    public static final String FIND_DEVICE_URL = "255.255.255.255"; // hello 数据包地址
    public static final int FIND_DEVICE_PORT = 54321; // hello数据包端口
    public static final String CONFIG_DEVICE_TO_SERVER_ADDRESS = "scgz.zhitingtech.com";  // 配网服务器地址
    public static final String ZHITING_APP = "zhitingApp";//js交互别名
    public static final String ZHITING_USER_AGENT = "zhitingua-android";//webview设置代理后缀
    public static final String AGREED = "agreed";// 同意用户协议和隐私政策
    public static final String GUIDED = "guided";// 同意用户协议和隐私政策
    public static final String AGREEMENT_URL = HttpBaseUrl.baseSCUrl + "/smartassitant/protocol/user/";  // 用户协议
    public static final String POLICY_URL = HttpBaseUrl.baseSCUrl + "/smartassitant/protocol/privacy/";  // 隐私政策
    public static final String THIRD_PLATFORM = "/#/third-platform";  // 第三方平台
    public static final String OFFLINE_HELP = "/#/help/out-line?type=sa";  // 离线帮助

    public static final String DOT_ZIP = ".zip";
    public static final int HOME_MODE = 1; // 家庭
    public static final int COMPANY_MODE = 2; // 公司
    public static final String CRM_MIDDLE_URL = "/crm/#/?crmToken=";  // CRM中间地址
    public static final String SCM_MIDDLE_URL = "/scm/#/?scmToken=";  // SCM中间地址
    public static final int OPEN_FILE = 100;
    public static boolean isUnregisterSuccess;//是否注销成功

    public static final String CLIENT = "client";
    public static final String APP_TYPE = "app_type";
    public static final String ZHI_TING = "zhiting";
    public static final String TYPE = "type";
    public static final String TARGET = "target";
    public static final String COUNTRY_CODE = "country_code";
    public static final String REGISTER = "register";
    public static final String FORGET_PWD = "forget_password";
    public static final String UNREGISTER = "unregister";
    public static final String LOGIN = "login";
    public static final String WANGPAN = "wangpan";
    public static final String ANDROID = "android";
    public static final String MQTT = "mqtt";
    public static final String CLOUD = "cloud";
    public static final String CONFIG_SERVER_PORT = ":1883";
    public static final String FILE_HEAD = "file://";
    public static final String SEPARATOR = "/";
    public static final String PLUGINS_PATH = "/plugins/";
    public static final String VIDEO = "video";


    public static long mSendId = 0;

    // 上传头像
    public static final String FILE_UPLOAD = "file_upload";
    public static final String FILE_HASH = "file_hash";     //文件sha256
    public static final String FILE_TYPE = "file_type";  // 用户上传头像填img
    public static final String FILE_AUTH = "file_auth"; // 1公有服务|2私有服务 (本地服务默认为1公有服务）
    public static final String FILE_SERVER = "file_server"; // 1本地服务|2云服务
    public static final String IMG = "img";
    public static final String PUB_SERVICE = "1";
    public static final String PRI_SERVICE = "2";
    public static final String LOCAL_SERVICE = "1";
    public static final String CLOUD_SERVICE= "2";
    public static final String FEEDBACK= "feedback";

    public static final String MH_SA = "MH-SA";
    public static final String VERSION = "version";
    public static final String TIMEOUT = "timeout";
    public static final String AREA_ID = "area_id";
    public static final String ERROR_BSSID = "02:00:00:00:00:00";

    public static LinkedList<DeviceDetailActivity> devDetailList = new LinkedList<>();

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
        String HOMEKIT_TYPE_ON = "1.0"; //打开
        String TYPE_OFF = "off"; //关闭
        String HOMEKIT_TYPE_OFF = "0.0"; //关闭
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
     * 更新类型
     */
    public interface UpdateType {
        // 不需操作
        int NONE = 0;
        // 普通更新
        int ORDINARY = 1;
        // 强制更新
        int FORCE = 2;
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
            "        callBack: function (callBackID, data, noFire) {\n" +
            "            WKBridgeEvent.fireEvent(callBackID, data);\n" +
            "          if (!noFire) {" +
            "            WKBridgeEvent.removeEvent(callBackID);\n" +
            "          }" +
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
