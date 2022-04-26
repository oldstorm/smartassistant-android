package com.app.main.framework.httputil;

import android.app.Activity;

import com.app.main.framework.NetworkErrorConstant;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.app.main.framework.event.AccountCancelledEvent;
import com.app.main.framework.event.FourZeroFourEvent;
import com.app.main.framework.event.LoginInvalidEvent;
import com.app.main.framework.event.PwdModifiedEvent;
import com.app.main.framework.gsonutils.GsonConverter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public abstract class RequestDataCallback<T> {
    //这个是网络返回的
    private List<Integer> successCode = Arrays.asList(200, 201, 204);

    //返回http状态、json对象和http原始数据
    void dataCallback(int status, T obj, byte[] body, String url) {
        String bodyContent = new String(body, Charset.forName("UTF-8"));
        LogUtil.e("RequestDataCallback1=status=" + status + "," + GsonConverter.getGson().toJson(obj) + ",body=" + bodyContent);
        Activity currentActivity = LibLoader.getCurrentActivity();
        if (currentActivity instanceof BaseActivity) {
            ((BaseActivity) currentActivity).hideLoadingView();
        }
        if (status == -1 || status == 404) {
            onFailed(status, status == 404 ? bodyContent : "", status == 404 ? url : "");
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
            if (status!=NetworkErrorConstant.NO_START_WITH_HTTP) {
                onFailed(status, "服务器异常");
                LogUtil.e("printStackTrace==" + e.getMessage());
            }

        }
    }

    public void onSuccess(T obj) {
    }

    public void onFailed(int errorCode, String errorMessage) {
        // 登录失效
        if (errorCode == NetworkErrorConstant.LOGIN_INVALID_1 || errorCode == NetworkErrorConstant.LOGIN_INVALID_2) {
            EventBus.getDefault().post(new LoginInvalidEvent());
        }

        if (errorCode == NetworkErrorConstant.ACCOUNT_CANCELLED) {
            EventBus.getDefault().post(new AccountCancelledEvent());
        }

        // 密码被修改了
        if (errorCode == NetworkErrorConstant.PWD_MODIFIED) {
            EventBus.getDefault().post(new PwdModifiedEvent());
        }
    }

    public void onFailed(int errorCode, String errorMessage, String url) {
        LogUtil.e("错误码："+errorCode + "  " + errorMessage);
        // 访问不了
        if (errorCode == 404){
            EventBus.getDefault().post(new FourZeroFourEvent(errorMessage, url));
        }
        onFailed(errorCode, errorCode == 404 ? "" : errorMessage);

    }
}
