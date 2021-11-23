package com.yctc.zhiting.entity;

public class ScanDeviceByUDPBean {

    private String host;  // 地址
    private int port;  // 端口
    private String deviceId;  // 设备id
    private String password;  // 密码
    private String token;  // token
    private long id;  // 发送获取设备信息时的id
    private GetDeviceInfoBean deviceInfoBean; // 设备信息
    private ServerConfigBean serverConfigBean; // 设置服务器配置

    public ScanDeviceByUDPBean() {
    }

    public ScanDeviceByUDPBean(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ScanDeviceByUDPBean(String host, int port, String deviceId) {
        this.host = host;
        this.port = port;
        this.deviceId = deviceId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GetDeviceInfoBean getDeviceInfoBean() {
        return deviceInfoBean;
    }

    public void setDeviceInfoBean(GetDeviceInfoBean deviceInfoBean) {
        this.deviceInfoBean = deviceInfoBean;
    }

    public ServerConfigBean getServerConfigBean() {
        return serverConfigBean;
    }

    public void setServerConfigBean(ServerConfigBean serverConfigBean) {
        this.serverConfigBean = serverConfigBean;
    }
}
