package com.app.main.framework.baseutil;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateUtil {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_YEAR_MONTH_DAY_HOUR_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
    public static final String SERVCE_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_FORMAT1 = "MM-dd-yy HH:mm";
    public static final String ORDER_TIME_ORDER_LIST = "MMM dd,yyyy HH:mm";
    public static final String DATE_FORMAT_UI = "yyyy-MM-dd'T'HH:mm:ss.SSS+0000";

    private static final String DEFAULT_TIMEZONE_ID = "Etc/GMT0"; //默认时区
    private static final String CRESULT_SERVER_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"; //服务器时间格式
    public static final String COMMON_SERVER_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSS"; //服务器时间格式


    /**
     * 将毫秒值转换为指定格式的字符串
     *
     * @param : @param time	毫秒值
     * @param : @param fomat	格式
     * @return type:String	返回格式化后的字符串
     */
    public static String getTimeFormat(long time, String format) {
        String formatString = "";
        if (format != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            formatString = dateFormat.format(new Date(time));
        }
        return formatString;
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

    public static String getSysTime() {
        return getSysTime(DATE_FORMAT);
    }

    /**
     * @param list
     * @return 移除list中的相同元素
     */
    public static List removeDuplicate(List list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

    /**
     * @return 格式化时间，没时分秒
     * sDate需要传入的格式为  例：“2017-04-10”
     * dataFormat原来的格式例："yyyy年MM月dd日  HH:mm:ss"
     * resultFormat 转化后的格式
     */
    public static String formatAllDate(String sDate, String dataFormat, String resultFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);
        Date date = null;
        try {
            date = sdf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat(resultFormat);

        return dateFormat.format(date);
    }

    /**
     * 获取年
     *
     * @return
     */
    public static int getYear() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.YEAR);
    }

    /**
     * 获取月
     *
     * @return
     */
    public static int getMonth() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日
     *
     * @return
     */
    public static int getDay() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.DATE);
    }

    /**
     * 获取当前时间的时间戳 毫秒
     *
     * @return
     */
    public static long getCurrentTimeMillis2() {
        long currentMillis = System.currentTimeMillis();
        String currentTime = formatOrderTimePushServer(currentMillis);
        return getMillisecond(currentTime, COMMON_SERVER_TIME);
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取月日年
     * February 27, 2019
     *
     * @return
     */
    public static String getMonthDayYear() {
        String day = String.valueOf(getDay());
        String year = String.valueOf(getYear());
        String monthStr = getMonthEn(getMonth());
        if (day.length() == 1) {
            day = "0" + day;
        }
        return monthStr + " " + day + "," + year;
    }

    /**
     * 通过数字获取月份
     *
     * @param month
     * @return
     */
    public static String getMonthEn(int month) {
        String monthStr = "";
        if (1 == month) {
            monthStr = "Jan";
        } else if (2 == month) {
            monthStr = "Feb";
        } else if (3 == month) {
            monthStr = "Mar";
        } else if (4 == month) {
            monthStr = "Apr";
        } else if (5 == month) {
            monthStr = "May";
        } else if (6 == month) {
            monthStr = "Jun";
        } else if (7 == month) {
            monthStr = "Jul";
        } else if (8 == month) {
            monthStr = "Aug";
        } else if (9 == month) {
            monthStr = "Sep";
        } else if (10 == month) {
            monthStr = "Oct";
        } else if (11 == month) {
            monthStr = "Nov";
        } else if (12 == month) {
            monthStr = "Dec";
        }
        return monthStr;
    }

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSS 转 April 30, 2019 09:30
     *
     * @param sDate
     * @return
     */
    public static String getCQFormatDate(String sDate) {
        if (UiUtil.isAvailable(sDate)) {
            return getOrderListTimeFromServer(sDate);
        } else {
            return "";
        }
    }

    public static String getCQFormatDate(long time) {
        String timeStr = getTimeFormat(time, CRESULT_SERVER_TIME);
        return getOrderListTimeFromServer(timeStr);
    }

    /**
     * 显示的时间
     *
     * @param time
     * @return
     */
    public static String getOrderListTimeFromServer(String time) {
        return getOrderListTimeFromServer(time, CRESULT_SERVER_TIME);
    }

    public static String getOrderListTimeFromServer(String time, String dataFormat) {
//        if (TextUtils.isEmpty(time)) return "";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);//原来时间
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE_ID));
//        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(ORDER_TIME_ORDER_LIST);//显示时间
//        Date time1 = null;
//        try {
//            time1 = simpleDateFormat.parse(time);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (time1 == null)
//            return "";
//        long timeLong = time1.getTime();
//        simpleDateFormat2.setTimeZone(TimeZone.getDefault());
//        return simpleDateFormat2.format(timeLong);
        return getOrderListTimeFromServer(time,dataFormat,ORDER_TIME_ORDER_LIST);
    }
    public static String getOrderListTimeFromServer(String time, String dataFormat,String resultFormat) {
        if (TextUtils.isEmpty(time)) return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataFormat);//原来时间
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE_ID));
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(resultFormat);//显示时间
        Date time1 = null;
        try {
            time1 = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (time1 == null)
            return "";
        long timeLong = time1.getTime();
        simpleDateFormat2.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat2.format(timeLong);
    }
    /**
     * 上传的时间
     *
     * @param time
     * @return
     */
    public static String formatOrderTimePushServer(long time) {
        return formatOrderTimePushServer(time, COMMON_SERVER_TIME);
    }

    public static String formatOrderTimePushServer(long time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE_ID));
        return simpleDateFormat.format(time);
    }

    public static String formatOrderTimePushServer(String data) {
        if (TextUtils.isEmpty(data)) return "";
        return formatOrderTimePushServer(data, DATE_FORMAT_UI);
    }

    public static String formatOrderTimePushServer(String data, String format) {
        if (TextUtils.isEmpty(data)) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = date.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(COMMON_SERVER_TIME);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE_ID));
        return simpleDateFormat.format(time);
    }

    /**
     * 返回毫秒
     *
     * @param data   时间字符串
     * @param format 数据的格式
     * @return
     */
    public static long getMillisecond(String data, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static long getMillisecondDefault(String data) {
        return getMillisecond(data, ORDER_TIME_ORDER_LIST);
    }

    /**
     * 毫秒转日期格式
     *
     * @param currentTime
     * @param formatType
     * @return
     */
    public static String longToDate(long currentTime, String formatType) {
        if ((currentTime + "").length() <= 10) {
            currentTime = currentTime * 1000;
        }
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatType);
        long nowTime = currentTime;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(nowTime);

        try {
            date = dateFormat.parse(dateFormat.format(cal.getTime()));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateToString(date, formatType);
    }

    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }


    /**
     * 是否有没有超时间操作
     */
    public static boolean isPermission(String serverTime) {
        //控件置灰,一个小时内不能拒绝，取消
        long nowTime = DateUtil.getCurrentTimeMillis();
        String finalTimeStr = DateUtil.getOrderListTimeFromServer(serverTime);
        long appointTime = DateUtil.getMillisecond(finalTimeStr, DateUtil.ORDER_TIME_ORDER_LIST);

        return (nowTime + 60 * 60 * 1000) < appointTime;
    }
}
