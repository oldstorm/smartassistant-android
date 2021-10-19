package com.yctc.zhiting.entity.mine;

public class UserInfoBean {

    private int userId;
    private String nickname;
    private String phone;
    private String iconUrl;

    public UserInfoBean() {
    }

    public UserInfoBean(int userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    public UserInfoBean(int userId, String nickname, String phone, String iconUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.phone = phone;
        this.iconUrl = iconUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
