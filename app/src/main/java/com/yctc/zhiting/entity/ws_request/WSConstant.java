package com.yctc.zhiting.entity.ws_request;

public class WSConstant {
    public static final String DISCOVER = "discover";
    public static final String DOMAIN_ZHITING = "zhiting";
    public static final String SERVICE_CONNECT = "connect";
    public static final String SERVICE_DISCONNECT = "disconnect";
    public static final String SERVICE_GET_INSTANCE = "get_instances";
    public static final String SERVICE_SET_ATTRIBUTES = "set_attributes";

    public static final String EVENT = "event";
    public static final String RESPONSE = "response";
    public static final String ATTRIBUTE_CHANGE = "attribute_change";

    public static final String LIGHT_BULB = "light_bulb";
    public static final String LIGHT = "light";
    public static final String OUT_LET = "outlet";
    public static final String SWITCH = "switch";
    public static final String WATER_LEAK_SENSOR = "leak_sensor"; // 水浸传感器
    public static final String WINDOW_DOOR_SENSOR = "contact_sensor"; // 门窗传感器
    public static final String MOTION_SENSOR = "motion_sensor"; // 人体传感器
    public static final String TEMP_HUMIDITY_SENSOR = "temp_humidity_sensor"; // 温湿度传感器
    public static final String TEMP_SENSOR = "temperature_sensor"; // 温度传感器
    public static final String HUMIDITY_SENSOR = "humidity_sensor"; // 湿度传感器
    public static final String CURTAIN = "curtain"; // 窗帘
    public static final String WALL_PLUG = "wall_plug"; // 墙壁插座
    public static final String THREE_KEY_SWITCH = "three_key_switch"; // 三键开关
    public static final String WIRELESS_SWITCH = "wireless_switch"; // 无状态开关

    public static final String ATTR_ON_OFF = "on_off"; // 属性是开关
    public static final String ATTR_LEAK_DETECTED = "leak_detected"; // 属性是水浸传感器
    public static final String ATTR_WIDOW_DOOR_CLOSE = "contact_sensor_state"; // 属性是门窗传感器
    public static final String ATTR_DETECTED = "motion_detected"; // 属性是人体传感器
    public static final String ATTR_TEMPERATURE = "temperature"; // 属性是温度
    public static final String ATTR_HUMIDITY = "humidity"; // 属性是湿度
    public static final String ATTR_CURRENT_POSITION = "current_position"; // 属性是窗帘位置
}
