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

        private String title;
        private String color;
        private String background;
        private boolean isShow;


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
    }
}
