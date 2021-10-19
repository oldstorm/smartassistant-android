package com.yctc.zhiting.websocket;

import androidx.annotation.Nullable;

import okhttp3.Response;
import okhttp3.WebSocket;

/**
 *
 * date : 2021/5/1915:28
 * desc :
 */
public class IWebSocketListener {
    //连接成功
    public void onOpen(WebSocket webSocket, Response response) {
    }

    //后台发送消息
    public void onMessage(WebSocket webSocket, String text) {
    }

    //连接失败
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
    }
}
