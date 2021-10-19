package com.app.main.framework.httputil.download;

public class DownloadListener {
    /**
     *  开始下载
     */
    public void start(long max){}
    /**
     *  正在下载
     */
    public void loading(int progress){}
    /**
     *  下载完成
     */
    public void complete(String path){}
    /**
     *  请求失败
     */
    public void fail(int code, String message){}
    /**
     *  下载过程中失败
     */
    public void loadfail(String message){}
}