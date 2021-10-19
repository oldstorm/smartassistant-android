package com.yctc.zhiting.utils;

import android.content.Context;
import android.text.TextUtils;

import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class StringUtil {

    /**
     * 运算符转文字
     * @param operator
     * @return
     */
    public static String operator2String(String operator, Context context){
        String result = "";
        switch (operator){
            case "<":
                result = context.getResources().getString(R.string.scene_less);
                break;

            case "=":
                result = context.getResources().getString(R.string.scene_equal);
                break;

            case ">":
                result = context.getResources().getString(R.string.scene_greater);
                break;
        }
        return result;
    }

    /**
     * 开关状态转文字
     * @param attr
     * @param context
     * @return
     */
    public static String switchStatus2String(String attr, Context context){
        String result = "";
        if (attr.equals(Constant.ON)){
            result = context.getResources().getString(R.string.scene_turn_on);
        }else if (attr.equals(Constant.OFF)){
            result = context.getResources().getString(R.string.scene_turn_off);
        }else if (attr.equals(Constant.TOGGLE)){
            result = context.getResources().getString(R.string.scene_toggle);
        }
        return result;
    }

    /**
     * 属性转文字
     * @param attr
     * @param context
     * @return
     */
    public static String attr2String(String attr, Context context){
        String result = "";
        if (attr.equals(Constant.POWER)) {
            result = context.getResources().getString(R.string.scene_switch);
        }if (attr.equals(Constant.brightness)){
            result = context.getResources().getString(R.string.scene_brightness);
        }else if (attr.equals(Constant.color_temp)){
            result = context.getResources().getString(R.string.scene_color_temperature);
        }
        return result;
    }

    /**
     * 判断是否json格式
     * @param str
     * @return
     */
    public static boolean getJSONType(String str) {
        boolean result = false;
        if (!TextUtils.isEmpty(str)) {
            str = str.trim();
            if (str.startsWith("{") && str.endsWith("}")) {
                result = true;
            } else if (str.startsWith("[") && str.endsWith("]")) {
                result = true;
            }
        }
        return result;
    }


    /**
     * 场景日志执行状态
     * @param context
     * @param type
     * @return
     */
    public static String getLogStatus(Context context, int type){
        String result = "";
        switch (type){
            case 1:
                result = context.getResources().getString(R.string.scene_log_success);
                break;

            case 2:
                result = context.getResources().getString(R.string.scene_log_some_success);
                break;

            case 3:
                result = context.getResources().getString(R.string.scene_log_some_fail);
                break;

            case 4:
                result = context.getResources().getString(R.string.scene_log_time_out);
                break;

            case 5:
                result = context.getResources().getString(R.string.scene_log_device_removed);
                break;

            case 6:
                result = context.getResources().getString(R.string.scene_device_offline);
                break;

            case 7:
                result = context.getResources().getString(R.string.scene_log_scen_removed);
                break;
        }
        return result;
    }

    /**
     * 时分秒转  HH:mm:ss字符串
     * @param h
     * @param m
     * @param s
     * @return
     */
    public static String hms2String(int h, int m, int s){
        String result = "";
        String hour = h < 10 ? "0"+h : h+"";
        String minute = m < 10 ? "0"+m : m+"";
        String seconds = s < 10 ? "0"+s : s+"";
        result = hour+":"+minute+":"+seconds;
        return result;
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str){
        try {
            byte[] bytes = str.getBytes("UTF-8");
            if (bytes.length == str.length()){
                return false;
            }else {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * uuid
     * @return
     */
    public static String getUUid(){
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr=str.replace("-", "");
        return uuidStr;
    }
}
