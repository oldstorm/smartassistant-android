package com.yctc.zhiting.entity;

/**
 * 设置服务器配置
 */
public class ServerConfigBean {

    /**
     * server : www.zt.com
     * port : 8081
     * access_token : 123456
     * area_id : 111111
     */

    private String server;
    private int port;
    private String access_token;
    private String area_id;

    public ServerConfigBean() {
    }

    public ServerConfigBean(String server, int port, String access_token, String area_id) {
        this.server = server;
        this.port = port;
        this.access_token = access_token;
        this.area_id = area_id;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }
}
