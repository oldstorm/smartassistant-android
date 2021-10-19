package com.yctc.zhiting.utils;

import android.content.Context;

import com.yctc.zhiting.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    /**
     * 时分秒转秒
     * @param hour
     * @param minute
     * @param seconds
     * @return
     */
    public static int toSeconds(int hour, int minute, int seconds){
        int s = seconds;
        s = hour*3600 + minute*60+s;
        return s;
    }

    /**
     * 时分秒转时间戳
     * @param hour
     * @param minute
     * @param seconds
     * @return
     */
    public static int toMillis(int hour, int minute, int seconds){
        int millis = seconds*1000;
        millis = millis + hour*3600*1000 + minute*60*1000;
        return millis;
    }

    /**
     * 时分秒
     * @param hour
     * @param minute
     * @param seconds
     * @param context
     * @return
     */
    public static String toHMSString(int hour, int minute, int seconds, Context context){
        String time = "";
        if (hour>0){
            time = time + hour+context.getResources().getString(R.string.scene_hour);
        }
        if (minute>0){
            time = time + minute+context.getResources().getString(R.string.scene_minute);
        }
        if (seconds>0){
            time = time + seconds+context.getResources().getString(R.string.scene_seconds);
        }
        return time;
    }

    /**
     * 字符串转时间戳
     * @param dateStr
     * @return
     */
    public static long string2Stamp(String dateStr){
        long stamp = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(dateStr);
            stamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stamp;
    }

    /**
     * 今天日期
     * @return
     */
    public static String getToday(){
        String today = "";
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        today = sdf.format(d);
        return today;
    }

    /**
     * 今天日期的时分秒
     * @return
     */
    public static String getTodayHMS(long time){
        String today = "";
        Date d = new Date(time*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        today = sdf.format(d);
        return today;
    }

    /**
     * 1234567转星期
     * @param week
     * @return
     */
    public static String toWeek(String week, Context context){
        String result = "";
        switch (week){
            case "1":
                result = context.getResources().getString(R.string.scene_monday);
                break;

            case "2":
                result = context.getResources().getString(R.string.scene_tuesday);
                break;

            case "3":
                result = context.getResources().getString(R.string.scene_wednesday);
                break;

            case "4":
                result = context.getResources().getString(R.string.scene_thursday);
                break;

            case "5":
                result = context.getResources().getString(R.string.scene_friday);
                break;

            case "6":
                result = context.getResources().getString(R.string.scene_saturday);
                break;

            case "7":
                result = context.getResources().getString(R.string.scene_sunday);
                break;

        }

        return result;
    }



    /**
     * 日期的月日时分
     * @return
     */
    public static String getMDHM(long time){
        String today = "";
        Date d = new Date(time*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        today = sdf.format(d);
        return today;
    }

    /**
     * 秒转时分秒
     * @param seconds
     * @return
     */
    public static String seconds2String(int seconds){
        String result = "";
        int hour = seconds/3600;
        int minute =  (seconds % 3600) / 60;
        int sec = (seconds % 3600) % 60;
        result = (hour<10 ? "0"+hour : hour) + ":" + (minute<10 ? "0"+minute : minute)+ ":" + (sec<10 ? "0"+sec : sec);
        return result;
    }

}
