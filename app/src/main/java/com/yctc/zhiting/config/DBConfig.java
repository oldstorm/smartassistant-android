package com.yctc.zhiting.config;

public class DBConfig {

    public static final String USER_KEY = "zhiting_";
    public static final String DB_NAME = "zhiting.db";//数据库名
    public static final int DB_VERSION = 2;//数据库版本src/main/java/com/yexinlib/db/DBHelper.java

    public static final String TABLE_HOME_COMPANY = USER_KEY + "home_company";//家庭/公司表格
    public static final String TABLE_USER_INFO = USER_KEY + "user_info";//成员信息表格
    public static final String TABLE_ROOM_AREA = USER_KEY + "room_area";//房间区域表格 废弃
    public static final String TABLE_LOCATION = USER_KEY + "location";//房间区域表格
    public static final String TABLE_DEVICE = USER_KEY + "devices";//设备表格
    public static final String TABLE_SCENE = USER_KEY + "scene";//场景表格
    public static final String DataBaseName = "sqlite_master";
}

