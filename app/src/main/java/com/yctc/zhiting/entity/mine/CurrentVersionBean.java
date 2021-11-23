package com.yctc.zhiting.entity.mine;

import com.app.main.framework.httputil.request.Request;

public class CurrentVersionBean extends Request {
    private String version;//当前版本
    private boolean is_bind;//是否已被绑定

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isIs_bind() {
        return is_bind;
    }

    public void setIs_bind(boolean is_bind) {
        this.is_bind = is_bind;
    }
}
