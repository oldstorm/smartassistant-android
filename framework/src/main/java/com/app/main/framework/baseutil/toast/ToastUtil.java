package com.app.main.framework.baseutil.toast;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.UiUtil;

public class ToastUtil {
    private static MToast TOAST;

    public static void init() {
        TOAST = MToast.makeText(LibLoader.getApplication(), "", Toast.LENGTH_SHORT);
    }

    public static void show(@NonNull String message) {
        showCenter(message);
    }

    public static void showTop(String message) {
        if (TextUtils.isEmpty(message)) return;
        TOAST.setText(message)
                .setGravity(Gravity.CENTER | Gravity.TOP, 0, 0)
                .show();
    }

    public static void showTop(@StringRes int resId) {
        showTop(UiUtil.getString(resId));
    }

    public static void showCenter(String message) {
        if (TextUtils.isEmpty(message)) return;
        if (TOAST != null) TOAST.cancel();
        init();
        TOAST.setText(message)
                .setGravity(Gravity.CENTER, 0, 0)
                .show();
    }

    public static void showCenter(@StringRes int resId) {
        showCenter(UiUtil.getString(resId));
    }

    public static void showBottom(String message) {
        if (TextUtils.isEmpty(message)) return;
        TOAST.setText(message)
                .setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0)
                .show();
    }

    public static void showBottom(@StringRes int resId) {
        showBottom(UiUtil.getString(resId));
    }

    public static void showBottom(String message, int yOffset) {
        if (TextUtils.isEmpty(message)) return;
        TOAST.setText(message)
                .setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, yOffset)
                .show();
    }
}
