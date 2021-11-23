package com.yctc.zhiting.utils;

import android.text.TextUtils;
import android.util.Log;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.TimeFormatUtil;
import com.app.main.framework.entity.ChannelEntity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * date : 2021/8/27 17:43
 * desc :临时通道获取及保存
 */
public class ChannelUtil {

    public static void reSaveChannel() {
        boolean isBindSA = SpUtil.getBoolean(SpConstant.IS_BIND_SA);
        if (isBindSA && UserUtils.isLogin() && HomeUtil.getHomeId() > 0 && !isWithinTime()) {
            LogUtil.e("reSaveChannel123");
            String url = HttpUrlConfig.apiSCUrl + "datatunnel" + Constant.ONLY_SC;
            List<NameValuePair> requestData = new ArrayList<>();
            requestData.add(new NameValuePair("scheme", "https"));

            String areaId = SpUtil.get(SpConstant.AREA_ID);
            HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, areaId);
            HTTPCaller.getInstance().get(ChannelEntity.class, url, requestData,
                    new RequestDataCallback<ChannelEntity>() {
                        @Override
                        public void onSuccess(ChannelEntity bean) {
                            super.onSuccess(bean);
                            if (bean != null) {
                                Log.e("HTTPCaller1=", "checkTemporaryUrl=onSuccess");
                                bean.setGenerate_channel_time(TimeFormatUtil.getCurrentTime());
                                String tokenKey = SpUtil.get(SpConstant.SA_TOKEN);
                                String value = GsonConverter.getGson().toJson(bean);
                                SpUtil.put(tokenKey, value);
                            }
                        }

                        @Override
                        public void onFailed(int errorCode, String errorMessage) {
                            super.onFailed(errorCode, errorMessage);
                            Log.e("HTTPCaller2=", "checkTemporaryUrl=onFailed");
                        }
                    });
        }
    }

    /**
     * 判断是否在有效时间内
     *
     * @return
     */
    public static boolean isWithinTime() {
        long currentTime = TimeFormatUtil.getCurrentTime();
        String tokenKey = SpUtil.get(SpConstant.SA_TOKEN);
        String json = SpUtil.get(tokenKey);
        if (!TextUtils.isEmpty(json)) {
            ChannelEntity channel = GsonConverter.getGson().fromJson(json, ChannelEntity.class);
            if (channel != null) {
                if ((currentTime - channel.getCreate_channel_time()) < channel.getExpires_time()) {
                    return true;
                }
            }
        }
        return false;
    }
}
