package com.app.main.framework.httputil.download;

import android.os.Process;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.fileutil.BaseFileUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：wl on 2017/4/13 09:39
 * 邮箱：wangl@ixinyongjia.com
 */
public class DownLoadUtil {
    private String downloadUrl;
    private String fileName;
    private String MD5;
    private DownloadListener lisener;
    private Thread downloadThread;

    public DownLoadUtil(String targetUrl, String fName,String MD5, DownloadListener callback) {
        this.MD5 = MD5;
        downloadUrl = targetUrl;
        fileName = fName;
        lisener = callback;
        initDownLoadThread();
    }

    private void download(String url, final DownloadListener downloadListener, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .header("RANGE", "bytes=" + (long) 0 + "-")//断点续传
                .build();

        // 重写ResponseBody监听请求
        Interceptor interceptor = chain -> {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .body(new DownloadResponBody(originalResponse, (long) 0, downloadListener))
                    .build();
        };
        OkHttpClient.Builder dlOkhttp = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor);
        // 发起请求
        Call call = dlOkhttp.build().newCall(request);
        call.enqueue(callback);
    }

    private void initDownLoadThread() {
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                download(downloadUrl, lisener, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        lisener.loadfail(e.getMessage());
                        LogUtil.e("DownLoadUtil3==="+e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.body() == null){
                            lisener.loadfail("respond body null");
                            return;
                        }
                        long length = response.body().contentLength();
                        String dir = BaseFileUtil.getFilePath(fileName);
                        File file = new File(dir);
                        String fileMD5 = BaseFileUtil.getFileMD5(file);
                        if (file.exists() && fileMD5 != null && fileMD5.equalsIgnoreCase(MD5)){
                            lisener.complete(dir);
                            return;
                        }
                        if (length == 0) {
                            // 说明文件已经下载完，直接跳转安装就好
                            lisener.complete(dir);
                            return;
                        }
                        lisener.start(length);
                        // 保存文件到本地
                        InputStream is = null;
                        RandomAccessFile randomAccessFile = null;
                        BufferedInputStream bis = null;

                        byte[] buff = new byte[2048];
                        int len;
                        try {
                            is = response.body().byteStream();
                            bis = new BufferedInputStream(is);
                            // 随机访问文件，可以指定断点续传的起始位置
                            randomAccessFile = new RandomAccessFile(dir, "rwd");
                            randomAccessFile.seek(0);
                            while ((len = bis.read(buff)) != -1) {
                                randomAccessFile.write(buff, 0, len);
                            }

                            // 下载完成
                            lisener.complete(dir);
                        } catch (Exception e) {
                            e.printStackTrace();
                            lisener.loadfail(e.getMessage());
                            LogUtil.e("DownLoadUtil1==="+e.getMessage());
                        } finally {
                            try {
                                if (is != null) {
                                    is.close();
                                }
                                if (bis != null) {
                                    bis.close();
                                }
                                if (randomAccessFile != null) {
                                    randomAccessFile.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    public void startDownload() {
        if (downloadThread != null && !downloadThread.isAlive()) {
            downloadThread.start();
        } else {
            initDownLoadThread();
            downloadThread.start();
        }
    }

    public void stopDownLoad() {
        if (downloadThread != null && downloadThread.isAlive()) {
            downloadThread.interrupt();
        }
    }


    /**
     * 从输入流中获取字节数组
     */
    public byte[] readInputStream(InputStream inputStream) {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            if (lisener != null) {
                UiUtil.post(() -> lisener.loadfail(e.getMessage()));
                LogUtil.e("DownLoadUtil2==="+e.getMessage());
            }
        }

        return bos.toByteArray();
    }
}
