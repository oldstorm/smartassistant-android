package com.yctc.zhiting.entity.mine;

import java.util.List;

/**
 * 成员信息
 */
public class UserBean {


    /**
     * user_id : 4
     * role_infos : [{"id":1,"name":"管理员"},{"id":2,"name":"成员"}]
     * account_name :
     * nickname : User_F63C6F
     * token :
     * phone :
     * is_set_password : false
     */

    private boolean selected;

    private int user_id;
    private String account_name;
    private String nickname;
    private String token;
    private String phone;
    private boolean is_set_password;
    private List<RoleInfosBean> role_infos;
    private boolean is_manager;
    private String avatar_url; // 头像

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public boolean isIs_set_password() {
        return is_set_password;
    }

    public void setIs_set_password(boolean is_set_password) {
        this.is_set_password = is_set_password;
    }

    public List<RoleInfosBean> getRole_infos() {
        return role_infos;
    }

    public void setRole_infos(List<RoleInfosBean> role_infos) {
        this.role_infos = role_infos;
    }

    public boolean isIs_manager() {
        return is_manager;
    }

    public void setIs_manager(boolean is_manager) {
        this.is_manager = is_manager;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public static class RoleInfosBean {
        /**
         * id : 1
         * name : 管理员
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
