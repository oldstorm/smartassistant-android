package com.yctc.zhiting.entity;

public class GetDeviceInfoBean {

    /**
     * id : xxx
     * result : {"model":"ZTSW3SLW001W","token":"3ECEF3DE05592EA5E3B515304211527A","sw_ver":"1.0.0","hw_ver":"esp32c3"}
     */

    private long id;
    private ResultBean result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * model : ZTSW3SLW001W
         * token : 3ECEF3DE05592EA5E3B515304211527A
         * sw_ver : 1.0.0
         * hw_ver : esp32c3
         */

        private String model;
        private String token;
        private String sw_ver;
        private String hw_ver;
        private String port;
        private String sa_id;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getSw_ver() {
            return sw_ver;
        }

        public void setSw_ver(String sw_ver) {
            this.sw_ver = sw_ver;
        }

        public String getHw_ver() {
            return hw_ver;
        }

        public void setHw_ver(String hw_ver) {
            this.hw_ver = hw_ver;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getSa_id() {
            return sa_id;
        }

        public void setSa_id(String sa_id) {
            this.sa_id = sa_id;
        }
    }
}
