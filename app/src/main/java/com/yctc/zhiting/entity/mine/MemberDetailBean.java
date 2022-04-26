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
    private boolean is_owner; //是否是SA拥有者
    private String avatar_url;
    private List<RoleInfosBean> role_infos;
    private List<LocationBean> department_infos;
    private AreaEntity area;

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

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public List<RoleInfosBean> getRole_infos() {
        return role_infos;
    }

    public void setRole_infos(List<RoleInfosBean> role_infos) {
        this.role_infos = role_infos;
    }

    public List<LocationBean> getDepartment_infos() {
        return department_infos;
    }

    public void setDepartment_infos(List<LocationBean> department_infos) {
        this.department_infos = department_infos;
    }

    public AreaEntity getArea() {
        return area;
    }

    public void setArea(AreaEntity area) {
        this.area = area;
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

    public static class AreaEntity {
        private String id;
        private String name;
        private int mode;
        private List<DepartmentInfoBean> departments;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public List<DepartmentInfoBean> getDepartments() {
            return departments;
        }

        public void setDepartments(List<DepartmentInfoBean> departments) {
            this.departments = departments;
        }
    }
}
