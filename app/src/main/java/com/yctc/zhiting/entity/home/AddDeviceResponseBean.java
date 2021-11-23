package com.yctc.zhiting.entity.home;

import com.yctc.zhiting.entity.mine.IdBean;

public class AddDeviceResponseBean {
    private int device_id;
    private String plugin_url;
    private UserInfo user_info;
    private IdBean area_info;

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public String getPlugin_url() {
        return plugin_url;
    }

    public void setPlugin_url(String plugin_url) {
        this.plugin_url = plugin_url;
    }

    public UserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfo user_info) {
        this.user_info = user_info;
    }

    public IdBean getArea_info() {
        return area_info;
    }

    public void setArea_info(IdBean area_info) {
        this.area_info = area_info;
    }

    public class UserInfo {
        private int user_id;
        private String account_name;
        private String nick_name;
        private String token;
        private String phone;
        private String is_set_password;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getAccount_name() {
            return account_name;
        }

        public void setAccount_name(String account_name) {
            this.account_name = account_name;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getIs_set_password() {
            return is_set_password;
        }

        public void setIs_set_password(String is_set_password) {
            this.is_set_password = is_set_password;
        }
    }
}
