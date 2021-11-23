package com.yctc.zhiting.entity.mine;

public class InvitationCheckBean {

    /**
     * user_info : {"user_id":0,"role_infos":null,"account_name":"","nickname":"","token":"","phone":"","is_set_password":false}
     */

    private UserInfoBean user_info;
    private IdBean area_info;

    public UserInfoBean getUser_info() {
        return user_info;
    }

    public void setUser_info(UserInfoBean user_info) {
        this.user_info = user_info;
    }

    public IdBean getArea_info() {
        return area_info;
    }

    public void setArea_info(IdBean area_info) {
        this.area_info = area_info;
    }

    public static class UserInfoBean {
        /**
         * user_id : 0
         * role_infos : null
         * account_name :
         * nickname :
         * token :
         * phone :
         * is_set_password : false
         */

        private int user_id;
        private Object role_infos;
        private String account_name;
        private String nickname;
        private String token;
        private String phone;
        private boolean is_set_password;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public Object getRole_infos() {
            return role_infos;
        }

        public void setRole_infos(Object role_infos) {
            this.role_infos = role_infos;
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
    }
}
