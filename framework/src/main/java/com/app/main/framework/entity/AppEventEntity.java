package com.app.main.framework.entity;

import java.util.List;

/**
 * on 2019/3/30
 */
public class AppEventEntity<T> {
    private String key;
    private String value;
    private String[] values;

    public String[] getValues() {
        return values;
    }

    public AppEventEntity<T> setValues(String[] values) {
        this.values = values;
        return this;
    }

    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public AppEventEntity<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    public String getKey() {
        return key;
    }

    public AppEventEntity setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public AppEventEntity setValue(String value) {
        this.value = value;
        return this;
    }

    public AppEventEntity() {

    }
    public AppEventEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public AppEventEntity(String key, String... value) {
        this.key = key;
        this.values = value;
    }

    public AppEventEntity(String key) {
        this.key = key;
    }
}
