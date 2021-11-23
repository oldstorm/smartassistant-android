package com.app.main.framework.httputil;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;

import androidx.fragment.app.FragmentActivity;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CertificateDialog;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.httputil.cookie.CookieJarImpl;
import com.app.main.framework.httputil.cookie.PersistentCookieStore;
import com.app.main.framework.httputil.download.ResponseJsonBean;
import com.app.main.framework.httputil.log.LoggerInterceptor;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLPeerUnverifiedException;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * HTTP请求发起和数据解析转换
 */
public class HTTPCaller {

    public static final String CLOUD_HOST_NAME = "scgz.zhitingtech.com";
    private static HTTPCaller _instance = null;
    private OkHttpClient client;//okhttp对象
    private Map<String, Call> requestHandleMap = null;//以URL为KEY存储的请求
    private CacheControl cacheControl = null;//缓存控制器
    private Gson gson = null;
    private HttpConfig httpConfig = new HttpConfig();//配置信息
    private boolean hasDialog;

    private HTTPCaller() {
    }

    public static HTTPCaller getInstance() {
        if (_instance == null) {
            _instance = new HTTPCaller();
        }
        return _instance;
    }

    public void setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    public HttpConfig getHttpConfig() {
        return httpConfig;
    }

    /**
     * 设置配置信息 这个方法必需要调用一次
     */
    public void initHttpConfig() {
        client = new OkHttpClient.Builder()
                .connectTimeout(httpConfig.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(httpConfig.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(httpConfig.getReadTimeout(), TimeUnit.SECONDS)
                .addInterceptor(new LoggerInterceptor("ZhiTing", true))
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .hostnameVerifier((hostname, session) -> {
                    if (hostname.equals(CLOUD_HOST_NAME)) {  // SC直接访问
                        return true;
                    } else {
                        if (session != null) {
                            String cersCacheJson = SpUtil.get(hostname); // 根据主机名获取本地存储的数据
                            try {
                                Certificate[] certificates = session.getPeerCertificates();  // 证书
                                String cersJson = byte2Base64String(certificates[0].getEncoded());  // 把证书转为base64存储
                                if (!TextUtils.isEmpty(cersCacheJson)) {  // 如果之前存储过
                                    String ccj = new String(cersCacheJson.getBytes(), "UTF-8");
                                    String cj = new String(cersJson.getBytes(), "UTF-8");
                                    boolean cer = cj.equals(ccj);
                                    if (cer) {  // 之前存储过的证书和当前证书一样，直接访问
                                        return true;
                                    } else {// 之前存储过的证书和当前证书不一样，重新授权
                                        showAlertDialog(LibLoader.getCurrentActivity().getString(R.string.whether_trust_this_certificate_again), hostname, cersJson);
                                        return false;
                                    }
                                } else {// 之前没存储过，询问是否信任证书
                                    showAlertDialog(LibLoader.getCurrentActivity().getString(R.string.whether_trust_this_certificate), hostname, cersJson);
                                    return false;
                                }
                            } catch (SSLPeerUnverifiedException e) {
                                e.printStackTrace();
                            } catch (CertificateEncodingException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                })
                .cookieJar(new CookieJarImpl(PersistentCookieStore.getInstance()))
                .build();

        gson = new Gson();
        requestHandleMap = Collections.synchronizedMap(new WeakHashMap<>());
        cacheControl = new CacheControl.Builder().noCache().noStore().build();//不使用缓存
    }

    private void showAlertDialog(String tips, String hostName, String cerCache) {
        FragmentActivity activity = (FragmentActivity) LibLoader.getCurrentActivity();
        if (activity != null) {
           if (!hasDialog) {
               hasDialog = true;
               CertificateDialog certificateDialog = CertificateDialog.newInstance(tips);
               certificateDialog.setConfirmListener(() -> {
                   SpUtil.put(hostName, cerCache);
                   certificateDialog.dismiss();
                   hasDialog = false;
               });
               certificateDialog.show(activity);
           }
        }
    }

    public <T> void get(Class<T> clazz, final String url, Header[] header, final RequestDataCallback<T> callback) {
        this.get(clazz, url, header, callback, true);
    }

    public <T> void get(Class<T> clazz, final String url, final RequestDataCallback<T> callback) {
        this.get(clazz, url, httpConfig.getHeaders(), callback, false);
    }

    public <T> void get(Class<T> clazz, final String url, List<NameValuePair> requestData, final RequestDataCallback<T> callback) {
        this.get(clazz, url, httpConfig.getHeaders(), requestData, callback, false);
    }

    public <T> void get(final String url, List<NameValuePair> requestData, Type typeToken, final RequestDataCallback<T> callback) {
        this.get(url, callback, requestData, typeToken, false);
    }

    public <T> void put(Class<T> clazz, final String url, String params, final RequestDataCallback<T> callback) {
        if (checkAgent()) {
            return;
        }
        TempChannelUtil.checkTemporaryUrl(url, new TempChannelUtil.OnTempChannelListener() {
            @Override
            public void onSuccess(String newUrl) {
                add(newUrl, putBuilder(newUrl, httpConfig.getHeaders(), params, new MyHttpResponseHandler(clazz, newUrl, callback)), false);
            }
        });
    }

    /**
     * get请求
     *
     * @param clazz      json对应类的类型
     * @param url        请求url
     * @param header     请求头
     * @param callback   回调接口
     * @param autoCancel 是否自动取消 true:同一时间请求一个接口多次  只保留最后一个
     * @param <T>
     */
    public <T> void get(final Class<T> clazz, final String url, Header[] header, List<NameValuePair> requestData, final RequestDataCallback<T> callback, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        TempChannelUtil.checkTemporaryUrl(url, new TempChannelUtil.OnTempChannelListener() {
            @Override
            public void onSuccess(String newUrl) {
                add(newUrl, getBuilder(newUrl, header, requestData, new MyHttpResponseHandler(clazz, newUrl, callback)), autoCancel);
            }
        });
    }

    public <T> void getChannel(final Class<T> clazz, final String url, Header[] header, List<NameValuePair> requestData, final RequestDataCallback<T> callback, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        add(url, getBuilder(url, header, requestData, new MyHttpResponseHandler(clazz, url, callback)), autoCancel);
    }

    public <T> void get(final Class<T> clazz, final String url, Header[] header, final RequestDataCallback<T> callback, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        TempChannelUtil.checkTemporaryUrl(url, new TempChannelUtil.OnTempChannelListener() {
            @Override
            public void onSuccess(String newUrl) {
                add(newUrl, getBuilder(newUrl, header, new MyHttpResponseHandler(clazz, newUrl, callback)), autoCancel);
            }
        });
    }

    public <T> void get(final String url, final RequestDataCallback<T> callback, Type typeToken, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        TempChannelUtil.checkTemporaryUrl(url, new TempChannelUtil.OnTempChannelListener() {
            @Override
            public void onSuccess(String newUrl) {
                add(newUrl, getBuilder(newUrl, httpConfig.getHeaders(), new MyHttpResponseHandler(newUrl, callback, typeToken)), autoCancel);
            }
        });
    }

    public <T> void get(final String url, final RequestDataCallback<T> callback, List<NameValuePair> requestData, Type typeToken, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        TempChannelUtil.checkTemporaryUrl(url, new TempChannelUtil.OnTempChannelListener() {
            @Override
            public void onSuccess(String newUrl) {
                add(newUrl, getBuilder(newUrl, httpConfig.getHeaders(), requestData, new MyHttpResponseHandler(newUrl, callback, typeToken)), autoCancel);
            }
        });
    }

    private Call getBuilder(String url, Header[] header, List<NameValuePair> requestData, HttpResponseHandler responseCallback) {
        //url = Util.getMosaicParameter(url, httpConfig.getCommonField());//拼接公共参数
        if (requestData != null && requestData.size() > 0)
            url = Util.getMosaicParameter(url, requestData);//拼接公共参数
        Request.Builder builder = new Request.Builder();
        builder.addHeader("smart-assistant-token", SpUtil.get(SpConstant.SA_TOKEN));
        builder.url(url);
        builder.get();
        return execute(builder, header, responseCallback);
    }

    private Call getBuilder(String url, Header[] header, HttpResponseHandler responseCallback) {
        url = Util.getMosaicParameter(url, httpConfig.getCommonField());//拼接公共参数
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.get();
        return execute(builder, header, responseCallback);
    }

    private Call putBuilder(String url, Header[] headers, String json, HttpResponseHandler responseHandler) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.put(requestBody);
            return execute(builder, headers, responseHandler);
        } catch (Exception e) {
            if (responseHandler != null)
                responseHandler.onFailure(-1, e.getMessage().getBytes());
        }
        return null;
    }

    public <T> void post(final Class<T> clazz, final String url, Header[] header, List<NameValuePair> params, final RequestDataCallback<T> callback) {
        this.post(clazz, url, header, params, callback, false);
    }

    public <T> void post(final Class<T> clazz, final String url, List<NameValuePair> params, final RequestDataCallback<T> callback) {
        this.post(clazz, url, httpConfig.getHeaders(), params, callback, false);
    }

    public <T> void post(final Class<T> clazz, final String url, String json, final RequestDataCallback<T> callback) {
        this.post(clazz, url, httpConfig.getHeaders(), json, callback, false);
    }

    /**
     * @param clazz    请求返回的对象
     * @param url      请求地址
     * @param request  body对象
     * @param callback 回掉
     * @param <T>
     */
    public <T> void post(final Class<T> clazz, final String url, com.app.main.framework.httputil.request.Request request, final RequestDataCallback<T> callback) {
        String json = GsonConverter.getGson().toJson(request);
        this.post(clazz, url, httpConfig.getHeaders(), json, callback, false);
    }

    public <T> void delete(final Class<T> clazz, final String url, String json, final RequestDataCallback<T> callback) {
        this.delete(clazz, url, httpConfig.getHeaders(), json, callback, false);
    }

    public <T> void delete(final Class<T> clazz, final String url, Header[] header, final String json, final RequestDataCallback<T> callback, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        TempChannelUtil.checkTemporaryUrl(url, new TempChannelUtil.OnTempChannelListener() {
            @Override
            public void onSuccess(String newUrl) {
                add(newUrl, deleteBuilder(newUrl, header, json, new MyHttpResponseHandler(clazz, newUrl, callback)), autoCancel);
            }
        });
    }

    /**
     * @param clazz      json对应类的类型
     * @param url        请求url
     * @param header     请求头
     * @param params     参数
     * @param callback   回调
     * @param autoCancel 是否自动取消 true:同一时间请求一个接口多次  只保留最后一个
     * @param <T>
     */
    public <T> void post(final Class<T> clazz, final String url, Header[] header, final List<NameValuePair> params, final RequestDataCallback<T> callback, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        TempChannelUtil.checkTemporaryUrl(url, new TempChannelUtil.OnTempChannelListener() {
            @Override
            public void onSuccess(String newUrl) {
                add(newUrl, postBuilder(newUrl, header, params, new MyHttpResponseHandler(clazz, newUrl, callback)), autoCancel);
            }
        });
    }

    public <T> void post(final Class<T> clazz, final String url, Header[] header, final String json, final RequestDataCallback<T> callback, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        TempChannelUtil.checkTemporaryUrl(url, new TempChannelUtil.OnTempChannelListener() {
            @Override
            public void onSuccess(String newUrl) {
                add(newUrl, postBuilder(newUrl, header, json, new MyHttpResponseHandler(clazz, newUrl, callback)), autoCancel);
            }
        });
    }

    public <T> void patch(final Class<T> clazz, final String url, String json, final RequestDataCallback<T> callback) {
        this.patch(clazz, url, httpConfig.getHeaders(), json, callback, false);
    }

    public <T> void patch(final Class<T> clazz, final String url, Header[] header, final String json, final RequestDataCallback<T> callback, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        TempChannelUtil.checkTemporaryUrl(url, new TempChannelUtil.OnTempChannelListener() {
            @Override
            public void onSuccess(String newUrl) {
                add(newUrl, patchBuilder(newUrl, header, json, new MyHttpResponseHandler(clazz, newUrl, callback)), autoCancel);
            }
        });
    }

    private Call postBuilder(String url, Header[] header, List<NameValuePair> form, HttpResponseHandler responseCallback) {
        try {
            if (form == null) {
                form = new ArrayList<>(2);
            }
            form.addAll(httpConfig.getCommonField());//添加公共字段
            FormBody.Builder formBuilder = new FormBody.Builder();
            for (NameValuePair item : form) {
                formBuilder.add(item.getName(), item.getValue());
            }
            RequestBody requestBody = formBuilder.build();
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(requestBody);
            return execute(builder, header, responseCallback);
        } catch (Exception e) {
            if (responseCallback != null)
                responseCallback.onFailure(-1, e.getMessage().getBytes());
        }
        return null;
    }

    private Call deleteBuilder(String url, Header[] header, String json, HttpResponseHandler responseCallback) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.delete(requestBody);
            return execute(builder, header, responseCallback);
        } catch (Exception e) {
            if (responseCallback != null)
                responseCallback.onFailure(-1, e.getMessage().getBytes());
        }
        return null;
    }

    private Call postBuilder(String url, Header[] header, String json, HttpResponseHandler responseCallback) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(requestBody);
            return execute(builder, header, responseCallback);
        } catch (Exception e) {
            if (responseCallback != null)
                responseCallback.onFailure(-1, e.getMessage().getBytes());
        }
        return null;
    }

    /**
     * 带 okhttpClient
     * @param url
     * @param header
     * @param json
     * @param responseCallback
     * @param okHttpClient
     * @return
     */
    public Call postBuilder(String url, Header[] header, String json, OkHttpClient okHttpClient, HttpResponseHandler responseCallback) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(requestBody);
            return execute(builder, header, responseCallback, okHttpClient);
        } catch (Exception e) {
            if (responseCallback != null)
                responseCallback.onFailure(-1, e.getMessage().getBytes());
        }
        return null;
    }

    private Call patchBuilder(String url, Header[] header, String json, HttpResponseHandler responseCallback) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.patch(requestBody);
            return execute(builder, header, responseCallback);
        } catch (Exception e) {
            if (responseCallback != null)
                responseCallback.onFailure(-1, e.getMessage().getBytes());
        }
        return null;
    }

    /**
     * 上传文件
     *
     * @param clazz    json对应类的类型
     * @param url      请求url
     * @param header   请求头
     * @param form     请求参数
     * @param callback 回调
     * @param <T>
     */
    public <T> void postFile(final Class<T> clazz, final String url, Header[] header, List<NameValuePair> form, final RequestDataCallback<T> callback) {
        postFile(url, header, form, new MyHttpResponseHandler(clazz, url, callback), null);
    }

    public <T> void postFile(final Class<T> clazz, final String url, List<NameValuePair> form, final RequestDataCallback<T> callback) {
        postFile(url, httpConfig.getHeaders(), form, new MyHttpResponseHandler(clazz, url, callback), null);
    }

    /**
     * 上传文件
     *
     * @param clazz              json对应类的类型
     * @param url                请求url
     * @param header             请求头
     * @param form               请求参数
     * @param callback           回调
     * @param progressUIListener 上传文件进度
     * @param <T>
     */
    public <T> void postFile(final Class<T> clazz, final String url, Header[] header, List<NameValuePair> form, final RequestDataCallback<T> callback, ProgressUIListener progressUIListener) {
        add(url, postFile(url, header, form, new MyHttpResponseHandler(clazz, url, callback), progressUIListener));
    }

    /**
     * 上传文件
     *
     * @param clazz       json对应类的类型
     * @param url         请求url
     * @param header      请求头
     * @param name        名字
     * @param fileName    文件名
     * @param fileContent 文件内容
     * @param callback    回调
     * @param <T>
     */
    public <T> void postFile(final Class<T> clazz, final String url, Header[] header, String name, String fileName, byte[] fileContent, final RequestDataCallback<T> callback) {
        postFile(clazz, url, header, name, fileName, fileContent, callback, null);
    }

    public <T> void postFile(final Class<T> clazz, final String url, String name, String fileName, byte[] fileContent, final RequestDataCallback<T> callback) {
        postFile(clazz, url, httpConfig.getHeaders(), name, fileName, fileContent, callback, null);
    }

    public <T> void postFile(final Class<T> clazz, final String url, String name, String fileName, byte[] fileContent, final RequestDataCallback<T> callback, ProgressUIListener progress) {
        postFile(clazz, url, httpConfig.getHeaders(), name, fileName, fileContent, callback, progress);
    }

    /**
     * 多图片的上传
     *
     * @param clazz
     * @param url
     * @param name
     * @param callback
     * @param <T>
     */
    public <T> void postFiles(final Class<T> clazz, final String url, String name, HashMap hashMap, final RequestDataCallback<T> callback) {
        postFiles(clazz, url, httpConfig.getHeaders(), name, hashMap, callback, null);
    }

    /**
     * 上传文件添加参数
     *
     * @param clazz
     * @param url
     * @param name
     * @param hashMap
     * @param params
     * @param callback
     * @param <T>
     */
    public <T> void postFilesParams(final Class<T> clazz, final String url, String name, HashMap hashMap, List<NameValuePair> params, final RequestDataCallback<T> callback) {
        postFilesParams(clazz, url, httpConfig.getHeaders(), name, hashMap, params, callback, null);
    }

    public <T> void postFilesParams(final Class<T> clazz, final String url, String name, HashMap hashMap, List<NameValuePair> params, final RequestDataCallback<T> callback, ProgressUIListener progressUIListener) {
        postFilesParams(clazz, url, httpConfig.getHeaders(), name, hashMap, params, callback, progressUIListener);
    }

    /**
     * 上传文件
     *
     * @param clazz              json对应类的类型
     * @param url                请求url
     * @param header             请求头
     * @param name               名字
     * @param callback           回调
     * @param progressUIListener 回调上传进度
     * @param <T>
     */
    public <T> void postFiles(Class<T> clazz, final String url, Header[] header, String name, HashMap hashMap, final RequestDataCallback<T> callback, ProgressUIListener progressUIListener) {
        add(url, postFiles(url, header, name, hashMap, new MyHttpResponseHandler(clazz, url, callback), progressUIListener));
    }

    public <T> void postFilesParams(Class<T> clazz, final String url, Header[] header, String name, HashMap hashMap, List<NameValuePair> params, final RequestDataCallback<T> callback, ProgressUIListener progressUIListener) {
        add(url, postFilesParams(url, header, name, hashMap, params, new MyHttpResponseHandler(clazz, url, callback), progressUIListener));
    }

    public <T> void postFile(Class<T> clazz, final String url, Header[] header, String name, String fileName, byte[] fileContent, final RequestDataCallback<T> callback, ProgressUIListener progressUIListener) {
        add(url, postFile(url, header, name, fileName, fileContent, new MyHttpResponseHandler(clazz, url, callback), progressUIListener));
    }

    public void downloadFile(String url, String saveFilePath, String fileName, Header[] header, String errorTip, ProgressUIListener progressUIListener) {
        downloadFile(url, saveFilePath, fileName, header, errorTip, progressUIListener, false);
    }

    public void downloadFile(String url, String saveFilePath, String fileName, Header[] header, String errorTip, ProgressUIListener progressUIListener, boolean autoCancel) {
        if (checkAgent()) {
            return;
        }
        add(url, downloadFileSendRequest(url, saveFilePath,fileName, header, errorTip, progressUIListener), autoCancel);
    }

    private Call downloadFileSendRequest(String url, final String saveFilePath, String fileName, Header[] header, String errorTip, final ProgressUIListener progressUIListener) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.get();
        return execute(builder, header, new DownloadFileResponseHandler(url, saveFilePath,fileName, errorTip, progressUIListener));
    }

    private Call postFile(String url, Header[] header, List<NameValuePair> form, HttpResponseHandler responseCallback, ProgressUIListener progressUIListener) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            MediaType mediaType = MediaType.parse("application/octet-stream");

            form.addAll(httpConfig.getCommonField());//添加公共字段
            for (int i = form.size() - 1; i >= 0; i--) {
                NameValuePair item = form.get(i);
                if (item.isFile()) {//上传文件
                    File myFile = new File(item.getValue());
                    if (myFile.exists()) {
                        String fileName = Util.getFileName(item.getValue());
                        builder.addFormDataPart(item.getName(), fileName, RequestBody.create(mediaType, myFile));
                    }
                } else {
                    builder.addFormDataPart(item.getName(), item.getValue());
                }
            }

            RequestBody requestBody;
            if (progressUIListener == null) {//不需要回调进度
                requestBody = builder.build();
            } else {//需要回调进度
                requestBody = ProgressHelper.withProgress(builder.build(), progressUIListener);
            }
            Request.Builder requestBuider = new Request.Builder();
            requestBuider.url(url);
            requestBuider.post(requestBody);
            return execute(requestBuider, header, responseCallback);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(httpConfig.getTagName(), e.toString());
            if (responseCallback != null)
                responseCallback.onFailure(-1, e.getMessage().getBytes());
        }
        return null;
    }

    private Call postFilesParams(String url, Header[] header, String name, HashMap hashMap, List<NameValuePair> params, HttpResponseHandler responseCallback, ProgressUIListener progressUIListener) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            MediaType mediaType = MediaType.parse("application/octet-stream");

            Iterator<Map.Entry<String, byte[]>> iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, byte[]> entry = iterator.next();
                LogUtil.e("HTTPCaller=111==name=" + name + ",key=" + entry.getKey() + ",value=" + entry.getValue());
                builder.addFormDataPart(name, entry.getKey(), RequestBody.create(mediaType, entry.getValue()));
                LogUtil.e("HTTPCaller=1111==key" + name + ",value=" + RequestBody.create(mediaType, entry.getValue()));

            }
            List<NameValuePair> form = new ArrayList<>(2);
            //form.addAll(httpConfig.getCommonField());//添加公共字段
            form.addAll(params);//添加公共字段
            for (NameValuePair item : form) {
                builder.addFormDataPart(item.getName(), item.getValue());
            }

            RequestBody requestBody;
            if (progressUIListener == null) {//不需要回调进度
                requestBody = builder.build();
            } else {//需要回调进度
                requestBody = ProgressHelper.withProgress(builder.build(), progressUIListener);
            }
            Request.Builder requestBuider = new Request.Builder();
            requestBuider.header("content-type", "multipart/form-data");
            requestBuider.header("consumes", "multipart/*");
            requestBuider.url(url);
            requestBuider.post(requestBody);
            return execute(requestBuider, header, responseCallback);
        } catch (Exception e) {
            if (httpConfig.isDebug()) {
                e.printStackTrace();
                LogUtil.e(httpConfig.getTagName(), e.toString());
            }
            if (responseCallback != null)
                responseCallback.onFailure(-1, e.getMessage().getBytes());
        }
        return null;
    }

    private Call postFiles(String url, Header[] header, String name, HashMap hashMap, HttpResponseHandler responseCallback, ProgressUIListener progressUIListener) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            MediaType mediaType = MediaType.parse("application/octet-stream");

            Iterator<Map.Entry<String, byte[]>> iterator = hashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, byte[]> entry = iterator.next();
                LogUtil.e("HTTPCaller=111==name=" + name + ",key=" + entry.getKey() + ",value=" + entry.getValue());
                builder.addFormDataPart(name, entry.getKey(), RequestBody.create(mediaType, entry.getValue()));
                LogUtil.e("HTTPCaller=1111==key" + name + ",value=" + RequestBody.create(mediaType, entry.getValue()));

            }
            List<NameValuePair> form = new ArrayList<>(2);
            form.addAll(httpConfig.getCommonField());//添加公共字段
            for (NameValuePair item : form) {
                builder.addFormDataPart(item.getName(), item.getValue());
            }

            RequestBody requestBody;
            if (progressUIListener == null) {//不需要回调进度
                requestBody = builder.build();
            } else {//需要回调进度
                requestBody = ProgressHelper.withProgress(builder.build(), progressUIListener);
            }
            Request.Builder requestBuider = new Request.Builder();
            requestBuider.header("content-type", "multipart/form-data");
            requestBuider.header("consumes", "multipart/*");
            requestBuider.url(url);
            requestBuider.post(requestBody);
            return execute(requestBuider, header, responseCallback);
        } catch (Exception e) {
            if (httpConfig.isDebug()) {
                e.printStackTrace();
                LogUtil.e(httpConfig.getTagName(), e.toString());
            }
            if (responseCallback != null)
                responseCallback.onFailure(-1, e.getMessage().getBytes());
        }
        return null;
    }

    private Call postFile(String url, Header[] header, String name, String filename, byte[] fileContent, HttpResponseHandler responseCallback, ProgressUIListener progressUIListener) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            MediaType mediaType = MediaType.parse("application/octet-stream");
            builder.addFormDataPart(name, filename, RequestBody.create(mediaType, fileContent));

            List<NameValuePair> form = new ArrayList<>(2);
            form.addAll(httpConfig.getCommonField());//添加公共字段
            for (NameValuePair item : form) {
                builder.addFormDataPart(item.getName(), item.getValue());
            }

            RequestBody requestBody;
            if (progressUIListener == null) {//不需要回调进度
                requestBody = builder.build();
            } else {//需要回调进度
                requestBody = ProgressHelper.withProgress(builder.build(), progressUIListener);
            }
            Request.Builder requestBuider = new Request.Builder();
            requestBuider.header("content-type", "multipart/form-data");
            requestBuider.header("consumes", "multipart/*");
            requestBuider.url(url);
            requestBuider.post(requestBody);
            return execute(requestBuider, header, responseCallback);
        } catch (Exception e) {
            if (httpConfig.isDebug()) {
                e.printStackTrace();
                LogUtil.e(httpConfig.getTagName(), e.toString());
            }
            if (responseCallback != null)
                responseCallback.onFailure(-1, e.getMessage().getBytes());
        }
        return null;
    }

    private Call execute(Request.Builder builder, Header[] header, Callback responseCallback) {
        boolean hasUa = false;
        if (header == null) {
            builder.header("Connection", "close");
            builder.header("Accept", "*/*");
        } else {
            for (Header h : header) {
                builder.header(h.getName(), h.getValue());
                if (!hasUa && h.getName().equals("User-Agent")) {
                    hasUa = true;
                }
            }
        }
        if (!hasUa && !TextUtils.isEmpty(httpConfig.getUserAgent())) {
            builder.header("User-Agent", httpConfig.getUserAgent());
        }
        Request request = builder.cacheControl(cacheControl).build();
        Call call = client.newCall(request);
        call.enqueue(responseCallback);
        return call;
    }

    /**
     * 带 okhttpClient
     * @param builder
     * @param header
     * @param responseCallback
     * @param okHttpClient
     * @return
     */
    private Call execute(Request.Builder builder, Header[] header, Callback responseCallback, OkHttpClient okHttpClient) {
        boolean hasUa = false;
        if (header == null) {
            builder.header("Connection", "close");
            builder.header("Accept", "*/*");
        } else {
            for (Header h : header) {
                builder.header(h.getName(), h.getValue());
                if (!hasUa && h.getName().equals("User-Agent")) {
                    hasUa = true;
                }
            }
        }
        if (!hasUa && !TextUtils.isEmpty(httpConfig.getUserAgent())) {
            builder.header("User-Agent", httpConfig.getUserAgent());
        }
        Request request = builder.cacheControl(cacheControl).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(responseCallback);
        return call;
    }

    public class DownloadFileResponseHandler implements Callback {
        private String saveFilePath;
        private String fileName;
        private ProgressUIListener progressUIListener;
        private String url;
        private String errorTip;

        public DownloadFileResponseHandler(String url, String saveFilePath, String fileName, ProgressUIListener progressUIListener) {
            this.url = url;
            this.fileName = fileName;
            this.saveFilePath = saveFilePath;
            this.progressUIListener = progressUIListener;
        }

        public DownloadFileResponseHandler(String url, String saveFilePath, String fileName, String errorTip, ProgressUIListener progressUIListener) {
            this.url = url;
            this.fileName = fileName;
            this.saveFilePath = saveFilePath;
            this.errorTip = errorTip;
            this.progressUIListener = progressUIListener;

        }

        @Override
        public void onFailure(Call call, IOException e) {
            clear(url);
            printLog(url + " " + -1 + " " + new String(e.getMessage().getBytes(), StandardCharsets.UTF_8));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            printLog(url + " code:" + response.code());
            clear(url);
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            int code = clone.code();
            if (code == 200) {
                ResponseBody body = clone.body();
                if (body!=null) {
                    String contentType = body.contentType().toString();
                    if (contentType.contains("application/json")) {
                        String json = response.body().toString();
                        ResponseJsonBean responseJsonBean = new Gson().fromJson(json, ResponseJsonBean.class);
                        ToastUtil.show(responseJsonBean.getReason());
                    } else {
                        ResponseBody responseBody = ProgressHelper.withProgress(response.body(), progressUIListener);
                        BufferedSource source = responseBody.source();

//            File outFile = new File(saveFilePath);
//            outFile.delete();
//            outFile.createNewFile();
                        File dirFile = new File(saveFilePath);
                        if (!dirFile.exists()) {
                            dirFile.mkdirs();
                        }
                        String suffix = url.substring(url.lastIndexOf(".") + 1);
                        File outFile = new File(saveFilePath + fileName + "." + suffix);
                        BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                        source.readAll(sink);
                        sink.flush();
                        source.close();
                    }
                }
            }else {
                ToastUtil.show(errorTip);
            }
        }
    }

    public class MyHttpResponseHandler<T> extends HttpResponseHandler {
        private Class<T> clazz;
        private String url;
        private RequestDataCallback<T> callback;
        private Type typeToken;

        public MyHttpResponseHandler(Class<T> clazz, String url, RequestDataCallback<T> callback) {
            this.clazz = clazz;
            this.url = url;
            this.callback = callback;
        }

        public MyHttpResponseHandler(String url, RequestDataCallback<T> callback, Type typeToken) {
            this.url = url;
            this.callback = callback;
            this.typeToken = typeToken;
        }

        @Override
        public void onFailure(int status, byte[] data) {
            String datas = null;
            try {
                clear(url);
                datas = new String(data, StandardCharsets.UTF_8);
                printLog(url + " " + status + " " + datas);
                System.out.println("结果：" + datas);
                T t = null;
                String dataStr = "";
                if (status != -1) {
                    JSONObject jsonObject = new JSONObject(datas);
                    dataStr = jsonObject.getString("data");
                    t = gson.fromJson(dataStr, clazz != null ? clazz : typeToken);
                }
                sendCallback(status, t, data, callback);
                LogUtil.e("HTTPCaller1=" + dataStr);
            } catch (Exception e) {
                e.printStackTrace();
                sendCallback(status, null, data, callback);
            }
        }

        @Override
        public void onSuccess(int status, final Header[] headers, byte[] responseBody) {
            String str = null;
            try {
                clear(url);
                str = new String(responseBody, StandardCharsets.UTF_8);
                printLog(url + " " + status + " " + str);
                String data;
                T t = null;
                if (str.contains("\"data\"")) {
                    JSONObject jsonObject = new JSONObject(str);
                    data = jsonObject.getString("data");
                    t = gson.fromJson(data, clazz != null ? clazz : typeToken);
                    LogUtil.e("HTTPCaller1=" + data);
                }
                sendCallback(status, t, responseBody, callback);
                LogUtil.e("HTTPCaller2=" + str);
            } catch (Exception e) {
                if (httpConfig.isDebug()) {
                    e.printStackTrace();
                    printLog("自动解析错误:" + e.toString());
                }
                sendCallback(status, null, responseBody, callback);
            }
        }
    }

    private void autoCancel(String function) {
        Call call = requestHandleMap.remove(function);
        if (call != null) {
            call.cancel();
        }
    }

    private void add(String url, Call call) {
        add(url, call, true);
    }

    /**
     * 保存请求信息
     *
     * @param url        请求url     * @param call       http请求call
     * @param autoCancel 自动取消
     */
    private void add(String url, Call call, boolean autoCancel) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("?")) {//get请求需要去掉后面的参数
                url = url.substring(0, url.indexOf("?"));
            }
            if (false) {
                autoCancel(url);//如果同一时间对api进行多次请求，自动取消之前的
            }
            requestHandleMap.put(url, call);
        }
    }

    private void clear(String url) {
        if (url.contains("?")) {//get请求需要去掉后面的参数
            url = url.substring(0, url.indexOf("?"));
        }
        requestHandleMap.remove(url);
    }

    private void printLog(String content) {
        if (httpConfig.isDebug()) {
            LogUtil.i(httpConfig.getTagName(), content);
        }
    }

    /**
     * 检查代理
     *
     * @return
     */
    private boolean checkAgent() {
        if (httpConfig.isAgent()) {
            return false;
        } else {
            String proHost = android.net.Proxy.getDefaultHost();
            int proPort = android.net.Proxy.getDefaultPort();
            if (proHost == null || proPort < 0) {
                return false;
            } else {
                LogUtil.i(httpConfig.getTagName(), "有代理,不能访问");
                return true;
            }
        }
    }

    //更新字段值
    public void updateCommonField(String key, String value) {
        httpConfig.updateCommonField(key, value);
    }

    public void removeCommonField(String key) {
        httpConfig.removeCommonField(key);
    }

    public void addCommonField(String key, String value) {
        httpConfig.addCommonField(key, value);
    }

    private <T> void sendCallback(int status, RequestDataCallback<T> callback) {
        sendCallback(status, null, null, callback);
    }

    private <T> void sendCallback(int status, T data, byte[] body, RequestDataCallback<T> callback) {
        CallbackMessage<T> msgData = new CallbackMessage<T>();
        msgData.body = body;
        msgData.status = status;
        msgData.data = data;
        msgData.callback = callback;

        Message msg = handler.obtainMessage();
        msg.obj = msgData;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CallbackMessage data = (CallbackMessage) msg.obj;
            data.callback();
        }
    };

    private class CallbackMessage<T> {
        public RequestDataCallback<T> callback;
        public T data;
        public byte[] body;
        public int status;

        public void callback() {
            if (callback != null) {
                callback.dataCallback(status, data, body);
            }
        }
    }

    public static String byte2Base64String(byte[] data) {
        String base64String = new String(Base64.encodeToString(data, Base64.DEFAULT));
        return base64String.replace("\n", "");
    }
}
