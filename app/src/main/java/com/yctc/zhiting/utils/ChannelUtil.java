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
import com.yctc.zhiting.websocket.WSocketManager;

import java.util.ArrayList;
import java.util.List;

/**
 * date : 2021/8/27 17:43
 * desc :临时通道获取及保存
 */
public class ChannelUtil {
    private static String TAG = "ChannelUtil==";

    public static void refreshHomeTempeChannel() {
        //虚拟SA也走临时通道
        String saId = SpUtil.get(SpConstant.SA_ID);
        LogUtil.e(TAG, "saId=" + saId);
        LogUtil.e(TAG, "isLogin=" + UserUtils.isLogin());
        LogUtil.e(TAG, "getHomeId=" + HomeUtil.getHomeId());
        LogUtil.e(TAG, "isWithinTime=" + isWithinTime());

        if (!TextUtils.isEmpty(saId) && UserUtils.isLogin() && HomeUtil.getHomeId() > 0 && !isWithinTime()) {
            LogUtil.e("reSaveChannel123");
            //WSocketManager.getInstance().close();

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
                            Log.e("HTTPCaller1=", "checkTemporaryUrl=onSuccess");
                            saveChannelData(bean);
                        }

                        @Override
                        public void onFailed(int errorCode, String errorMessage) {
                            super.onFailed(errorCode, errorMessage);
                            Log.e("HTTPCaller2=", "checkTemporaryUrl=onFailed");
                        }
                    });
        } else {
            WSocketManager.getInstance().start();
        }
    }

    /**
     * 保存临时通道
     *
     * @param bean
     */
    private static void saveChannelData(ChannelEntity bean) {
        if (bean == null) return;
        bean.setGenerate_channel_time(TimeFormatUtil.getCurrentTime());
        String tokenKey = SpUtil.get(SpConstant.AREA_ID);
        String value = GsonConverter.getGson().toJson(bean);
        SpUtil.put(tokenKey, value);
        WSocketManager.getInstance().start();
    }

    /**
     * 判断是否在有效时间内
     *
     * @return
     */
    public static boolean isWithinTime() {
        long currentTime = TimeFormatUtil.getCurrentTime();
        String tokenKey = SpUtil.get(SpConstant.AREA_ID);
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
