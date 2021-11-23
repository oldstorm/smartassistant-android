package com.app.main.framework.httputil;

import android.text.TextUtils;
import android.util.Log;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.TimeFormatUtil;
import com.app.main.framework.entity.ChannelEntity;
import com.app.main.framework.entity.SATokenEntity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.comfig.HttpConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 临时通道数据
 */
public class TempChannelUtil {

    public static String TAG = "TempChannelUtil";
    public static String SC_HOST = "scgz.zhitingtech.com";
    public static String baseSCUrl = "https://scgz.zhitingtech.com/api";//测试服务SC
    public static String baseSAUrl = "http://192.168.22.123:9020/api";//测试服务器SA
    public static String ONLY_SC = "&type=only_sc";//注意com.yctc.zhiting.config.Constant也有这个常量
    public static String CHANNEL_URL = baseSCUrl + "/datatunnel"+ONLY_SC;

    public static class OnTempChannelListener {
        void onSuccess(String newUrl) {
        }

        void onError(int code, String message) {
        }
    }

    public static void checkTemporaryUrl(String oldUrl, OnTempChannelListener listener) {
        if (oldUrl != null && oldUrl.contains(baseSAUrl) || isNoNeedTempChannelUrl(oldUrl)) {//就是sa或者不需要转换,直接返url
            if (!TextUtils.isEmpty(oldUrl) && oldUrl.contains(ONLY_SC))
                oldUrl = oldUrl.replace(ONLY_SC, "");
            listener.onSuccess(oldUrl);
        } else {
            if (isWithinTime(oldUrl, listener)) return;//在有效时间内

            List<NameValuePair> requestData = new ArrayList<>();
            requestData.add(new NameValuePair("scheme", "https"));
            String finalOriginalUrl = oldUrl;

            HTTPCaller.getInstance().get(ChannelEntity.class, CHANNEL_URL, requestData,
                    new RequestDataCallback<ChannelEntity>() {
                        @Override
                        public void onSuccess(ChannelEntity obj) {
                            super.onSuccess(obj);
                            if (obj != null) {
                                Log.e(TAG, "checkTemporaryUrl=onSuccess123=");
                                listener.onSuccess(getUrl(finalOriginalUrl, obj.getHost()));
                                saveTempChannelUrl(obj);
                            }
                        }

                        @Override
                        public void onFailed(int errorCode, String errorMessage) {
                            super.onFailed(errorCode, errorMessage);
                            Log.e(TAG, "checkTemporaryUrl=onFailed123=");
                            if (errorCode == 5012) {
                                getSAToken(listener, finalOriginalUrl);
                            } else {
                                listener.onSuccess(finalOriginalUrl);
                                listener.onError(errorCode, errorMessage);
                            }
                        }
                    });
        }
    }

    /**
     * 找回家庭凭证
     *
     * @param url
     */
    public static void getSAToken(OnTempChannelListener listener, String url) {
        String areaId = SpUtil.get(SpConstant.AREA_ID);
        NameValuePair nameValuePair = new NameValuePair("area_id", areaId);
        List<NameValuePair> requestData = new ArrayList<>();
        requestData.add(nameValuePair);

        int cloudUserId = SpUtil.getInt(SpConstant.CLOUD_USER_ID);
        String requestUrl = baseSCUrl + "/users/" + cloudUserId + "/sa_token" + ONLY_SC;
        LogUtil.e("getSAToken1=" + cloudUserId);

        HTTPCaller.getInstance().get(SATokenEntity.class, requestUrl, requestData, new RequestDataCallback<SATokenEntity>() {
            @Override
            public void onSuccess(SATokenEntity obj) {
                super.onSuccess(obj);
                if (obj != null && !TextUtils.isEmpty(obj.getSa_token())) {
                    HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, obj.getSa_token());
                    checkTemporaryUrl(url, listener);
                    LogUtil.e("getSAToken2=" + obj.getSa_token());
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                LogUtil.e("getSAToken2=" + errorCode + "," + errorMessage);
            }
        });
    }

    //登出logout .登陆login, 注册register,短信 .captcha, (完成)
    //用户详情.cloudUserDetail,编辑用户 .editCloudUser, 家庭列表 .areaList,创建家庭 .createArea:
    private static boolean isNoNeedTempChannelUrl(String originalUrl) {
        if (!TextUtils.isEmpty(originalUrl) && originalUrl.contains(ONLY_SC)) {
            return true;
        }
        return false;
    }

    public static void saveTempChannelUrl(ChannelEntity bean) {
        bean.setGenerate_channel_time(TimeFormatUtil.getCurrentTime());
        String tokenKey = SpUtil.get(SpConstant.SA_TOKEN);
        String value = GsonConverter.getGson().toJson(bean);
        SpUtil.put(tokenKey, value);
    }

    /**
     * 是否在有效期内
     *
     * @return
     */
    public static boolean isWithinTime(String originalUrl, OnTempChannelListener listener) {
        long currentTime = TimeFormatUtil.getCurrentTime();
        String tokenKey = SpUtil.get(SpConstant.SA_TOKEN);
        String json = SpUtil.get(tokenKey);
        if (!TextUtils.isEmpty(json)) {
            ChannelEntity channel = GsonConverter.getGson().fromJson(json, ChannelEntity.class);
            if (channel != null) {
                if ((currentTime - channel.getCreate_channel_time()) < channel.getExpires_time()) {
                    String newUrl = getUrl(originalUrl, channel.getHost());
                    listener.onSuccess(newUrl);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 通过域名获网络请求地址,临时通道是走http
     *
     * @param host
     * @return
     */
    private static String getUrl(String originalUrl, String host) {
        String url = "";
        if (!TextUtils.isEmpty(host)) {
            url = originalUrl.replace(SC_HOST, host);
        }
        LogUtil.e(TAG+"=getUrl=="+url);
        return url;
    }
}
