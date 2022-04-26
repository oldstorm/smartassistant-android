package com.yctc.zhiting.entity;

public class JsBean {

    /**
     * func : funcName
     * params : params
     * callbackID : callbackID
     */

    private String func;
    private JsSonBean params;
    private String callbackID;

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public JsSonBean getParams() {
        return params;
    }

    public void setParams(JsSonBean params) {
        this.params = params;
    }

    public String getCallbackID() {
        return callbackID;
    }

    public void setCallbackID(String callbackID) {
        this.callbackID = callbackID;
    }

    public static class JsSonBean{

        /**
         * title : 标题名称
         * color : #333333
         * background : #ffffff
         * isShow : true
         */

        private String title; // 标题名称
        private String color; // 标题颜色
        private String background; // 标题栏背景色
        private Boolean isShow;  // 是否显示标题栏

        private String bluetoothName; // 蓝牙名称
        private String wifiName; // WiFi名称
        private String wifiPass; // WiFi密码
        private String hotspotName; // 设备的热点名称
        private String ownership; // 设备拥有权 默认: abcd1234
        private String url; // 开发者服务器 wss 接口地址
        private HeaderBean header; // ws 请求头 不传使用默认
        private Object data;

        private String control; // 相对路径
        private String plugin_id; // 设备plugin_id


        // wenbsocket发送的内容
        private long id;
        private String domain;
        private String service;
        private String identity;

        private String token; // websocket token

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public boolean isIsShow() {
            return isShow;
        }

        public void setIsShow(Boolean isShow) {
            this.isShow = isShow;
        }

        public String getBluetoothName() {
            return bluetoothName;
        }

        public void setBluetoothName(String bluetoothName) {
            this.bluetoothName = bluetoothName;
        }

        public String getWifiName() {
            return wifiName;
        }

        public void setWifiName(String wifiName) {
            this.wifiName = wifiName;
        }

        public String getWifiPass() {
            return wifiPass;
        }

        public void setWifiPass(String wifiPass) {
            this.wifiPass = wifiPass;
        }

        public String getHotspotName() {
            return hotspotName;
        }

        public void setHotspotName(String hotspotName) {
            this.hotspotName = hotspotName;
        }

        public String getOwnership() {
            return ownership;
        }

        public void setOwnership(String ownership) {
            this.ownership = ownership;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public HeaderBean getHeader() {
            return header;
        }

        public void setHeader(HeaderBean header) {
            this.header = header;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getControl() {
            return control;
        }

        public void setControl(String control) {
            this.control = control;
        }

        public String getPlugin_id() {
            return plugin_id;
        }

        public void setPlugin_id(String plugin_id) {
            this.plugin_id = plugin_id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class HeaderBean {
        String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
