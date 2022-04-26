package com.yctc.zhiting.entity;

public class KeyValBean {

    private String key;
    private String value;

    public KeyValBean(String key) {
        this.key = key;
    }

    public KeyValBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
