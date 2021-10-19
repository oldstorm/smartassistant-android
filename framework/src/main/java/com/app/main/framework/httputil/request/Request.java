package com.app.main.framework.httputil.request;

import androidx.annotation.NonNull;

import com.app.main.framework.gsonutils.GsonConverter;

import java.io.Serializable;

public class Request implements Serializable {
    @NonNull
    @Override
    public String toString() {
        String json = GsonConverter.getGson().toJson(this);
        return json;
    }
}