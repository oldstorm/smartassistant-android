package com.app.main.framework.baseutil;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class TimeFormatUtil {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_YMD = "HH:mm"; //消息时间格式 一年内
    private static final String SERVCE_DATE_FORMAT = "MMM dd,yyyy HH:mm"; //消息时间格式一年之外
    private static final String MESSAGE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSS+0000";
    private static final String COMMENT_TIME = "HH:mm";

    @SuppressLint("SimpleDateFormat")
    public static String formatTime(long time){
        return new SimpleDateFormat(COMMENT_TIME).format(new Date(time));
    }
    @SuppressLint("SimpleDateFormat")
    public static long getTimeCurrent(String time) {
        try {
            return new SimpleDateFormat(COMMENT_TIME).parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取星期
     * @param date
     * @return
     */
    public static int getWeekOfDate(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
    }

    /**
     * 计算时间差
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateDurationToThisDay(long time){
        if (time == -1)
            return "";
        long thisTime = System.currentTimeMillis();
        long duration = thisTime - time;
//        int miuntes = (int) (duration/(1000*60));
        int hours = (int) (duration/(1000*60*60));
//        int years = (int) (duration/(1000*60*60*24*365));
        /*if (miuntes < 5)
            return UiUtil.getString(R.string.default_common_now);
        else if (hours < 1)
            return String.format(UiUtil.getString(R.string.default_common_minutes),miuntes);
        else */if (hours < 24){
//            return String.format(UiUtil.getString(R.string.default_common_hours),hours);
        /*else if (years <= 0) {*/
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_YMD);
            return simpleDateFormat.format(new Date(time));
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SERVCE_DATE_FORMAT);
            return simpleDateFormat.format(new Date(time));
        }
    }

    /**
     * 将毫秒值转换为指定格式的字符串
     *
     * @param : @param time	毫秒值
     * @param : @param fomat	格式
     * @return type:String	返回格式化后的字符串
     */
    public static String getFormatString(long time, String fomat) {
        String formatString = "";
        if (fomat != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(fomat);
            formatString = dateFormat.format(new Date(time));
        }
        return formatString;
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatMessageTime(long time){
        return new SimpleDateFormat(MESSAGE_TIME).format(time);
    }

    /**
     * 将字符串转换为毫秒值
     *
     * @param : @param date	日期字符串
     * @param : @param format	日期字符串的格式
     * @return type:long	日期字符串对应的毫秒值
     */
    public static long getFormatTime(String date, String format) {
        long time = 0;
        if (date != null && format != null) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            try {
                Date parse = dateFormat.parse(date);
                time = parse.getTime();
            } catch (ParseException ignored) {
            }
        }
        return time;
    }

    public static int timeDifference(String oldDate){
        return timeDifference(oldDate,getSysTime());
    }

    /**
     * 计算时间差
     * @param oldDate 记录时间
     * @param newDate 当前时间
     * @return
     */
    public static int timeDifference(String oldDate, String newDate) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(oldDate);
            Date d2 = df.parse(newDate);
            long diff = d2.getTime() - d1.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            System.out.println("" + days + "天" + hours + "小时" + minutes + "分");
            if (hours > 24 || (minutes > 60 && hours > 23))
                days+=1;
            return (int) days;
        } catch (Exception e) {
            return 0;
        }
    }

    /***
     * 得到系统时间
     * @return
     */
    public static String getSysTime(String format) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String sysNewTime = sdf.format(calendar.getTime());
        return sysNewTime;
    }

    public static String stringForTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static String getSysTime() {
        return getSysTime(DATE_FORMAT);
    }
}
