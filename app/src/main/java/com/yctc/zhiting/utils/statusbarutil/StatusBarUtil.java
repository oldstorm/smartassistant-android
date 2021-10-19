package com.yctc.zhiting.utils.statusbarutil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StatusBarUtil {
    public final static int TYPE_MIUI = 0;
    public final static int TYPE_FLYME = 1;
    public final static int TYPE_M = 3;//6.0

    @IntDef({TYPE_MIUI, TYPE_FLYME, TYPE_M})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param colorId 颜色
     */
    public static void setStatusBarColor(Activity activity, int colorId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(colorId);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTintManager,需要先将状态栏设置为透明
            setTranslucentStatus(activity);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(activity);
            systemBarTintManager.setStatusBarTintEnabled(true);//显示状态栏
            systemBarTintManager.setStatusBarTintColor(colorId);//设置状态栏颜色
        }
    }

    /**
     * 设置状态栏透明
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //导航栏颜色也可以正常设置
            //window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
//            int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
//            attributes.flags |= flagTranslucentNavigation;
            window.setAttributes(attributes);
        }
    }


    /**
     * 代码实现android:fitsSystemWindows
     * 当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
     *
     * @param activity
     */
    public static void setRootViewFitsSystemWindows(Activity activity, boolean fitSystemWindows) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && activity != null) {
                ViewGroup winContent = (ViewGroup) activity.findViewById(android.R.id.content);
                if (winContent != null && winContent.getChildCount() > 0) {
                    ViewGroup rootView = (ViewGroup) winContent.getChildAt(0);
                    if (rootView != null) {
                        rootView.setFitsSystemWindows(fitSystemWindows);
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("setRootViewFitsSystemWindows is error");
        }
    }

    /**
     * 设置状态栏文字深色浅色切换
     */
    public static boolean setStatusBarDarkTheme(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setStatusBarFontIconDark(activity, TYPE_M, dark);
            } else if (OSUtils.isMiui()) {
                setStatusBarFontIconDark(activity, TYPE_MIUI, dark);
            } else if (OSUtils.isFlyme()) {
                setStatusBarFontIconDark(activity, TYPE_FLYME, dark);
            } else {//其他情况
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 设置状态栏文字深色浅色切换
     */
    public static boolean setStatusBarFontIconDark(Activity activity, @ViewType int type, boolean dark) {
        switch (type) {
            case TYPE_MIUI:
                return setMiuiUI(activity, dark);
            case TYPE_FLYME:
                return setFlymeUI(activity, dark);
            case TYPE_M:
            default:
                return setCommonUI(activity, dark);
        }
    }

    //设置6.0 状态栏深色浅色切换
    public static boolean setCommonUI(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (dark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                if (decorView.getSystemUiVisibility() != vis) {
                    decorView.setSystemUiVisibility(vis);
                }
                return true;
            }
        }
        return false;

    }

    //设置Flyme 状态栏深色浅色切换
    public static boolean setFlymeUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //设置MIUI 状态栏深色浅色切换
    public static boolean setMiuiUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            Class<?> clazz = activity.getWindow().getClass();
            @SuppressLint("PrivateApi") Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getDeclaredMethod("setExtraFlags", int.class, int.class);
            extraFlagField.setAccessible(true);
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    //检查流行机型是否存在刘海屏
    public static boolean isNotch(Context context) {
        //return isNotch_VIVO(context) || isNotch_OPPO(context) || isNotch_HUAWEI(context) || isNotch_XIAOMI(context);{
        // android  P 以上有标准 API 来判断是否有刘海屏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            DisplayCutout displayCutout = activity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
//            if (displayCutout != null) {
//                // 说明有刘海屏
//                return true;
//            }
//        } else {
        //通过其他方式判断是否有刘海屏  目前官方提供有开发文档的就 小米，vivo，华为（荣耀），oppo
        String manufacturer = Build.MANUFACTURER;

        if (TextUtils.isEmpty(manufacturer)) {
            return false;
        } else if (manufacturer.equalsIgnoreCase("HUAWEI")) {
            return isNotch_HUAWEI(context);
        } else if (manufacturer.equalsIgnoreCase("xiaomi")) {
            return isNotch_XIAOMI(context);
        } else if (manufacturer.equalsIgnoreCase("oppo")) {
            return isNotch_OPPO(context);
        } else if (manufacturer.equalsIgnoreCase("vivo")) {
            return isNotch_VIVO(context);
        } else {
            return false;
        }
//        }
    }

    //检查vivo是否存在刘海屏、水滴屏等异型屏
    public static boolean isNotch_VIVO(Context context) {
        boolean isNotch = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class cls = cl.loadClass("android.util.FtFeature");
            Method method = cls.getMethod("isFeatureSupport", int.class);
            isNotch = (boolean) method.invoke(cls, 0x00000020);//0x00000020：是否有刘海  0x00000008：是否有圆角
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            return isNotch;
        }
    }

    //检查oppo是否存在刘海屏、水滴屏等异型屏
    public static boolean isNotch_OPPO(Context context) {
        boolean isNotch = false;
        try {
            isNotch = context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return isNotch;
        }
    }

    //检查huawei是否存在刘海屏、水滴屏等异型屏
    public static boolean isNotch_HUAWEI(Context context) {
        boolean isNotch = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class cls = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method method = cls.getMethod("hasNotchInScreen");
            isNotch = (boolean) method.invoke(cls);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return isNotch;
        }
    }

    //检查xiaomi是否存在刘海屏、水滴屏等异型屏
    public static boolean isNotch_XIAOMI(Context context) {
        boolean isNotch = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class cls = cl.loadClass("android.os.SystemProperties");
            Method method = cls.getMethod("getInt", String.class, int.class);
            isNotch = ((int) method.invoke(null, "ro.miui.notch", 0) == 1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            return isNotch;
        }
    }

    //获取huawei刘海屏、水滴屏的宽度和高度：int[0]值为刘海宽度 int[1]值为刘海高度
    public static int[] getNotchSize_HUAWEI(Context context) {
        int[] notchSize = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class cls = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method method = cls.getMethod("getNotchSize");
            notchSize = (int[]) method.invoke(cls);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return notchSize;
        }
    }

    //获取xiaomi刘海屏、水滴屏的宽度和高度：int[0]值为刘海宽度 int[1]值为刘海高度
    public static int[] getNotchSize_XIAOMI(Context context) {
        int[] notchSize = new int[]{0, 0};
        if (isNotch_XIAOMI(context)) {
            int resourceWidthId = context.getResources().getIdentifier("notch_width", "dimen", "android");
            if (resourceWidthId > 0) {
                notchSize[0] = context.getResources().getDimensionPixelSize(resourceWidthId);
            }
            int resourceHeightId = context.getResources().getIdentifier("notch_height", "dimen", "android");
            if (resourceHeightId > 0) {
                notchSize[1] = context.getResources().getDimensionPixelSize(resourceHeightId);
            }
            //小米9获取不到刘海高度 所以用状态栏高度减30来作为刘海高度
            if (0 == notchSize[1]) {
                int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    int dimensionPixelSize = context.getResources().getDimensionPixelSize(resourceId) - 30;
                    if (dimensionPixelSize >= 0) {
                        notchSize[1] = dimensionPixelSize;
                    }
                }
            }
        }
        return notchSize;
    }

    //获取vivo、oppo刘海屏、水滴屏的高度：官方没有给出标准的获取刘海高度的API，由于大多情况是：状态栏≥刘海，因此此处获取刘海高度采用状态栏高度
    public static int getNotchHeight(Context context) {
        int notchHeight = 0;
        if (isNotch_HUAWEI(context)) {
            notchHeight = getNotchSize_HUAWEI(context)[1];
        } else if (isNotch_XIAOMI(context)) {
            notchHeight = getNotchSize_XIAOMI(context)[1];
        } else if (isNotch_VIVO(context) || isNotch_OPPO(context)) {
            //若不想采用状态栏高度作为刘海高度或者可以采用官方给出的刘海固定高度：vivo刘海固定高度：27dp（need dp2px）  oppo刘海固定高度：80px
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                notchHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return notchHeight;
    }

    public static void fitNotchScreen(Context context, View topView) {
        if (isNotch(context)) {
            topView.setPadding(
                    topView.getPaddingStart(),
                    topView.getPaddingTop() / 4 + getNotchHeight(context),
                    topView.getPaddingEnd(),
                    topView.getPaddingBottom()
            );
        }
    }
}

