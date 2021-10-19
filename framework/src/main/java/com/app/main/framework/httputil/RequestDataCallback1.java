package com.app.main.framework.httputil;

import android.app.Activity;
import android.text.TextUtils;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.app.main.framework.entity.HttpResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public abstract class RequestDataCallback1<T> {
    private List<Integer> successCode = Arrays.asList(200, 201, 204);

    //返回http状态、json对象和http原始数据
    void dataCallback(int status, T obj, byte[] body) {
        LogUtil.e("dataCallback01="+status);
        LogUtil.e("dataCallback02="+new Gson().toJson(obj));
        LogUtil.e("dataCallback03="+body);
        Activity currentActivity = LibLoader.getCurrentActivity();
        if (currentActivity instanceof BaseActivity) {
            ((BaseActivity) currentActivity).hideLoadingView();
        }
        String json = null;
        JSONArray jsonArray = null;
        try {
            json = new String(body, StandardCharsets.UTF_8);
            jsonArray = new JSONArray(json);
            LogUtil.e("dataCallback1="+json);
            LogUtil.e("dataCallback11="+jsonArray.toString());
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        if (jsonArray != null) {
            onSuccess(json);
        } else {
            if (successCode.contains(status))
                onSuccess(obj);
            else {
                LogUtil.e("dataCallback2="+new Gson().toJson(obj));
                if (obj != null || !TextUtils.isEmpty(json)) {
                    if (obj instanceof HttpResult) {
                        HttpResult result = (HttpResult) obj;
                        resultFailed(result);
                    } else {
                        try {
                            HttpResult httpResult = new Gson().fromJson(json, HttpResult.class);
                            resultFailed(httpResult);
                        } catch (JsonSyntaxException e) {
                            onFailed(-1, UiUtil.getString(R.string.network_params_error));
                        }
                    }
                } else {
                    onFailed(-1, UiUtil.getString(R.string.network_params_error));
                }
            }
        }
    }

    private void resultFailed(HttpResult result) {
        String message = result.getMessage();
        int code = result.getCode();
        onFailed(code, message);
    }

    public void onSuccess(T obj) {
    }

    public void onFailed(int errorCode, String errorMessage) {
    }

    public void onSuccess(String datas) {
    }

    public <T> List<T> stringToArray(String s, Type type) {
        return new Gson().fromJson(s, type);
    }
}
