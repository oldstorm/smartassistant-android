package com.yctc.zhiting.entity;

/**
 * 设置服务器配置返回结果
 */
public class ConfigServerResultBean {
    private long id;
    private ServerConfigBean result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ServerConfigBean getResult() {
        return result;
    }

    public void setResult(ServerConfigBean result) {
        this.result = result;
    }
}
