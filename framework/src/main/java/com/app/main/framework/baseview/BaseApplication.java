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
        //HTTPCaller.getInstance().getHttpConfig().setDebug(BuildConfig.DEBUG);
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
//        httpConfig.addHeader("smart-assistant-token", "aaaa");
//        httpConfig.addHeader("smart-assistant-token", "MTYyMTIyNDc3NHxOd3dBTkVJMVZra3lWRXcxUWs0MFNsSTFXVTlTTjFFMlVFSTBVVnBaU2xwTVYxVllXRFpaVmxoVldWVlJUMWRIU1RSUFdFZzFVRUU9fMVgwp5VltADX8A9FSU6ptqPCem2SSge7h6b2Wgj97oi");
//        httpConfig.addHeader("smart-assistant-token", "MTYyMTU4NjA4NHxOd3dBTkVGRFVGcGFSVkZHVDFZelRETk9XVVJJVmtoRVJUVlJTRWhVVTA1Wk56UkdTVEpMVjBrM1MwbzBVVTFQVmtkQlVVOHpORUU9fOzGdHuMv-0wzhyi3sAE63r-ZLysEPZf3bgKAe7o2ZIE");
//        httpConfig.addHeader("smart-assistant-token", "MTYyMTIxODM4OHxOd3dBTkZOWFZUVkRTRTlLVDFGS1JrRklRa2MwUkVnMk16WkhXRUpSUmpKRFVVVk9NMXBCU0VGTFZGWTBUak5VUVVFME5rUlFTMUU9fBfLt9mHlbXXlmGsFCcz7KwSBErLxlk9jC4SHVaz-Bxp");
//        httpConfig.addHeader("smart-assistant-token", "MTYyMDM3ODExNXxOd3dBTkROWVJFY3pVbFJIUVZaQlJFTlJTVXBKU2xOTlRsVkNSRmcyUzFCS1NWRTJRVTlWUVRWWldUZElVbEJCUmxsUlNGZE5SMEU9fKoRgLHoHf2jk3tWWfbN9tQB4IouWYtgLoi9BoOeRbT2");
//        SpUtil.init(BaseApplication.getContext());
//        httpConfig.addHeader("smart-assistant-token", SpUtil.get(SpConstant.SA_TOKEN));
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
