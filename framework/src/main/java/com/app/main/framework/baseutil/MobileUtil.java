package com.app.main.framework.baseutil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileUtil {
    public static Map<String, String> rexMap = new HashMap<>();

    static {
        rexMap.put("ar-DZ", "^(+?213|0)(5|6|7)\\d{8}$"); //阿拉伯语
        rexMap.put("ar-SY", "^(!?(\\+?963)|0)?9\\d{8}$"); //阿拉伯语(叙利亚)
        rexMap.put("en-US", "^(\\+?1)?[2-9]\\d{2}[2-9](?!11)\\d{6}$"); //英语(美国)
        rexMap.put("cs-CZ", "^(\\+?420)? ?[1-9][0-9]{2} ?[0-9]{3} ?[0-9]{3}$"); //捷克语(捷克共和国)
        rexMap.put("de-DE", "^(\\+?49[ \\.\\-])?([\\(]{1}[0-9]{1,6}[\\)])?([0-9 \\.\\-\\/]{3,20})((x|ext|extension)[ ]?[0-9]{1,4})?$"); //德语(德国)
        rexMap.put("da-DK", "^(\\+?45)?(\\d{8})$"); //丹麦语(丹麦)
        rexMap.put("el-GR", "^(\\+?30)?(69\\d{8})$"); //希腊语(希腊)
        rexMap.put("en-AU", "^(\\+?61|0)?4?2?\\d{8}$"); //英语(澳大利亚)
//        rexMap.put("en-AU", "^(\\+?61|0)?4?2?\\d{0,8}$"); //英语(澳大利亚)
        rexMap.put("en-GB", "^(\\+?44|0)7\\d{9}$"); //英语(英国)
        rexMap.put("en-HK", "^(\\+?852\\-?)?[569]\\d{3}\\-?\\d{4}$"); //英语(香港)
        rexMap.put("en-IN", "^(\\+?91|0)?[789]\\d{9}$"); //英语(印度)
        rexMap.put("en-NZ", "^(\\+?64|0)2\\d{7,9}$"); //英语(新西兰)
        rexMap.put("en-ZA", "^(\\+?27|0)\\d{9}$"); //英语(南非)
        rexMap.put("es-ES", "^(\\+?34)?(6\\d{1}|7[1234])\\d{7}$"); //西班牙语(西班牙)
        rexMap.put("fi-FI", "^(\\+?358|0)\\s?(4(0|1|2|4|5)?|50)\\s?(\\d\\s?){4,8}\\d$"); //芬兰语(芬兰)
        rexMap.put("fr-FR", "^(\\+?33|0)[67]\\d{8}$"); //法语(法国)
        rexMap.put("he-IL", "^(\\+972|0)([23489]|5[0248]|77)[1-9]\\d{6}"); //希伯来语(以色列)
        rexMap.put("hu-HU", "^(\\+?36)(20|30|70)\\d{7}$"); //匈牙利语(匈牙利)
        rexMap.put("it-IT", "^(\\+?39)?\\s?3\\d{2} ?\\d{6,7}$"); //意大利语(意大利)
        rexMap.put("ja-JP", "^(\\+?81|0)\\d{1,4}[ \\-]?\\d{1,4}[ \\-]?\\d{4}$"); //日语(日本)
        rexMap.put("nl-BE", "^(\\+?32|0)4?\\d{8}$"); //荷兰语(比利时)
        rexMap.put("pl-PL", "^(\\+?48)? ?[5-8]\\d ?\\d{3} ?\\d{2} ?\\d{2}$"); //波兰语(波兰)
        rexMap.put("pt-BR", "^(\\+?55|0)\\-?[1-9]{2}\\-?[2-9]{1}\\d{3,4}\\-?\\d{4}$"); //葡萄牙语(巴西)
        rexMap.put("pt-PT", "^(\\+?351)?9[1236]\\d{7}$"); //葡萄牙语(葡萄牙)
        rexMap.put("ru-RU", "^(\\+?7|8)?9\\d{9}$"); //俄语(俄罗斯)
        rexMap.put("tr-TR", "^(\\+?90|0)?5\\d{9}$"); //土耳其语(土耳其)
        rexMap.put("zh-CN", "^(\\+?0?86\\-?)?1[345789]\\d{9}$"); //简体中文
        rexMap.put("zh-TW", "^(\\+?886\\-?|0)?9\\d{8}$"); //中国台湾
    }

    public static boolean matchMobile(String nationality,String mobile){
        String regular = rexMap.get(nationality);
//        Logs.i(regular);
//        Logs.i(nationality);
//        Logs.i(mobile);
        System.out.println("nationality == " + nationality + ",mobile == " + mobile + ",regular = " + regular);
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(regular);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(mobile);
        return m.find();
    }

    public static boolean matchMobile(String mobile){
        return matchMobile(AndroidUtil.getLocale(),mobile);
    }
}
