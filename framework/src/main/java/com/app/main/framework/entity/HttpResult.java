package com.app.main.framework.entity;

public class HttpResult extends BaseEntity {
    private String data;

    public String getData() {
        return data;
    }

    public HttpResult setData(String data) {
        this.data = data;
        return this;
    }
}
