package com.app.main.framework.httputil.comfig;

import android.text.TextUtils;

import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.NameValuePair;

import java.util.ArrayList;
import java.util.List;

public class HttpConfig {
    private boolean debug = false;//true:debug模式
    private String userAgent = "";//用户代理 它是一个特殊字符串头，使得服务器能够识别客户使用的操作系统及版本、CPU 类型、浏览器及版本、浏览器渲染引擎、浏览器语言、浏览器插件等。
    private boolean agent = true;//有代理的情况能不能访问，true:有代理能访问 false:有代理不能访问
    private String tagName = "Http";

    private int connectTimeout = 30;//连接超时时间 单位:秒
    private int writeTimeout = 30;//写入超时时间 单位:秒
    private int readTimeout = 30;//读取超时时间 单位:秒
    public static final String TOKEN_KEY = "smart-assistant-token";
    public static final String AREA_ID = "Area-ID";

    //通用字段
    private List<NameValuePair> commonField = new ArrayList<>();
    private static List<Header> headers = new ArrayList<>();

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isAgent() {
        return agent;
    }

    public void setAgent(boolean agent) {
        this.agent = agent;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<NameValuePair> getCommonField() {
        return commonField;
    }

    public Header[] getHeaders() {
        return headers.toArray(new Header[headers.size()]);
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * 更新参数
     *
     * @param key
     * @param value
     */
    public void updateCommonField(String key, String value) {
        boolean result = true;
        for (int i = 0; i < commonField.size(); i++) {
            NameValuePair nameValuePair = commonField.get(i);
            if (nameValuePair.getName().equals(key)) {
                commonField.set(i, new NameValuePair(key, value));
                result = false;
                break;
            }
        }
        if (result) {
            commonField.add(new NameValuePair(key, value));
        }
    }

    /**
     * 删除公共参数
     *
     * @param key
     */
    public void removeCommonField(String key) {
        for (int i = commonField.size() - 1; i >= 0; i--) {
            if (commonField.get(i).equals(key)) {
                commonField.remove(i);
            }
        }
    }

    /**
     * 添加请求参数
     *
     * @param key
     * @param value
     */
    public void addCommonField(String key, String value) {
        commonField.add(new NameValuePair(key, value));
    }

    /**
     * 默认一个token
     *
     * @param value
     */
    public static void addHeader(String value) {
        if (!TextUtils.isEmpty(value)) {
            headers.clear();
            headers.add(new Header(TOKEN_KEY, value));
        }
    }

    public static void addHeader(String name, String value) {
        if (!TextUtils.isEmpty(value)) {
            headers.clear();
            headers.add(new Header(name, value));
        }
    }

    public static void addAreaIdHeader(String name, String value) {
        if (!TextUtils.isEmpty(value)) {
            if (headers != null && headers.size() > 0) {
                for (int i = 0; i < headers.size(); i++) {
                    Header header = headers.get(i);
                    if (header.getName().equalsIgnoreCase(name)) {
                        header.setValue(value);
                        return;
                    }
                }
            }
            headers.add(new Header(name, value));
        }
    }

    public static void clearHeader() {
        headers.clear();
    }

    public static void clearHear(String name) {
        if (headers == null || headers.size() == 0) return;
        for (Header header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                headers.remove(header);
                return;
            }
        }
    }
}
