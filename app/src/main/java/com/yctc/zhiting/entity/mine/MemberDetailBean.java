package com.yctc.zhiting.entity.mine;

import java.util.List;

public class MemberDetailBean {


    /**
     * user_id : 1
     * role_infos : [{"id":1,"name":"管理员"}]
     * account_name :
     * nickname : zhou hang wu
     * token :
     * phone :
     * is_set_password : false
     * is_creator : true
     * is_self : true
     */

    private int user_id;
    private String account_name;
    private String nickname;
    private String token;
    private String phone;
    private boolean is_set_password;
    private boolean is_creator;
    private boolean is_self;
    private boolean is_owner; // 是否是拥有者
    private List<RoleInfosBean> role_infos;

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

    public boolean isIs_creator() {
        return is_creator;
    }

    public void setIs_creator(boolean is_creator) {
        this.is_creator = is_creator;
    }

    public boolean isIs_self() {
        return is_self;
    }

    public void setIs_self(boolean is_self) {
        this.is_self = is_self;
    }

    public boolean isIs_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public List<RoleInfosBean> getRole_infos() {
        return role_infos;
    }

    public void setRole_infos(List<RoleInfosBean> role_infos) {
        this.role_infos = role_infos;
    }

    public static class RoleInfosBean {
        /**
         * id : 1
         * name : 管理员
         */

        private int id;
        private String name;

        public RoleInfosBean() {
        }

        public RoleInfosBean(int id, String name) {
            this.id = id;
            this.name = name;
        }

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
