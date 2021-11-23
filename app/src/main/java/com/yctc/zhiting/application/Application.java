package com.yctc.zhiting.application;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseview.BaseApplication;
import com.tencent.bugly.crashreport.CrashReport;
import com.yctc.zhiting.BuildConfig;
import com.yctc.zhiting.activity.ErrorActivity;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.utils.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * date : 2021/4/2720:48
 * desc :
 */
public class Application extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        initUncaughtException();
        initBugly();
    }

    /**
     * Bugly注册
     */
    private void initBugly() {
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(context, "829795a10f", BuildConfig.DEBUG, strategy);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    private void initUncaughtException() {
        if (BuildConfig.DEBUG) {
            Thread.UncaughtExceptionHandler handler = (thread, throwable) -> {
                String errorLogPath = this.getExternalCacheDir().getAbsolutePath() + "/error";
                Log.e("errorLogPath=", errorLogPath);
                String errorLog = FileUtils.writeErrorLog(throwable, errorLogPath);
                Intent intent = new Intent(this, ErrorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ErrorActivity.KEY_ERROR_LOG, errorLog);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            };
            Thread.setDefaultUncaughtExceptionHandler(handler);
        }
    }

    public static void releaseResource() {
        if (BaseApplication.getContext() == null) {
            return;
        }
        DBManager.getInstance(BaseApplication.getContext()).closeDb();
    }
}
