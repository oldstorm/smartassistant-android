package com.app.main.framework.baseutil;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public interface ScreenUtil {

    //检查流行机型是否存在刘海屏
    static boolean isNotch(Context context) {
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
    static boolean isNotch_VIVO(Context context) {
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
    static boolean isNotch_OPPO(Context context) {
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
    static boolean isNotch_HUAWEI(Context context) {
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
    static boolean isNotch_XIAOMI(Context context) {
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
    static int[] getNotchSize_HUAWEI(Context context) {
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
    static int[] getNotchSize_XIAOMI(Context context) {
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
    static int getNotchHeight(Context context) {
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

    static void fitNotchScreen(Context context, View topView) {
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
