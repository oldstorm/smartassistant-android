package com.yctc.zhiting.utils;

import android.text.TextUtils;

import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.gsonutils.GsonConverter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.MemberDetailBean;

/**
 * date : 2021/7/7 11:15
 * desc :
 */
public class UserUtils {

    /**
     * 保存用户信息
     *
     * @param user
     */
    public static void saveUser(MemberDetailBean user) {
        if (user == null) {
            SpUtil.put(Constant.CLOUD_USER, "");
            SpUtil.put(SpConstant.CLOUD_USER_ID, 0);
        } else {
            SpUtil.put(SpConstant.CLOUD_USER_ID, user.getUser_id());
            SpUtil.put(Constant.CLOUD_USER, GsonConverter.getGson().toJson(user));
        }
    }

    /**
     * 获取云用户id
     *
     * @return
     */
    public static int getCloudUserId() {
        MemberDetailBean user = getUser();
        if (user != null)
            return user.getUser_id();
        return 0;
    }

    /**
     * 用户昵称
     *
     * @return
     */
    public static String getCloudUserName() {
        MemberDetailBean user = getUser();
        if (user != null)
            return user.getNickname();
        return "";
    }

    /**
     * 更新名字
     *
     * @param name
     */
    public static void setCloudUserName(String name) {
        MemberDetailBean user = getUser();
        if (user != null) {
            user.setNickname(name);
            SpUtil.put(Constant.CLOUD_USER, GsonConverter.getGson().toJson(user));
        }
    }

    /**
     * 用户手机
     *
     * @return
     */
    public static String getPhone() {
        MemberDetailBean user = getUser();
        if (user != null)
            return user.getPhone();
        return "";
    }

    /**
     * 判断是否登陆状态
     *
     * @return
     */
    public static boolean isLogin() {
        return getCloudUserId() > 0;
    }

    private static MemberDetailBean getUser() {
        String userJson = SpUtil.get(Constant.CLOUD_USER);
        if (TextUtils.isEmpty(userJson)) {
            return null;
        } else {
            MemberDetailBean user = GsonConverter.getGson().fromJson(userJson, MemberDetailBean.class);
            return user;
        }
    }
}
