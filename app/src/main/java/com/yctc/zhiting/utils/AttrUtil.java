package com.yctc.zhiting.utils;

public class AttrUtil {

    /**
     * 设备属性值转百分比
     * @param minVal
     * @param maxVal
     * @param val
     * @return
     */
    public static int getPercentVal(double minVal, double maxVal, double val){
        int result = 0;
        result = (int) Math.round((val-minVal)/(maxVal-minVal)*100);
        return result;
    }

    /**
     * 设备百分比值转属性值
     * @param minVal
     * @param maxVal
     * @param val
     * @return
     */
    public static double getActualVal(double minVal, double maxVal, int val){
        double result = 0;
        result =  ((double)val/100)*(maxVal-minVal)+minVal;
        return result;
    }

}
