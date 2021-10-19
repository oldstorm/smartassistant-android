package com.app.main.framework.gsonutils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * LongDeserializer 反序列化时 无法解析的Double字段都返回 0
 */
public class LongDeserializer implements JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return Long.parseLong(json.getAsString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
