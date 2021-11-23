package com.yctc.zhiting.entity.home;

public class AccessTokenBean {

    private String access_token; // access_token 令牌
    private String expires_in; // 令牌存活时间（单位秒）

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }
}
