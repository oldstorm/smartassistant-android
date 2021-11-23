package com.yctc.zhiting.entity.mine;

import com.app.main.framework.httputil.request.Request;

public class SoftWareVersionBean extends Request {
    private String version;//当前版本
    private String latest_version;//最新版本

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLatest_version() {
        return latest_version;
    }

    public void setLatest_version(String latest_version) {
        this.latest_version = latest_version;
    }
}
