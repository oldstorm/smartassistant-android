package com.app.main.framework.baseutil;

import android.util.Log;

import com.app.main.framework.BuildConfig;

/**
 * 安卓系统日志打印
 */
public class LogUtil {
    private static final String TAG = "ZhiTing";
    public static boolean allowD = BuildConfig.DEBUG;
    public static boolean allowE = BuildConfig.DEBUG;;
    public static boolean allowI = BuildConfig.DEBUG;;
    public static boolean allowV = BuildConfig.DEBUG;;
    public static boolean allowW = BuildConfig.DEBUG;;

    /**
     * 打印超大日志
     * @param message
     */
    public static void logMsg(String message) {
        int length = message.length();
        if (length >= 2000) {
            int logNum;
            for(logNum = 0; length - logNum >= 2000; logNum += 2000) {
                String tmp = message.substring(logNum, logNum + 2000);
                Log.e(TAG, "│ " + tmp);
            }

            Log.e(TAG, "│ " + message.substring(logNum, length));
        } else {
            Log.e(TAG, "│ " + message);
        }

    }

    public static void logEnterMsg(String message) {
    }

    public static void logBottom() {
    }

    public static void logI(String tag, String message) {
    }

    public static void logE(String tag, String message) {
    }

    private static String generateTag(StackTraceElement caller) {
        String tag = "tag:%s.%s(Line:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        return tag;
    }

    public static void d(String content) {
        if (allowD) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.d(tag, content);
        }
    }

    public static void d(String content, Throwable tr) {
        if (allowD) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.d(tag, content, tr);
        }
    }

    public static void e(String content) {
        if (allowE) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.e(tag, content);
        }
    }

    public static void e(String tag, String content) {
        if (allowE) {
            Log.e(tag, content);
        }
    }

    public static void e(String content, Throwable tr) {
        if (allowE) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.e(tag, content, tr);
        }
    }

    public static void i(String content) {
        if (allowI) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.i(tag, content);
        }
    }

    public static void i(String content, Throwable tr) {
        if (allowI) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.i(tag, content, tr);
        }
    }

    public static void i(String tag,String content) {
        if (allowI) {
            Log.i(tag, content);
        }
    }

    public static void v(String content) {
        if (allowV) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.v(tag, content);
        }
    }

    public static void v(String content, Throwable tr) {
        if (allowV) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.v(tag, content, tr);
        }
    }

    public static void w(String content) {
        if (allowW) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.w(tag, content);
        }
    }

    public static void w(String content, Throwable tr) {
        if (allowW) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.w(tag, content, tr);
        }
    }

    public static void w(Throwable tr) {
        if (allowW) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            Log.w(tag, tr);
        }
    }

    public static void myD(String tag, String content) {
        if (allowW) {
            Log.d(tag, content);
        }
    }

    public static void myE(String tag, String content) {
        if (allowW) {
            Log.e(tag, content);
        }
    }

    public static void myI(String tag, String content) {
        if (allowW) {
            Log.i(tag, content);
        }
    }

    public static void myV(String tag, String content) {
        if (allowW) {
            Log.v(tag, content);
        }
    }

    public static void myW(String tag, String content) {
        if (allowW) {
            Log.w(tag, content);
        }
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }
}
