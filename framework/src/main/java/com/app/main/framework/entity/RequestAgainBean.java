package com.app.main.framework.entity;

import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.HttpResponseHandler;

import okhttp3.Request;

public class RequestAgainBean {
    private Request.Builder builder;
    private Header[] header;
    private HttpResponseHandler responseCallback;

    public RequestAgainBean(Request.Builder builder, Header[] header, HttpResponseHandler responseCallback) {
        this.builder = builder;
        this.header = header;
        this.responseCallback = responseCallback;
    }

    public Request.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Request.Builder builder) {
        this.builder = builder;
    }

    public Header[] getHeader() {
        return header;
    }

    public void setHeader(Header[] header) {
        this.header = header;
    }

    public HttpResponseHandler getResponseCallback() {
        return responseCallback;
    }

    public void setResponseCallback(HttpResponseHandler responseCallback) {
        this.responseCallback = responseCallback;
    }
}
