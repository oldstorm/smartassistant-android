package com.yctc.zhiting.entity.home;

import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.entity.mine.LocationBean;

import java.util.List;

/**
 * 同步数据
 */
public class SynPost {

    /**
     * nickname : dolore enim laboris laborum
     * area : {"name":"tempor conse","locations":[{"name":"cupidatat irure","sort":-52224372},{"name":"ipsum aliqua","sort":61093102}]}
     */

    private String nickname;
    private AreaBean area;

    public SynPost(String nickname, AreaBean area) {
        this.nickname = nickname;
        this.area = area;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public AreaBean getArea() {
        return area;
    }

    public void setArea(AreaBean area) {
        this.area = area;
    }

    public static class AreaBean extends Request {
        /**
         * name : tempor conse
         * locations : [{"name":"cupidatat irure","sort":-52224372},{"name":"ipsum aliqua","sort":61093102}]
         */

        private String name;
        private List<LocationBean> locations;

        public AreaBean(String name, List<LocationBean> locations) {
            this.name = name;
            this.locations = locations;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<LocationBean> getLocations() {
            return locations;
        }

        public void setLocations(List<LocationBean> locations) {
            this.locations = locations;
        }
    }
}
