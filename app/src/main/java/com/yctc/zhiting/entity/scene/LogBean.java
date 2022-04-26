package com.yctc.zhiting.entity.scene;

import java.util.List;

public class LogBean {

    /**
     * date : 2021-06
     * items : [{"name":"日志","type":2,"result":1,"finished_at":1623725281,"items":[]}]
     */

    private String date;
    private List<ItemsBean> items;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {
        /**
         * name : 日志
         * type : 2
         * result : 1
         * finished_at : 1623725281
         * items : []
         */

        private String name;
        private int type;
        /**
         * 任务结果:1执行完成;2部分执行完成;3执行失败;4执行超时;5设备已被删除;6设备离线;7场景已被删除
         */
        private int result;
        private int finished_at;
        private List<DeviceStatusBean> items;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public int getFinished_at() {
            return finished_at;
        }

        public void setFinished_at(int finished_at) {
            this.finished_at = finished_at;
        }

        public List<DeviceStatusBean> getItems() {
            return items;
        }

        public void setItems(List<DeviceStatusBean> items) {
            this.items = items;
        }
    }

    public static class DeviceStatusBean {

        private String name;
        private int type;
        private String location_name;
        private String department_name;
        /**
         * 子任务结果:1执行完成;2部分执行完成;3执行失败;4执行超时;5设备已被删除;6设备离线;7场景已被删除
         */
        private int result;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getLocation_name() {
            return location_name;
        }

        public void setLocation_name(String location_name) {
            this.location_name = location_name;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getDepartment_name() {
            return department_name;
        }

        public void setDepartment_name(String department_name) {
            this.department_name = department_name;
        }
    }
}
