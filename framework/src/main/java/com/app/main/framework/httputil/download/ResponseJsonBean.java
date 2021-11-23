package com.app.main.framework.httputil.download;

public class ResponseJsonBean {

    /**
     * status : 3002
     * reason : 该家庭不存在
     */

    private int status;
    private String reason;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
