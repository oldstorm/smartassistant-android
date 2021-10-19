package com.app.main.framework.httputil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * @ClassName: HttpResponseHandler
 */
public abstract class HttpResponseHandler implements Callback {
    public HttpResponseHandler() {
    }

    /**
     * @param call
     * @param e
     */
    public void onFailure(Call call, IOException e) {
        if (e != null && e.getMessage() != null && e.getMessage().getBytes() != null) {
            onFailure(-1, e.getMessage().getBytes());
        }
    }

    /**
     * @param call
     * @param response
     * @throws IOException
     */
    public void onResponse(Call call, Response response) throws IOException {
        if (null == response || null == response.body()) return;
        try {
            int code = response.code();
            byte[] body = response.body().bytes();
            if (code > 299) {
                onFailure(response.code(), body);
            } else {
                Headers headers = response.headers();
                Header[] hs = new Header[headers.size()];

                for (int i = 0; i < headers.size(); i++) {
                    hs[i] = new Header(headers.name(i), headers.value(i));
                }
                onSuccess(code, hs, body);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onFailure(int status, byte[] data) {
    }

    public abstract void onSuccess(int statusCode, Header[] headers, byte[] responseBody);
}
