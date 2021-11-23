package com.yctc.zhiting.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.app.main.framework.baseutil.UiUtil;
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

    public static String getSubString(String str, int start, int end){
        return str.substring(start, end);
    }

    public static boolean isNotEmpty(String str){
        return str!=null && str.length()>0;
    }


    /**
     * 登录界面的用户协议和隐私政策文案样式
     * @param content
     * @param color
     * @param agreementPolicyListener
     * @return
     */
    public static SpannableStringBuilder setAgreementAndPolicyTextStyle(String content, @ColorInt int color, AgreementPolicyListener agreementPolicyListener){
        SpannableStringBuilder spannableString = new SpannableStringBuilder();
        spannableString.append(content);

         ClickableSpan headClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ((TextView) widget).setHighlightColor(UiUtil.getColor(R.color.white));
               if (agreementPolicyListener!=null){
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
               if (agreementPolicyListener!=null){
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
                if (agreementPolicyListener!=null){
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
        int agreementBeginIndex = agreementEndIndex-4;
        int policyBeginIndex = agreementEndIndex+1;
        int policyEndIndex = content.length();


        spannableString.setSpan(headClickableSpan, 0, 10, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        spannableString.setSpan(agreementClickableSpan, agreementBeginIndex, agreementEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        spannableString.setSpan(agreementColorSpan, agreementBeginIndex, agreementEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        spannableString.setSpan(policyClickableSpan, policyBeginIndex, policyEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//        spannableString.setSpan(policyColorSpan, policyBeginIndex, policyEndIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableString;
    }
}
