package com.app.main.framework.baseutil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.LocaleList;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PermissionChecker;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.toast.ToastUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * android客户端工具类
 */
public class AndroidUtil {

    private static final String TAG = "AndroidUtil";
    public static final String NET_WIFI = "WIFI";
    private static final String NET_4G = "4G";
    private static final String NET_3G = "3G";
    private static final String NET_2G = "2G";
    private static final String NET_UNKNOWN = "UNKNOWN";

    /**
     * 获取手机唯一序列号
     * 注：如取不到设备号，则取UUID作为手机唯一序列号
     */
    public static String getDeviceUUID() {
        return Installation.id(UiUtil.getContext());
    }

    /**
     * 获取手机序列号
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getDeviceId() {
        String deviceID = null;

        TelephonyManager tm = (TelephonyManager) UiUtil.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(UiUtil.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        if (!TextUtils.isEmpty(tm.getDeviceId())) {
            deviceID = tm.getDeviceId();
        }

        return deviceID;
    }

    public static void copyText(String text) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) UiUtil.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastUtil.showCenter(R.string.copy_success);
    }

    /**
     * 获取当前系统语言
     *
     * @return
     */
    public static String getLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else locale = Locale.getDefault();
        return locale.getLanguage() + "-" + locale.getCountry();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = UiUtil.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 说明：根据手机的分辨率将sp转成为px
     */
    public static int sp2px(float spValue) {
        final float fontScale = UiUtil.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = UiUtil.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取应用的ApplicationId
     */
    public static String getApplicationId() {
        try {
            return UiUtil.getContext().getPackageName();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 取得操作系统版本号
     */
    public static String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取应用版本号
     */
    public static String getAppVersion() {
        String strVersion = null;

        try {
            PackageInfo pi = null;
            pi = UiUtil.getContext().getPackageManager().getPackageInfo(UiUtil.getContext().getPackageName(), 0);
            if (pi != null) {
                strVersion = pi.versionName;
            }
        } catch (NameNotFoundException e) {
            LogUtil.e(e.getMessage(), e);
        }

        return strVersion;
    }

    /**
     * 获取签名摘要
     */
    public static String getSign() {
        String strSign = null;
        try {
            int flag = PackageManager.GET_SIGNATURES;
            PackageManager pm = UiUtil.getContext().getPackageManager();
            List<PackageInfo> apps = pm.getInstalledPackages(flag);
            Object[] objs = apps.toArray();
            for (int i = 0, j = objs.length; i < j; i++) {
                PackageInfo packageinfo = (PackageInfo) objs[i];
                String packageName = packageinfo.packageName;
                if (packageName.equals(UiUtil.getContext().getPackageName())) {
                    Signature[] temps = packageinfo.signatures;
                    Signature tmpSign = temps[0];
                    strSign = tmpSign.toCharsString();
                }
            }
        } catch (Exception e) {
        }
        return strSign;
    }

    /**
     * 判断手机是否ROOT
     */
    public static boolean isSystemRoot() {
        boolean isRoot = false;
        try {
            isRoot = (new File("/system/bin/su").exists())
                    || (new File("/system/xbin/su").exists());
            LogUtil.d("isRoot  = " + isRoot);
        } catch (Exception e) {

        }
        return isRoot;
    }

    public static String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }

    /**
     * 网络类型判断
     */
    public static String getNetworkType() {
        String strNetworkType = NET_UNKNOWN;
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = ((ConnectivityManager) UiUtil.getContext().getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = NET_WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                // TD-SCDMA networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by
                        strNetworkType = NET_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by 15
                        strNetworkType = NET_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by 13
                        strNetworkType = NET_4G;
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA")
                                || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = NET_3G;
                        } else {
                            strNetworkType = _strSubTypeName;
                        }
                        break;
                }
            }
        }
        return strNetworkType;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 判断应用是否在后台
     */
    public static boolean isAppIsInBackground() {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) UiUtil.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            //前台程序
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(UiUtil.getContext().getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }
        return isInBackground;
    }

    public static String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) UiUtil.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 关闭系统键盘
     */
    public static void closeKeyBoards(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getApplicationWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 关闭系统键盘
     *
     * @param view
     */
    public static void closeKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            view.findFocus();
            view.requestFocus();
        }

    }

    public static void hideSoftInput(Activity activity) {
        try {
            if (activity != null && activity.getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) activity
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                            .getApplicationWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 动态显示软键盘
     *
     * @param context 上下文
     * @param edit    输入框
     */
    public static void showSoftInput(final Context context, final EditText edit) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                edit.setFocusable(true);
                edit.setFocusableInTouchMode(true);
                edit.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(edit, 0);
                edit.setSelection(edit.getText().length());
            }
        }, 200);

    }

    public static void installApk(Activity activity, String path) {
        try {
            Intent intent = new Intent();
            File file = new File(path);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (checkIsHuaWeiRom() || checkIsSamSungRom())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //String authority = "com.yctc.zhiting.provider";
                String authority = activity.getApplication().getPackageName()+".provider";
                Uri fileUri = FileProvider.getUriForFile(LibLoader.getApplication(), authority, file);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            } else {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            activity.startActivityForResult(intent, 0x007);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否华为
     */
    public static boolean checkIsHuaWeiRom() {
        return Build.MANUFACTURER.contains("HUAWEI");
    }

    /**
     * 判断是否是三星
     *
     * @return
     */
    public static boolean checkIsSamSungRom() {
        return Build.MANUFACTURER.contains("samsung");
    }

    public static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] permissionManifest = {
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES};

    /**
     * 判断是否有安装的权限
     */
    @SuppressLint("WrongConstant")
    public static boolean hasInstallPermission() {
        //小于23，不需要权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        for (int i = 0; i < permissionManifest.length; i++) {
            String permission = permissionManifest[i];
            if (PermissionChecker.checkSelfPermission(LibLoader.getApplication(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请安装的权限
     *
     * @param activity
     * @return
     */
    public static void applyInstallPermission(@NonNull Activity activity) {
        ActivityCompat.requestPermissions(activity, permissionManifest, PERMISSION_REQUEST_CODE);
    }

    /**
     * 比较版本号
     *
     * @param presetVersion 当前版本号
     * @param minVersion    最低版本号
     * @return
     */
    public static int compareVersion(String presetVersion, String minVersion) {
        if (TextUtils.isEmpty(presetVersion) || TextUtils.isEmpty(minVersion)) return 0;
        int presentLen = presetVersion.length();
        int minLen = minVersion.length();
        int i = 0, j = 0;
        while (i < presentLen || j < minLen) {
            int x = 0;
            for (; i < presentLen && presetVersion.charAt(i) != '.'; ++i) {
                x = x * 10 + presetVersion.charAt(i) - '0';
            }
            ++i;  // 跳过点号
            int y = 0;
            for (; j < minLen && minVersion.charAt(j) != '.'; ++j) {
                y = y * 10 + minVersion.charAt(j) - '0';
            }
            ++j; // 跳过点号
            if (x != y) {
                return x > y ? 1 : -1;
            }
        }
        return 0;
    }

    public static int checkUpdateInfo(boolean isForce, String currentVersion, String minVersion, String maxVersion) {
        int updateType = UpdateType.NONE;
        if (isForce) {
            boolean isUpgrade = AndroidUtil.compareVersion(currentVersion, minVersion) < 0;
            if (isUpgrade) {
                updateType = UpdateType.FORCE;
            } else {
                isUpgrade = AndroidUtil.compareVersion(currentVersion, maxVersion) < 0;
                if (isUpgrade) {
                    updateType = UpdateType.ORDINARY;
                }
            }
        } else {
            boolean isUpgrade = AndroidUtil.compareVersion(currentVersion, maxVersion) < 0;
            if (isUpgrade) {
                updateType = UpdateType.ORDINARY;
            }
        }
        return updateType;
    }

    /**
     * 更新类型
     */
    public interface UpdateType {
        // 不需操作
        int NONE = 0;
        // 普通更新
        int ORDINARY = 1;
        // 强制更新
        int FORCE = 2;
    }

    /**
     * 当前系统是否大于等于9
     *
     * @return
     */
    public static boolean isGE9() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * 当前是否是鸿蒙系统
     * 根据是否能调用Harmony JAVA API判断
     */
    public static boolean isHarmonyOs() {
        try {
            Class cls = Class.forName("ohos.utils.system.SystemCapability");
            return cls != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 鸿蒙系统版本号
     *
     * @return
     */
    public static String getHarmonyOSVersion() {
        if (checkIsHuaWeiRom() && isHarmonyOs()) {
            try {
                Class cls = Class.forName("android.os.SystemProperties");
                Method method = cls.getMethod("get", String.class);
                String version = (String) method.invoke(cls, "ro.huawei.build.display.id");
                return version;
            } catch (Exception e) {
                return "-1";
            }
        } else {
            return "-1";
        }
    }
}
