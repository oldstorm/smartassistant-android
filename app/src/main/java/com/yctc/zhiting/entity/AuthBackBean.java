package com.yctc.zhiting.entity;

import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;

import java.util.List;

import okhttp3.Cookie;

/**
 * 授权信息类
 */
public class AuthBackBean extends Request {

    private int userId;  // 用户id
    private String userName; // 用户昵称
    private HomeCompanyBean homeCompanyBean; // 家庭信息
    private ScopeTokenBean.STBean stBean; // 授权token和过期时间
    private List<Cookie> cookies; // 登录SC时的cookie

    public AuthBackBean(int userId, String userName, HomeCompanyBean homeCompanyBean, ScopeTokenBean.STBean stBean) {
        this.userId = userId;
        this.userName = userName;
        this.homeCompanyBean = homeCompanyBean;
        this.stBean = stBean;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public HomeCompanyBean getHomeCompanyBean() {
        return homeCompanyBean;
    }

    public void setHomeCompanyBean(HomeCompanyBean homeCompanyBean) {
        this.homeCompanyBean = homeCompanyBean;
    }

    public ScopeTokenBean.STBean getStBean() {
        return stBean;
    }

    public void setStBean(ScopeTokenBean.STBean stBean) {
        this.stBean = stBean;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }
}
