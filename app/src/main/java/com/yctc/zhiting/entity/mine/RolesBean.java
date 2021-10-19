package com.yctc.zhiting.entity.mine;

import java.util.List;

public class RolesBean {

    private List<RoleBean> roles;

    public List<RoleBean> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleBean> roles) {
        this.roles = roles;
    }

    public static class RoleBean {
        /**
         * id : 1
         * name : 管理员
         * is_manager : true
         */

        private int id;
        private String name;
        private boolean is_manager;

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

        public boolean isIs_manager() {
            return is_manager;
        }

        public void setIs_manager(boolean is_manager) {
            this.is_manager = is_manager;
        }
    }
}
