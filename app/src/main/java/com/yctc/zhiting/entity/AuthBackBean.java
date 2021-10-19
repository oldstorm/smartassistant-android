package com.yctc.zhiting.entity;

import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;

public class AuthBackBean extends Request {

    private int userId;
    private String userName;
    private HomeCompanyBean homeCompanyBean;
    private ScopeTokenBean.STBean stBean;

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
}
