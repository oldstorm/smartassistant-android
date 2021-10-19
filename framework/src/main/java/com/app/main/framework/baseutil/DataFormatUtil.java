package com.app.main.framework.baseutil;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import com.app.main.framework.R;

import java.text.DecimalFormat;

public class DataFormatUtil {
    private static DecimalFormat format = new DecimalFormat("###0.##");
    private static DecimalFormat formatTwo = new DecimalFormat("##0.00");
    private static DecimalFormat formatOnlyDot = new DecimalFormat("###0.#");
    private static DecimalFormat formatOnlyDotOrNot = new DecimalFormat("###0.0");

    /**
     * 格式化价格显示
     *
     * @param price 价格
     * @param time  价格单位时间
     * @return $price/time Min
     */
    public static String formatPrice(double price, int time) {
        return String.format(UiUtil.getString(R.string.price_for_minute), format.format(price), time);
    }

    /**
     * 格式化星级
     *
     * @param star
     * @return
     */
    public static String formatStar(double star) {
        return formatOnlyDotOrNot.format(star);
    }

    public static String formatVoiceTime(int time) {
        if (time < 60)
            return /*String.format(UiUtil.getString(R.string.voice_lenght_second_unit),time)*/time + "''";
        else {
            int minute = time / 60;
            int second = time - minute * 60;
            return minute + "'" + second + "''";
        }
    }

    public static String formatUserName(String userName) {
        if (userName.length() >= 4)
            return String.format(UiUtil.getString(R.string.username_unit), userName.substring(0, 2), userName.substring(userName.length() - 2));
        else
            return String.format(UiUtil.getString(R.string.username_unit), "", userName);
    }

    /**
     * 格式化用户等级
     *
     * @param level
     * @return
     */
    public static String formatUserLevel(int level) {
        return String.format(UiUtil.getString(R.string.level_unit), level);
    }

    /**
     * 格式化时间
     *
     * @param time 时间
     * @return time Min
     */
    public static Spanned formatTimeWithSlash(double price, int time) {
        return Html.fromHtml(String.format(UiUtil.getString(R.string.time_unit_with_slash), price, time));
    }

    /**
     * 格式化时间
     *
     * @param time 时间
     * @return time Min
     */
    public static String formatTime(int time) {
        return String.format(UiUtil.getString(R.string.time_unit), time);
    }

    /**
     * 格式化价格
     *
     * @param price 价格
     * @return $price
     */
    public static String formatPrice(double price) {
        return String.format(UiUtil.getString(R.string.price_unit), formatTwo.format(price));
    }

    /**
     * 显示价格
     *
     * @param price
     * @return
     */
    public static String formatPrice(String price) {
        if (TextUtils.isEmpty(price)) return "$0.00";
        double tempPrice = Double.valueOf(price);
        return String.format(UiUtil.getString(R.string.price_unit), formatTwo.format(tempPrice));
    }

    /**
     * 格式化折扣率百分比
     *
     * @param rate 百分比
     * @return rate%
     */
    public static String formatRate(int rate) {
        return String.format(UiUtil.getString(R.string.rate_unit), rate);
    }

    /**
     * 格式化距离单位
     *
     * @param distance 距离
     * @return distance m/km
     */
    public static String formatDistance(double distance) {
        if (distance < 500)
            return UiUtil.getString(R.string.distance_less_than_unit);
        else if (distance >= 500 && distance < 1000)
            return String.format(UiUtil.getString(R.string.distance_unit), ((int) Math.round(distance / 100)) * 100);
        else
            return String.format(UiUtil.getString(R.string.distance_km_unit), formatOnlyDot.format(distance / 1000));
    }

    /**
     * 格式化数字
     *
     * @param total 数量
     * @return 999+/total
     */
    public static String formatTotal(int total) {
        if (total > 999)
            return String.format(UiUtil.getString(R.string.total_unit), 999);
        else
            return String.valueOf(total);
    }

    /**
     * 格式化消息数字
     *
     * @param total 数量
     * @return 999+/total
     */
    public static String formatMessageTotal(int total) {
        if (total > 99)
            return String.format(UiUtil.getString(R.string.total_unit), 99);
        else
            return String.valueOf(total);
    }

    /**
     * 格式化身高
     *
     * @param height 身高
     * @return height cm
     */
    public static String formatHeight(int height) {
        return String.format(UiUtil.getString(R.string.height_unit), height);
    }

    /**
     * 格式化体重
     *
     * @param weight 体重
     * @return weight kg
     */
    public static String formatWeight(int weight) {
        return String.format(UiUtil.getString(R.string.weight_unit), weight);
    }

    /**
     * 格式化银行卡号
     */
    public static String formatCardNumber(String cardNumber) {
        return String.format(UiUtil.getString(R.string.card_format), cardNumber);
    }
}
