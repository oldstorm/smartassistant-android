package com.yctc.zhiting.websocket;

import static com.yctc.zhiting.config.Constant.CurrentHome;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.TimeFormatUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.config.HttpBaseUrl;
import com.app.main.framework.entity.ChannelEntity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.SSLSocketClient;
import com.app.main.framework.httputil.cookie.CookieJarImpl;
import com.app.main.framework.httputil.cookie.PersistentCookieStore;
import com.king.zxing.util.LogUtils;
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
    private String urlSC = "wss://" + HttpBaseUrl.baseSCHost + "/ws";//SC服务器获取的url
    private String urlSA = "ws://192.168.22.123:9020/ws";//SA服务器获取的url

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
            //Log.e(TAG, "onMessage1=" + text);
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
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            //连接失败调用 异常信息t.getMessage()
            t.printStackTrace();
            Log.e(TAG, "失败1=" + t.getMessage());
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
        //close();

        Log.e(TAG, "start");
        if (mOkHttpClient != null) {
            mOkHttpClient = null;
        }
        if (mRequest != null) {
            mRequest = null;
        }

        String url = getUrl();
        if (TextUtils.isEmpty(url)) return;

        mRequest = new Request.Builder()
                .url(url)
                .build();

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

        mWebSocket = mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
        mOkHttpClient.dispatcher().executorService().shutdown();//内存不足时释
        Log.e(TAG, "url=" + url);
    }

    /**
     * 设备详情的WS
     */
    private WebSocket mDevWebSocket;
    public void connectWS(String url, String token, WebSocketListener webSocketListener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (mDevWebSocket!=null) {
            mDevWebSocket = null;
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
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

        Request request = new Request.Builder()
                .url(url)
                .build();

        mDevWebSocket = okHttpClient.newWebSocket(request, webSocketListener);
        okHttpClient.dispatcher().executorService().shutdown();//内存不足时释
        Log.e(TAG, "url=" + url);
    }

    /**
     * 设备详情发送消息
     *
     * @param json
     * @return
     */
    public WSocketManager sendDevMessage(String json) {
        if (mDevWebSocket != null && !TextUtils.isEmpty(json)) {
//            LogUtil.e("发送数据："+json);
            boolean success = mDevWebSocket.send(json);
            //Log.e(TAG, "sendMessage发送消息2=" + json + "\nsuccess=" + success);
        }
        return this;
    }

    /**
     * 关闭设备详情的WS
     */
    public void closeDevWS() {
        if (mDevWebSocket!=null) {
            mDevWebSocket.close(1000, "close");
        }
    }

    public void close() {
        if (mWebSocket != null) {
            mWebSocket.close(1000, "close");
            mWebSocket = null;
        }
    }

    /**
     * 获取地址
     *
     * @return
     */
    private String getUrl() {
        if (UserUtils.isLogin() && !HomeUtil.isSAEnvironment()) {//登录并且不在SA,使用SC需要登录
            return getSCUrl();
        } else if (HomeUtil.isSAEnvironment() && HomeUtil.isBindSA()) {//SA
            return getSAUrl();
        } else {//断开
            close();
            return "";
        }
    }

    /**
     * 获取SA地址
     *
     * @return
     */
    private String getSAUrl() {
        if (CurrentHome != null && !TextUtils.isEmpty(CurrentHome.getSa_lan_address())) {
            Uri uri = Uri.parse(CurrentHome.getSa_lan_address());
            urlSA = "ws://" + uri.getAuthority() + "/ws";
        } else {
            urlSA = "";
        }
        return urlSA;
    }

    /**
     * 获取SC地址
     *
     * @return
     */
    private String getSCUrl() {
        String newUrlSC = urlSC;
        long currentTime = TimeFormatUtil.getCurrentTime();
        String tokenKey = SpUtil.get(SpConstant.AREA_ID);
        String json = SpUtil.get(tokenKey);

        if (!TextUtils.isEmpty(json)) {
            ChannelEntity channel = GsonConverter.getGson().fromJson(json, ChannelEntity.class);
            if (channel != null) {
                LogUtil.e("WSocketManager=getSCUrl=" + GsonConverter.getGson().toJson(channel));
                if ((currentTime - channel.getCreate_channel_time()) < channel.getExpires_time()) {
                    newUrlSC = newUrlSC.replace(HttpBaseUrl.baseSCHost, channel.getHost());
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
        if (isConnecting && mWebSocket != null && !TextUtils.isEmpty(json)) {
//            LogUtil.e("发送数据："+json);
            boolean success = mWebSocket.send(json);
            //Log.e(TAG, "sendMessage发送消息2=" + json + "\nsuccess=" + success);
        }
        return this;
    }

    public void addWebSocketListener(IWebSocketListener listener) {
        if (!mWebSocketListeners.contains(listener)) {
            mWebSocketListeners.add(listener);
        }
        LogUtils.d("mWebSocketListeners : " + mWebSocketListeners.size());
    }

    public void removeWebSocketListener(IWebSocketListener listener) {
        if (mWebSocketListeners.contains(listener)) {
            mWebSocketListeners.remove(listener);
        }
    }
}
