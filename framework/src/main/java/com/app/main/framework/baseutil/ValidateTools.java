package com.app.main.framework.baseutil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具类
 */
public abstract class ValidateTools {

	 // 正则,匹配URL
	private static final String REGEX_URL = "(http|https)://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";

	 // 正则,匹配全汉字
	private static final String REGEX_CHINESE_ONLY = "[\\u4e00-\\u9fa5]+";

	 // 正则,匹配a-z(英文小写)
	private static final String REGEX_LOWERCASE_ONLY = "[a-z]+";

	 // 正则,匹配A-Z(英文大写)
	private static final String REGEX_UPPERCASE_ONLY = "[A-Z]+";

	 // 正则,匹配a-z A-Z(英文大写或小写)
	private static final String REGEX_LowerOrUpperCase = "[A-Za-z]+";

	 // 正则,匹配0-9(数字)
	private static final String REGEX_NUM_ONLY = "[0-9]+";

	 // 正则,匹配0和非0开头的数字
	private static final String REGEX_ZERO_OR_NOTSTARTWITHZERO = "(0|[1-9][0-9]*)";

	 // 正则,只能由a-z A-Z 0-9 组成
	private static final String REGEX_AZ_az_Num_DOWNLINE = "[a-zA-Z0-9]+";

	 // 正则,匹配必须是英文字母开头,只能由a-z A-Z 0-9 和 下划线"_"组成
	private static final String REGEX_AZ_az_Num_DOWNLINE_STARTWITHENGLISH = "[a-zA-Z][a-zA-Z0-9_]+";

	 // 正则,验证是否含有特殊的字符(^%&',;=?$\")
	private static final String REGEX_HAVE_SOECIAL_CHAR = "[^%&',;=?$\\x22]+";

	 // 正则,匹配邮箱
	private static final String REGEX_EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

	 // 正则,匹配手机号
	private static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

	//匹配密码6-20位，可字母数字符号混合
	private static final String REGEX_PASSWORD = "^[a-z_A-Z0-9-.!@#$%\\\\^&*)(+={}\\[\\]/\",'<>~·`?:;|]{6,20}+$";

	//匹配用户名字母数字混合
	private static final String REGEX_USERNAME = "^[a-zA-Z0-9 ]{2,50}$";

	private static boolean matcher(String string, String regex) {
		if (string == null) {
			return false;
		}
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string);
		return m.matches();
	}
	
	//==============================================================
	
	/**
	 * 是否为Url 是:返回true 否:返回false
	 */
	public static boolean isUrl(String string) {
		return matcher(string, REGEX_URL);
	}

	/**
	 * 验证是否只为汉字 是:返回true 否:返回false
	 */
	public static boolean isChineseOnly(String string) {
		return matcher(string, REGEX_CHINESE_ONLY);
	}

	/**
	 * 验证是否只为英文小写(a-z) 是:返回true 否:返回false
	 */
	public static boolean isLowerCaseOnly(String string) {
		return matcher(string, REGEX_LOWERCASE_ONLY);
	}

	/**
	 * 验证是否只为英文大写(A-Z) 是:返回true 否:返回false
	 */
	public static boolean isUpperCaseOnly(String string) {
		return matcher(string, REGEX_UPPERCASE_ONLY);
	}

	/**
	 * 验证是否只为英文(A-Z a-z) 是:返回true 否:返回false
	 */
	public static boolean isLowerOrUpperCase(String string) {
		return matcher(string, REGEX_LowerOrUpperCase);
	}

	/**
	 * 验证是否为Email 是:返回true 否:返回false
	 */
	public static boolean isEmail(String string) {
		return matcher(string, REGEX_EMAIL);
	}

	/**
	 * 验证是否只为数字(0~9) 是:返回true 否:返回false
	 */
	public static boolean isNumOnly(String string) {
		return matcher(string, REGEX_NUM_ONLY);
	}

	/**
	 * 验证是否为0或者不为0开头的数字 是:返回true 否:返回false
	 */
	public static boolean isZeroOrNotStartWithZeroNum(String string) {
		return matcher(string, REGEX_ZERO_OR_NOTSTARTWITHZERO);
	}

	/**
	 * 验证只能由A-Z a-z 0-9组成
	 */
	public static boolean isAzazNum(String string) {
		return matcher(string, REGEX_AZ_az_Num_DOWNLINE);
	}

	/**
	 * 验证是否为由A-Z a-z 0-9 和 下划线"_"组成并且是字母开头的字符串 是:返回true 否:返回false
	 */
	public static boolean isAzazNumDownLineAndStartWithEnglish(String string) {
		return matcher(string, REGEX_AZ_az_Num_DOWNLINE_STARTWITHENGLISH);
	}

	/**
	 * 验证是否含有特殊的字符(^%&',;=?$\") 是:返回true 否:返回false
	 */
	public static boolean isHasSoecialChar(String string) {
		return matcher(string, REGEX_HAVE_SOECIAL_CHAR);
	}

	/**
	 * 验证是否匹配手机号 是:返回true 否:返回false
	 */
	public static boolean isMobile(String string) {
		return matcher(string, REGEX_MOBILE);
	}

	/**
	 * 判断数组是否为空
	 */
	public static boolean isNull(List<?> list){
		return list == null || list.size() == 0;
	}

	/**
	 * 判断密码格式是否正确
	 */
	public static boolean isPass(String passwoed){
		return matcher(passwoed,REGEX_PASSWORD);
	}

	/**
	 * 判断用户名格式
	 */
	public static boolean isUserName(String userName){
		return matcher(userName,REGEX_USERNAME);
	}
}
