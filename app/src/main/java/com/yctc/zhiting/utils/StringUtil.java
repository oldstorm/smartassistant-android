package com.yctc.zhiting.utils;

import static com.yctc.zhiting.config.Constant.wifiInfo;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.widget.CustomImageSpan;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StringUtil {

    /**
     * 运算符转文字
     *
     * @param operator
     * @return
     */
    public static String operator2String(String operator, Context context) {
        String result = "";
        switch (operator) {
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
     *
     * @param attr
     * @param context
     * @return
     */
    public static String switchStatus2String(String attr, Context context) {
        String result = "";
        if (attr.equals(Constant.ON)) {
            result = context.getResources().getString(R.string.scene_turn_on);
        } else if (attr.equals(Constant.OFF)) {
            result = context.getResources().getString(R.string.scene_turn_off);
        } else if (attr.equals(Constant.TOGGLE)) {
            result = context.getResources().getString(R.string.scene_toggle);
        }
        return result;
    }

    /**
     * 属性转文字
     *
     * @param attr
     * @param context
     * @return
     */
    public static String attr2String(String attr, Context context) {
        String result = "";
        if (attr.equals(Constant.ON_OFF)) {
            result = context.getResources().getString(R.string.scene_switch);
        }else if (attr.equals(Constant.brightness)) {
            result = context.getResources().getString(R.string.scene_brightness);
        } else if (attr.equals(Constant.color_temp)) {
            result = context.getResources().getString(R.string.scene_color_temperature);
        } else if (attr.equals(Constant.rgb)) {
            result = context.getResources().getString(R.string.home_color);
        } else if (attr.equals(Constant.leak_detected) || attr.equals(Constant.window_door_close)
                || attr.equals(Constant.detected)) {
            result = context.getResources().getString(R.string.scene_status);
        } else if (attr.equals(Constant.temperature)) {
            result = context.getResources().getString(R.string.scene_temperature);
        } else if (attr.equals(Constant.humidity)) {
            result = context.getResources().getString(R.string.scene_humidity);
        } else if (attr.equals(Constant.powers_1)) {
            result = context.getResources().getString(R.string.scene_powers_1);
        } else if (attr.equals(Constant.powers_2)) {
            result = context.getResources().getString(R.string.scene_powers_2);
        } else if (attr.equals(Constant.powers_3)) {
            result = context.getResources().getString(R.string.scene_powers_3);
        } else if (attr.equals(Constant.target_state)) {
            result = context.getResources().getString(R.string.scene_guard);
        } else if (attr.equals(Constant.target_position)) {
            result = context.getResources().getString(R.string.scene_curtain_status);
        } else  if (attr.equals(Constant.switch_event)) {
            result = context.getResources().getString(R.string.scene_switch);
        }
        return result;
    }

    /**
     * 判断是否json格式
     *
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
     * 网关守护转台
     * @param context
     * @param type
     * @return
     */
    public static String targetStatStr(Context context, int type){
        String result = "";
        switch (type){
            case 0:
                result = context.getResources().getString(R.string.scene_open_at_home);
                break;

            case 1:
                result = context.getResources().getString(R.string.scene_open_out_home);
                break;


            case 2:
                result = context.getResources().getString(R.string.scene_open_sleep);
                break;

            case 3:
                result = context.getResources().getString(R.string.scene_close_guard);
                break;
        }
        return result;
    }

    /**
     * 窗帘状态
     * @param context
     * @param min
     * @param max
     * @param val
     * @param operator
     * @return
     */
    public static String targetPositionStr(Context context, int min ,int max, int val, String operator) {
        String result = "";
        if (val == min && (TextUtils.isEmpty(operator) || operator.equals(context.getResources().getString(R.string.scene_equal)))) {
            result = context.getResources().getString(R.string.scene_curtain_close);
        } else if (val == max && (TextUtils.isEmpty(operator) || operator.equals(context.getResources().getString(R.string.scene_equal)))) {
            result = context.getResources().getString(R.string.scene_curtain_open);
        } else {
            result = context.getResources().getString(R.string.scene_curtain_status) + operator + (int)(AttrUtil.getActualVal(min, max, val)) + "%";
        }
        return result;
    }

    /**
     * 无线开关（一二三击）
     * @param context
     * @param val
     * @return
     */
    public static String switchEventStr(Context context, int val) {
        String result = "";
        switch (val) {
            case 0:
                result = context.getResources().getString(R.string.scene_single_click);
                break;

            case 1:
                result = context.getResources().getString(R.string.scene_double_click);
                break;

            case 2:
                result = context.getResources().getString(R.string.scene_triple_click);
                break;
        }
        return result;
    }


    /**
     * 场景日志执行状态
     *
     * @param context
     * @param type
     * @return
     */
    public static String getLogStatus(Context context, int type) {
        String result = "";
        switch (type) {
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
     *
     * @param h
     * @param m
     * @param s
     * @return
     */
    public static String hms2String(int h, int m, int s) {
        String result = "";
        String hour = h < 10 ? "0" + h : h + "";
        String minute = m < 10 ? "0" + m : m + "";
        String seconds = s < 10 ? "0" + s : s + "";
        result = hour + ":" + minute + ":" + seconds;
        return result;
    }

    /**
     * 温度值
     * @param val
     * @return
     */
    public static String getTemperatureString (Object val) {
        DecimalFormat df = new DecimalFormat(".0");
        String str = df.format(val) ;
        if (str.startsWith(".")) {
            str = str.replace(".", "");
        }
        if (str.endsWith(".0")) {
            str = str.replace(".0", "");
        }
        return str+ "℃";
    }

    /**
     * 湿度值
     * @param val
     * @return
     */
    public static String getHumidityString(Object val) {
        DecimalFormat df = new DecimalFormat(".0");
        String str = df.format(val);
        if (str.startsWith(".")) {
            str = str.replace(".", "");
        }
        if (str.endsWith(".0")) {
            str = str.replace(".0", "");
        }
        return str+"%";
    }

    /**
     * 门窗状态转字符串
     *
     * @param context
     * @param type
     * @return
     */
    public static String doorWindowStatus2String(Context context, int type) {
        String result = "";
        result = type == 1 ? context.getResources().getString(R.string.scene_close_to_open) : context.getResources().getString(R.string.scene_open_to_close);
        return result;
    }


    public static String getFeedbackType(Context context, int type) {
        List<String> typeData = (List<String>) Arrays.asList(UiUtil.getStringArray(R.array.feedback_type));
        return typeData.get(type-1);
    }

    /**
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            if (bytes.length == str.length()) {
                return false;
            } else {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * uuid
     *
     * @return
     */
    public static String getUUid() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr = str.replace("-", "");
        return uuidStr;
    }

    public static String getSubString(String str, int start, int end) {
        return str.substring(start, end);
    }

    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }


    /**
     * 登录界面的用户协议和隐私政策文案样式
     *
     * @param content
     * @param color
     * @param agreementPolicyListener
     * @return
     */
    public static SpannableStringBuilder setAgreementAndPolicyTextStyle(String content, @ColorInt int color, AgreementPolicyListener agreementPolicyListener) {
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append(content);

        ClickableSpan headClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ((TextView) widget).setHighlightColor(UiUtil.getColor(R.color.white));
                if (agreementPolicyListener != null) {
                    agreementPolicyListener.onHead();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(UiUtil.getColor(R.color.color_94A5BE));
            }
        };


        ClickableSpan agreementClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ((TextView) widget).setHighlightColor(UiUtil.getColor(R.color.white));
                if (agreementPolicyListener != null) {
                    agreementPolicyListener.onAgreement();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(color);
            }
        };

        ForegroundColorSpan policyColorSpan = new ForegroundColorSpan(color);
        ClickableSpan policyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ((TextView) widget).setHighlightColor(UiUtil.getColor(R.color.white));
                if (agreementPolicyListener != null) {
                    agreementPolicyListener.onPolicy();
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setColor(color);
            }
        };

        int agreementEndIndex = content.indexOf("、");
        int agreementBeginIndex = agreementEndIndex - 4;
        int policyBeginIndex = agreementEndIndex + 1;
        int policyEndIndex = content.length();


        spannableString.setSpan(headClickableSpan, 0, 10, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        spannableString.setSpan(agreementClickableSpan, agreementBeginIndex, agreementEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        spannableString.setSpan(agreementColorSpan, agreementBeginIndex, agreementEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        spannableString.setSpan(policyClickableSpan, policyBeginIndex, policyEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        spannableString.setSpan(policyColorSpan, policyBeginIndex, policyEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /**
     * 场景那块颜色显示
     * @param content
     * @param color
     * @return
     */
    public static SpannableStringBuilder changeTextColor(String content, int color){
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append(content);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(color);
        int textBegin = content.indexOf("■");
        int textEnd = textBegin+1;
        spannableString.setSpan(foregroundColorSpan, textBegin, textEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    /**
     * 部门详情 公司名称和部门名称之间插入图片
     * @param context
     * @param company
     * @param department
     * @param img
     * @return
     */
    public static SpannableStringBuilder setImgInText(Context context, String company, String department, @DrawableRes int img){
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append(company);
        spannableString.append(department);
        CustomImageSpan span = new CustomImageSpan(UiUtil.getDrawable(img), 10, 10);
        spannableString.setSpan(span, company.length()-1, company.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * wifi的bssid
     * @return
     */
    public static String getBssid() {
        String bssid = "";
        if (wifiInfo!=null) {
            String wifiBssid = wifiInfo.getBSSID();
            if (wifiBssid!=null && !wifiBssid.equals(Constant.ERROR_BSSID)) {
                bssid = wifiBssid;
            }
        }
        return bssid;
    }
}
