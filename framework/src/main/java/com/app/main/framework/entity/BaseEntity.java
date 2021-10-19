package com.app.main.framework.entity;

import androidx.annotation.NonNull;

import com.app.main.framework.gsonutils.GsonConverter;

import java.io.Serializable;

public class BaseEntity implements Serializable {
    public int code;
    public String message;

    public int getCode() {
        return code;
    }

    public BaseEntity setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BaseEntity setMessage(String message) {
        this.message = message;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        String json = GsonConverter.getGson().toJson(this);
        return json;
    }
}
