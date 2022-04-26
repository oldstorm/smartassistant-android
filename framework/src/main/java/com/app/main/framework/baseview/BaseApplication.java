package com.app.main.framework.baseview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.httputil.cookie.CookieStore;

public abstract class BaseApplication extends Application {
    private ActivityLifecycleCallbacks callbacks;

    private static Context context;

    private static CookieStore cookieStore;

    @Override
    public void onCreate() {
        super.onCreate();
        LibLoader.init(this);
        context = this;
        initActivityListener();
        initHttpClient();
        initUtils();
    }

    public static Context getContext(){
        return context;
    }

    //工具的处于实话
    protected void initUtils() {
        ToastUtil.init();
    }

    private void initHttpClient() {
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.setAgent(true);//有代理的情况能不能访问
        httpConfig.setDebug(true);//是否debug模式 如果是debug模式打印log
        httpConfig.setTagName("zhiting");//打印log的tagname
        //初始化HTTPCaller类
        HTTPCaller.getInstance().setHttpConfig(httpConfig);
        HTTPCaller.getInstance().initHttpConfig();
    }

    private void initActivityListener() {
        callbacks = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LibLoader.addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LibLoader.removeActivity(activity);
            }
        };
        this.registerActivityLifecycleCallbacks(callbacks);
    }
}
