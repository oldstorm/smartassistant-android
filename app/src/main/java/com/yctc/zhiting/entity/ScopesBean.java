package com.yctc.zhiting.entity;

import java.util.List;

public class ScopesBean {


    private List<ScopeBean> scopes;

    public List<ScopeBean> getScopes() {
        return scopes;
    }

    public void setScopes(List<ScopeBean> scopes) {
        this.scopes = scopes;
    }

    public static class ScopeBean {
        /**
         * name : user
         * description : 获取你的登录状态
         */

        private String name;
        private String description;

        public ScopeBean(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
