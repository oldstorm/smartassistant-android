package com.app.main.framework.gsonutils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * IntegerDeserializer 反序列化时 无法解析的Int字段都返回 0
 */
public class IntegerDeserializer implements JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return Integer.parseInt(json.getAsString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

