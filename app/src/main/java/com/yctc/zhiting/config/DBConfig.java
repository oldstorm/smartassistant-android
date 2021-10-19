package com.yctc.zhiting.config;

public class DBConfig {

    public static final String USER_KEY = "zhiting";
    public static final String DB_NAME = "zhiting.db";//数据库名
    public static final int DB_VERSION = 1;//数据库版本YeXinLib/src/main/java/com/yexinlib/db/DBHelper.java

    public static final String TABLE_HOME_COMPANY = USER_KEY + "_T_HomeCompany_";//家庭/公司表格
    public static final String TABLE_USER_INFO = USER_KEY + "T_UserInfo";//成员信息表格
    public static final String TABLE_ROOM_AREA = USER_KEY + "_T_RoomArea_";//房间区域表格 废弃
    public static final String TABLE_LOCATION = USER_KEY + "_T_Location_";//房间区域表格
    public static final String TABLE_DEVICE = USER_KEY + "_T_Device_";//设备表格
    public static final String TABLE_SCENE = USER_KEY + "_T_Scene_";//场景表格
    public static final String DataBaseName = "Sqlite_master";


    public static final String TABLE_RECENT_CONTACT = USER_KEY + "_T_RecentContact_";//最近联系人表名
    public static final String TABLE_MSG = USER_KEY + "_T_Message_";//聊天消息表格
}

