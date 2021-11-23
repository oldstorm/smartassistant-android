package com.yctc.zhiting.websocket;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.TimeFormatUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.entity.ChannelEntity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.SSLSocketClient;
import com.app.main.framework.httputil.cookie.CookieJarImpl;
import com.app.main.framework.httputil.cookie.PersistentCookieStore;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * date : 2021/5/1915:01
 * desc :websocket 管理类
 */
public class WSocketManager {

    private String TAG = "WSocketManager===";
    //private String url = "ws://192.168.0.84:8088/ws";//测试服务器ip
    //private String urlSC = "wss://sc.zhitingtech.com:8088/ws";//SC服务器获取的url
    //private String urlSC = "wss://echo.websocket.org";
    //private String urlSC = "wss://104.238.164.189:8088/ws";//SC服务器获取的url
    //private String url = "wss://sa.zhitingtech.com/ws";//服务器域名
    //private String url = "ws://192.168.0.188:8088/ws";//伟杰服务器
    //private String url = "ws://192.168.0.112:8088/ws";//马健
    //private String url = "ws://192.168.0.127:8088/ws";//巫力宏
    //private String urlSA = "ws://sa.zhitingtech.com:8088/ws";//SA服务器获取的url
    private String urlSC = "wss://scgz.zhitingtech.com/ws";//SC服务器获取的url
    private String urlSA = "ws://192.168.22.123:9020/ws";//SA服务器获取的url
//    private String urlSA = "ws://192.168.22.106:37965/ws";//SA服务器获取的url
//    private String urlSC = "wss://192.168.22.76:9097/ws";//SC服务器获取的url
//    private String urlSA = "ws://192.168.22.76:8088/ws";//SA服务器获取的url
    private String mHostSC = "scgz.zhitingtech.com";

    private Request mRequest;
    private WebSocket mWebSocket;
    private OkHttpClient mOkHttpClient;
    private long PING_TIME = 60 * 1000L;//心跳
    private long TIMEOUT = 30L;//单位 秒
    public static boolean isConnecting;//是否连接中
    private static WSocketManager instance;
    private List<IWebSocketListener> mWebSocketListeners = new ArrayList<>();

    public WSocketManager() {
    }

    public static WSocketManager getInstance() {
        if (instance == null) {
            synchronized (WSocketManager.class) {
                if (instance == null) {
                    instance = new WSocketManager();
                }
            }
        }
        return instance;
    }

    public WebSocketListener mWebSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            //连接成功
            Log.e(TAG, "websocket连接成功");
            isConnecting = true;
            if (mWebSocketListeners.size() > 0) {
                for (IWebSocketListener listener : mWebSocketListeners) {
                    if (listener != null) {
                        UiUtil.runInMainThread(() -> listener.onOpen(webSocket, response));
                    }
                }
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            Log.e(TAG, "onClosing");
            isConnecting = false;
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Log.e(TAG, "onClosed");
            isConnecting = false;
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            //接收服务器消息 text
            Log.e(TAG, "onMessage1=" + text);
            isConnecting = true;
            UiUtil.runInMainThread(() -> {
                if (mWebSocketListeners.size() > 0) {
                    for (IWebSocketListener listener : mWebSocketListeners) {
                        if (listener != null) {
                            listener.onMessage(webSocket, text);
                        }
                    }
                }
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
            //如果服务器传递的是byte类型的
            isConnecting = true;
            String msg = bytes.utf8();
            Log.e(TAG, "onMessage2=" + msg);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            //连接失败调用 异常信息t.getMessage()
            t.printStackTrace();
            Log.e(TAG, "失败1=" + t.getMessage());
            if (response != null) {
                Log.e(TAG, "失败2=" + response.message());
                Log.e(TAG, "失败3=" + response.body().toString());
            }
            isConnecting = false;
            UiUtil.runInMainThread(() -> {
                if (mWebSocketListeners.size() > 0) {
                    for (IWebSocketListener listener : mWebSocketListeners) {
                        if (listener != null) {
                            listener.onFailure(webSocket, t, response);
                        }
                    }
                }
            });
        }
    };

    /**
     * 初始化websocket
     */
    public void start() {
        Log.e(TAG, "start");
        if (mOkHttpClient != null) {
            mOkHttpClient = null;
        }
        if (mRequest != null) {
            mRequest = null;
        }
        if (mWebSocket != null) {
            mWebSocket.close(1000, "close");
            mWebSocket = null;
        }
        mOkHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)//允许失败重试
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                .pingInterval(PING_TIME, TimeUnit.SECONDS)//心跳
                .certificatePinner(CertificatePinner.DEFAULT)
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .cookieJar(new CookieJarImpl(PersistentCookieStore.getInstance()))
                .addInterceptor(new UserAgentInterceptor())
                .build();

        String url = getUrl();
        mRequest = new Request.Builder()
                .url(url)
                .build();

        mWebSocket = mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
        mOkHttpClient.dispatcher().executorService().shutdown();//内存不足时释
        Log.e(TAG, "url=" + url);
    }

    /**
     * 获取地址
     *
     * @return
     */
    private String getUrl() {
        if (Constant.CurrentHome!=null && !TextUtils.isEmpty(Constant.CurrentHome.getSa_lan_address())) {
            String url = "ws://"+Constant.CurrentHome.getSa_lan_address().replace("http://", "")+"/ws";
            urlSA = url;
        }
        if (HomeUtil.isSAEnvironment()) {
            return urlSA;
        } else {
            if (UserUtils.isLogin()) {
                return getSCUrl();
            } else {
                return urlSA;
            }
        }
    }

    private String getSCUrl() {
        String newUrlSC = urlSC;
        long currentTime = TimeFormatUtil.getCurrentTime();
        String tokenKey = SpUtil.get(SpConstant.SA_TOKEN);
        String json = SpUtil.get(tokenKey);

        if (!TextUtils.isEmpty(json)) {
            ChannelEntity channel = GsonConverter.getGson().fromJson(json, ChannelEntity.class);
            if (channel != null) {
                LogUtil.e("WSocketManager=getSCUrl=" + GsonConverter.getGson().toJson(channel));
                if ((currentTime - channel.getCreate_channel_time()) < channel.getExpires_time()) {
                    newUrlSC = newUrlSC.replace(mHostSC, channel.getHost());
//                    newUrlSC=newUrlSC.replace("wss","ws");
                    LogUtil.e("WSocketManager=getSCUrl=" + newUrlSC);
                }
            }
        }
        return newUrlSC;
    }

    /**
     * 发送消息
     *
     * @param json
     * @return
     */
    public WSocketManager sendMessage(String json) {
        Log.e(TAG, "sendMessage发送消息1=" + isConnecting + ",json=" + json);
        if (isConnecting && mWebSocket != null && !TextUtils.isEmpty(json)) {
            boolean success = mWebSocket.send(json);
            Log.e(TAG, "sendMessage发送消息2=" + json + "\nsuccess=" + success);
        }
        return this;
    }

    public void addWebSocketListener(IWebSocketListener listener) {
        if (!mWebSocketListeners.contains(listener)) {
            mWebSocketListeners.add(listener);
        }
    }

    public void removeWebSocketListener(IWebSocketListener listener) {
        if (mWebSocketListeners.contains(listener)) {
            mWebSocketListeners.remove(listener);
        }
    }
}
