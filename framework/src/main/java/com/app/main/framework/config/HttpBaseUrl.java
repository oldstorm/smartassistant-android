package com.app.main.framework.config;

import com.app.main.framework.baseutil.LibLoader;

public class HttpBaseUrl {
//    public static final String HTTPS = "http";
    public static final String HTTPS = "https";
    public static final String API = "api";

    //public static final String VERSION = "";//不使用版本数据库版本
    public static final String V = "v";
    public static final String VERSION_NUM = "2.0.0";
    public static final String VERSION = V+VERSION_NUM +"/";//数据库版本

    public static String baseSCHost = "gz.sc.zhitingtech.com";//线上服务SC域名
//    public static String baseSCHost = "scgz.zhitingtech.com";//测试服务SC域名
    //public static String baseSCHost = "192.168.22.114:8082";//伟业SC域名
//    public static String baseSCHost = "192.168.22.169:8082";//陈弈聪SC域名

    public static String baseSCUrl = HTTPS + "://" + baseSCHost;//测试服务SC地址
    public static String baseSCUrlApi = baseSCUrl + "/" + API;//测试服务SC地址

    public static String baseSAUrl = "";//测试服务器SA
    //public static String baseSAUrl = "http://192.168.22.114:37965";//伟业服务器SA
}
