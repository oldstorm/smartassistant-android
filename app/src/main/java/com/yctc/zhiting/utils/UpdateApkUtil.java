package com.yctc.zhiting.utils;

import android.text.TextUtils;
import android.util.Log;

import com.yctc.zhiting.BuildConfig;
import com.yctc.zhiting.config.Constant;

public class UpdateApkUtil {
    private final static String DOT = "\\.";

    /**
     * 检测是否需要升级
     *
     * @param minVersion  最小支持版本
     * @param lastVersion 最后支持版本
     */
    public static int checkUpdateInfo(String minVersion,
                                      String lastVersion) {

        if (TextUtils.isEmpty(minVersion)) {
            Log.i("MARK",
                    "checkUpdateInfo: min is null.");
            return Constant.UpdateType.NONE;
        }

        if (TextUtils.isEmpty(lastVersion)) {
            Log.i("MARK",
                    "checkUpdateInfo: last is null.");
            return Constant.UpdateType.NONE;
        }

        String versionName = BuildConfig.VERSION_NAME;

        // 如果最新的版本和当前的版本一致，则不需要更新
        if (lastVersion.equals(versionName)) {
            return Constant.UpdateType.NONE;
        }

        // 如果最小支持版本和当前的版本一致，则需要提示更新
        if (minVersion.equals(versionName)) {
            return Constant.UpdateType.ORDINARY;
        }

        String[] minVersionArray = minVersion.split(DOT);
        String[] lastVersionArray = lastVersion.split(DOT);

        if (minVersionArray.length < 3) {
            Log.i("MARK",
                    "checkUpdateInfo: min length not enough:" + minVersionArray.length);
            return Constant.UpdateType.NONE;
        }

        if (lastVersionArray.length < 3) {
            Log.i("MARK",
                    "checkUpdateInfo: last length not enough:" + lastVersionArray.length);
            return Constant.UpdateType.NONE;
        }


        String[] curVersionArray = versionName.split(DOT);

        if (curVersionArray.length < 3) {
            Log.i("MARK",
                    "checkUpdateInfo: cur length not enough:" + curVersionArray.length);
            return Constant.UpdateType.NONE;
        }

        try {
            for (int i = 0; i < 3; i++) {
                String minVer = minVersionArray[i];
                String curVer = curVersionArray[i];

                int minVerNum = Integer.parseInt(minVer);
                int curVerNum = Integer.parseInt(curVer);

                // 当前版本 大于 最小支持版本，可以立马终止循环，
                // 当前版本 小于 最小支持版本，则强制更新
                if (curVerNum > minVerNum) {
                    break;
                } else if (curVerNum < minVerNum) {
                    return Constant.UpdateType.FORCE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < 3; i++) {
                String lastVer = lastVersionArray[i];
                String curVer = curVersionArray[i];

                int lastVerNum = Integer.parseInt(lastVer);
                int curVerNum = Integer.parseInt(curVer);

                // 当前版本 大于 最小支持版本，可以立马终止循环，
                // 当前版本 小于 最小支持版本，则提示更新
                if (curVerNum > lastVerNum) {
                    break;
                } else if (curVerNum < lastVerNum) {
                    return Constant.UpdateType.ORDINARY;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Constant.UpdateType.NONE;
        }

        return Constant.UpdateType.NONE;
    }
}
