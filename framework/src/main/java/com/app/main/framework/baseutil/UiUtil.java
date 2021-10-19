package com.app.main.framework.baseutil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//类作用描述：1.提供常见的获取屏幕宽高、获取各种资源等方法
//            2.px  dp转换
//            3.提供延时处理的方法
public class UiUtil {

    //手机状态栏的高度
    private static int stateBarHeight;
    //屏幕的高度
    private static int screenHeight;
    //屏幕的宽度
    private static int screenWidth;
    //虚拟导航栏的高度
    private static int navigation_bar_height;

    private static void init(Context context) {

        //获取状态栏的高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            stateBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        //获取屏幕的高度和宽度
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();

        //获取底部导航栏的高度
        int navigationId = 0;
        int rid = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid > 0) {
            navigationId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            navigation_bar_height = getResources().getDimensionPixelSize(navigationId);
        }
    }

    static {
        init(getContext());
    }

    public static Context getContext() {
        return LibLoader.getApplication();
    }


    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * @return 获取导航栏高度
     */
    public static int getNavigation_bar_height() {
        return navigation_bar_height;
    }

    /**
     * @return 状态栏高度
     */
    public static int getStateBarHeight() {
        return stateBarHeight;
    }

    /**
     * @return 获取屏幕高度
     */
    public static int getScreenHeight() {
        return screenHeight;
    }

    /**
     * @return 获取屏幕宽度
     */
    public static int getScreenWidth() {
        return screenWidth;
    }


    public static long getMainThreadId() {
        return LibLoader.getMainThreadId();
    }

    /**
     * dip转换px
     */
    public static int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * px转换dip
     */
    public static int px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 获取主线程的handler
     */
    public static Handler getHandler() {
        return LibLoader.getMainThreadHandler();
    }

    /**
     * 延时在主线程执行runnable
     */
    public static boolean postDelayed(Runnable runnable, long delayMillis) {
        return getHandler().postDelayed(runnable, delayMillis);
    }

    /**
     * 在主线程执行runnable
     */
    public static boolean post(Runnable runnable) {
        return getHandler().post(runnable);
    }

    public static void starThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * 从主线程looper里面移除runnable
     */
    public static void removeCallbacks(Runnable runnable) {
        if(runnable!=null)
        getHandler().removeCallbacks(runnable);
    }

    /**
     * 加载布局
     */
    public static View inflate(int resId) {
        return LayoutInflater.from(getContext()).inflate(resId, null, false);
    }

    /**
     * 加载布局
     */
    public static View inflate(int resId, ViewGroup viewGroup) {
        return LayoutInflater.from(getContext()).inflate(resId, viewGroup, false);
    }


    /**
     * 获取文字
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取文字数组
     */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 获取dimen
     */
    public static int getDimens(int resId) {
        return getResources().getDimensionPixelSize(resId);
    }

    /**
     * 获取drawable
     */
    public static Drawable getDrawable(int resId) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    /**
     * 获取颜色
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }


    //判断当前的线程是不是在主线程
    public static boolean isRunInMainThread() {
        return android.os.Process.myTid() == getMainThreadId();
    }

    public static void runInMainThread(Runnable runnable) {
        if (isRunInMainThread()) {
            runnable.run();
        } else {
            post(runnable);
        }
    }

    /**
     * 判断触点是否落在该View上
     */
    public static boolean isTouchInView(MotionEvent ev, View v) {
        if (ev == null || v == null) {
            return false;
        }

        int[] vLoc = new int[2];
        v.getLocationOnScreen(vLoc);
        float motionX = ev.getRawX();
        float motionY = ev.getRawY();
        return motionX >= vLoc[0] && motionX <= (vLoc[0] + v.getWidth()) && motionY >= vLoc[1] && motionY <= (vLoc[1] + v.getHeight());
    }

    /**
     * 动态设置TextView的drawable
     * type 0=left,1=top,2=right,3=bottom
     */
    public static void setTextViewDrawable(TextView textDrawable, int type, Drawable drawable) {

//        Drawable drawable = null;
//        drawable = getResources().getDrawable(drawableId);

        if (0 == type) {
            textDrawable.setCompoundDrawablesWithIntrinsicBounds(drawable,
                    null, null, null);
        } else if (1 == type) {
            textDrawable.setCompoundDrawablesWithIntrinsicBounds(null,
                    drawable, null, null);
        } else if (2 == type) {
            textDrawable.setCompoundDrawablesWithIntrinsicBounds(null,
                    null, drawable, null);
        } else if (3 == type) {
            textDrawable.setCompoundDrawablesWithIntrinsicBounds(null,
                    null, null, drawable);
        }
        textDrawable.setCompoundDrawablePadding(4);
    }

    /**
     * 右边的图片
     *
     * @param textDrawable
     * @param drawable
     */
    public static void setTextViewDrawable(TextView textDrawable, Drawable drawable) {

//        Drawable drawable = null;
//        drawable = getResources().getDrawable(drawableId);

        textDrawable.setCompoundDrawablesWithIntrinsicBounds(null,
                null, drawable, null);
        textDrawable.setCompoundDrawablePadding(4);
    }

    /**
     * 判断数组是否为空
     */
    public static boolean isAvailable(List<?> list) {
        return null != list && list.size() > 0;
    }

    public static boolean isAvailable(Object object) {
        return null != object;
    }

    public static boolean isAvailable(String str) {
        return !TextUtils.isEmpty(str);
    }

    /**
     * 防快速的点击
     */
    private static final int MIN_DELAY_TIME = 500;  // 两次点击间隔不能少于500ms
    private static long startTime = 0;

    public static boolean isFastClick() {
        long endTime = System.currentTimeMillis();
        long l = endTime - startTime;
        startTime = System.currentTimeMillis();
        return l < MIN_DELAY_TIME;
    }

    /**
     * 设置文本
     *
     * @param editText
     * @param content
     */
    public static void setText(EditText editText, String content) {
        if (TextUtils.isEmpty(content)) return;
        editText.setText(content);
        editText.setSelection(content.length());
    }

    /**
     * 查找的数字字符串
     *
     * @param content
     * @return
     */
    public static String findNumber(String content) {
        if (TextUtils.isEmpty(content)) return "";
        try {
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(content);
            m.find();
            return m.group();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取字符串中的数字，并且返回整形
     * @param content
     * @return
     */
    public static int findNumberToInt(String content) {
        if (TextUtils.isEmpty(content)) return 0;
        try {
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(content);
            m.find();
            if(TextUtils.isEmpty(m.group())){
                return 0;
            }
            return Integer.parseInt(m.group());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 把view转图片保存
     * @param view
     */
    public static void saveViewBitmap(View view, Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/DCIM/Camera/";
                long time = System.currentTimeMillis();
                File dirFile = new File(filePath);
                if (!dirFile.exists()){
                    dirFile.mkdirs();
                }
                File file = new File(filePath, time+".png");

                try {
                    file.createNewFile();
                    FileOutputStream fos = null;
                    fos = new FileOutputStream(file);
                    Bitmap bitmap = createViewBitmap(view);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                    Message msg = Message.obtain();
                    msg.obj = file.getPath();
                    handler.sendMessage(msg);
                    fos.flush();
                    fos.close();

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * view转bitmap
     * @param view
     * @return
     */
    public static Bitmap createViewBitmap(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }
}
