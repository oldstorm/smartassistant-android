package com.yctc.zhiting.entity;

import java.io.Serializable;
import java.util.List;

public class CreatePluginListBean {

    private List<PluginsBean> plugins;

    public List<PluginsBean> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<PluginsBean> plugins) {
        this.plugins = plugins;
    }

    public static class PluginsBean implements Serializable {
        /**
         * id : occaecat dolore aliquip
         * version : ea do ut
         * name : reprehenderit qui ut sed
         * info : Duis enim Ut Lorem
         * build_status : 31504334  -1build失败,0正在build,1build成功
         */

        private String id;
        private String version;
        private String name;
        private String info;
        private int build_status;
        private boolean loading;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public int getBuild_status() {
            return build_status;
        }

        public void setBuild_status(int build_status) {
            this.build_status = build_status;
        }

        public boolean isLoading() {
            return loading;
        }

        public void setLoading(boolean loading) {
            this.loading = loading;
        }
    }
}
