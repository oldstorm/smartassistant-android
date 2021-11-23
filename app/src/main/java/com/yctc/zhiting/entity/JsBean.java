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
        private boolean isShow;  // 是否显示标题栏

        private String bluetoothName; // 蓝牙名称
        private String wifiName; // WiFi名称
        private String wifiPass; // WiFi密码
        private String hotspotName; // 设备的热点名称
        private String ownership; // 设备拥有权 默认: abcd1234

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

        public void setIsShow(boolean isShow) {
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
    }
}
