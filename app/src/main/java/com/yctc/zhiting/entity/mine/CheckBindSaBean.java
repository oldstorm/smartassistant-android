package com.yctc.zhiting.entity.mine;

public class CheckBindSaBean {
    private boolean is_bind;
    private String version;
    private String min_version;

    public boolean isIs_bind() {
        return is_bind;
    }

    public void setIs_bind(boolean is_bind) {
        this.is_bind = is_bind;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMin_version() {
        return min_version;
    }

    public void setMin_version(String min_version) {
        this.min_version = min_version;
    }
}
