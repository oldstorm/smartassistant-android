package com.yctc.zhiting.entity.mine;

import java.io.Serializable;
import java.util.List;

public class ThirdPartyBean {

    private List<AppsBean> apps;

    public List<AppsBean> getApps() {
        return apps;
    }

    public void setApps(List<AppsBean> apps) {
        this.apps = apps;
    }

    public static class AppsBean implements Serializable {
        /**
         * app_id : 1.8964135430035263E7
         * name : cupidatat anim
         * is_bind : true
         * img : dolore ullamco ut
         * link : cupidatat labore ut quis
         */

        private Integer app_id;
        private String name;
        private boolean is_bind;
        private String img;
        private String link;

        public Integer getApp_id() {
            return app_id;
        }

        public void setApp_id(Integer app_id) {
            this.app_id = app_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isIs_bind() {
            return is_bind;
        }

        public void setIs_bind(boolean is_bind) {
            this.is_bind = is_bind;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
