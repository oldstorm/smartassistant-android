package com.yctc.zhiting.utils;

import android.content.Context;
import android.text.TextUtils;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;

import java.io.File;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class LubanUtil {

    public static void compressImage(Context context, String path, OnCompressListener compressListener) {
        UiUtil.starThread(new Runnable() {
            @Override
            public void run() {
                Luban.with(context)
                        .load(path)
                        .ignoreBy(100)
                        .setTargetDir(context.getCacheDir().getAbsolutePath())
                        .filter(new CompressionPredicate() {
                            @Override
                            public boolean apply(String path) {
                                return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                            }
                        })
                        .setCompressListener(compressListener).launch();
            }
        });
    }
}
