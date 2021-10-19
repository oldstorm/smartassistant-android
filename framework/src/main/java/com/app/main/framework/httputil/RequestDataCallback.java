package com.app.main.framework.httputil;

import android.app.Activity;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public abstract class RequestDataCallback<T> {
    //这个是网络返回的
    private List<Integer> successCode = Arrays.asList(200, 201, 204);

    //返回http状态、json对象和http原始数据
    void dataCallback(int status, T obj, byte[] body) {
        LogUtil.e("RequestDataCallback1=status=" + status + "," + GsonConverter.getGson().toJson(obj) + ",body=" + body.toString());
        Activity currentActivity = LibLoader.getCurrentActivity();
        if (currentActivity instanceof BaseActivity) {
            ((BaseActivity) currentActivity).hideLoadingView();
        }
        if (status == -1) {
            onFailed(-1, "");
            return;
        }
        try {
            String json = new String(body, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);

            int code = jsonObject.getInt("status");
            String message = jsonObject.getString("reason");

            LogUtil.e("dataCallback=code=" + code+",message="+message);
            if (json.contains("\"data\"")) {
                String data = jsonObject.getString("data");
                LogUtil.e("dataCallback=data=" + data);
            }
            if (successCode.contains(status)) {
                if (code == 0) {
                    onSuccess(obj);
                } else {
                    onFailed(code, message);
                }
            } else {
                onFailed(code, message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailed(status, "服务器异常");
            LogUtil.e("printStackTrace==" + e.getMessage());
        }
    }

    public void onSuccess(T obj) {
    }

    public void onFailed(int errorCode, String errorMessage) {
    }
}
