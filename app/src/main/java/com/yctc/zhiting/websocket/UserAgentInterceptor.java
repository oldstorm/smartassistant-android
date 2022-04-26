package com.yctc.zhiting.websocket;

import android.text.TextUtils;
import android.util.Log;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.TraceUtil;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.utils.UserUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * date : 2021/5/2810:35
 * desc :请求头
 */
public class UserAgentInterceptor implements Interceptor {
    private final String KEY_TOKEN = "smart-assistant-token";
    private final String AREA_ID = "Area-ID";
    private final String TRACEPARENT = "traceparent";
    private String mToken = "";//家庭token
    private String mAreaId = "";//家庭id


    @Override
    public Response intercept(Chain chain) throws IOException {
        HomeCompanyBean home = Constant.CurrentHome;
        if (home != null) {
            if (!TextUtils.isEmpty(home.getSa_user_token())) {
                mToken = home.getSa_user_token();
            }
            if (UserUtils.isLogin() && home.getId() > 0) {
                mAreaId = String.valueOf(home.getId());
            }
        }
        String traceparentVal = "00-" + TraceUtil.getHexdiglc(32) + "-" + TraceUtil.getHexdiglc(16) + "-01";

        Request request = chain.request().newBuilder()
                .addHeader(KEY_TOKEN, mToken)
                .addHeader(AREA_ID, mAreaId)
                .addHeader(TRACEPARENT, traceparentVal)
                .build();
        Log.e("WSocketManager===", "mToken=" + mToken);
        Log.e("WSocketManager===", "mAreaId=" + mAreaId);
        Log.e("WSocketManager===","traceparentVal="+traceparentVal);
        return chain.proceed(request);
    }
}