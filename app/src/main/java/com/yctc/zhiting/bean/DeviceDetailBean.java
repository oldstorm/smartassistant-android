package com.yctc.zhiting.bean;

import java.util.List;

public class DeviceDetailBean {


    /**
     * device_info : {"actions":[{"action":"switch","attr":"power","name":"开关"},{"action":"set_bright","attr":"brightness","name":"亮度"},{"action":"set_color_temp","attr":"color_temp","name":"色温"}],"id":2,"location":{"id":1,"name":"room"},"logo_url":"http://192.168.0.84:8088/static/test-sa-test/plugins/light/html/static/img/led.0bd29fdd.png","model":"ceiling17","name":"yeelight吸顶灯","permissions":{"delete_device":true,"update_device":true},"plugin":{"id":"light_001","name":"light","url":"http://192.168.0.84:8088/static/test-sa-test/plugins/light/html?device_id=2&model=ceiling17&name=yeelight%E5%90%B8%E9%A1%B6%E7%81%AF&sa_id=test-sa-test&token=MTYyMzIyMDgwMHxOd3dBTkRORVRsRXlWRXRJVjFCT1dGaE5SVFZKVFU5S01qTllVa1ZPVWtSSk1rWlpRVFJVVmt0R1ZUVTNNa1pOVFVoYVZrSTFWbEU9fAhbPb5tiW4pSxhH0HG9oOZTBbhPco2jniuEVPa5K6Qj"}}
     */

    private DeviceInfoBean device_info;

    public DeviceInfoBean getDevice_info() {
        return device_info;
    }

    public void setDevice_info(DeviceInfoBean device_info) {
        this.device_info = device_info;
    }

    public static class DeviceInfoBean {
        /**
         * actions : [{"action":"switch","attr":"power","name":"开关"},{"action":"set_bright","attr":"brightness","name":"亮度"},{"action":"set_color_temp","attr":"color_temp","name":"色温"}]
         * id : 2
         * location : {"id":1,"name":"room"}
         * logo_url : http://192.168.0.84:8088/static/test-sa-test/plugins/light/html/static/img/led.0bd29fdd.png
         * model : ceiling17
         * name : yeelight吸顶灯
         * permissions : {"delete_device":true,"update_device":true}
         * plugin : {"id":"light_001","name":"light","url":"http://192.168.0.84:8088/static/test-sa-test/plugins/light/html?device_id=2&model=ceiling17&name=yeelight%E5%90%B8%E9%A1%B6%E7%81%AF&sa_id=test-sa-test&token=MTYyMzIyMDgwMHxOd3dBTkRORVRsRXlWRXRJVjFCT1dGaE5SVFZKVFU5S01qTllVa1ZPVWtSSk1rWlpRVFJVVmt0R1ZUVTNNa1pOVFVoYVZrSTFWbEU9fAhbPb5tiW4pSxhH0HG9oOZTBbhPco2jniuEVPa5K6Qj"}
         */

        private int id;
        private LocationBean location;
        private LocationBean department;
        private String logo_url;
        private String model;
        private String name;
        private PermissionsBean permissions;
        private PluginBean plugin;
        private List<ActionsBean> actions;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public LocationBean getDepartment() {
            return department;
        }

        public void setDepartment(LocationBean department) {
            this.department = department;
        }

        public String getLogo_url() {
            return logo_url;
        }

        public void setLogo_url(String logo_url) {
            this.logo_url = logo_url;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public PermissionsBean getPermissions() {
            return permissions;
        }

        public void setPermissions(PermissionsBean permissions) {
            this.permissions = permissions;
        }

        public PluginBean getPlugin() {
            return plugin;
        }

        public void setPlugin(PluginBean plugin) {
            this.plugin = plugin;
        }

        public List<ActionsBean> getActions() {
            return actions;
        }

        public void setActions(List<ActionsBean> actions) {
            this.actions = actions;
        }

        public static class LocationBean {
            /**
             * id : 1
             * name : room
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

        public static class PermissionsBean {
            /**
             * delete_device : true
             * update_device : true
             */

            private boolean delete_device;
            private boolean update_device;

            public boolean isDelete_device() {
                return delete_device;
            }

            public void setDelete_device(boolean delete_device) {
                this.delete_device = delete_device;
            }

            public boolean isUpdate_device() {
                return update_device;
            }

            public void setUpdate_device(boolean update_device) {
                this.update_device = update_device;
            }
        }

        public static class PluginBean {
            /**
             * id : light_001
             * name : light
             * url : http://192.168.0.84:8088/static/test-sa-test/plugins/light/html?device_id=2&model=ceiling17&name=yeelight%E5%90%B8%E9%A1%B6%E7%81%AF&sa_id=test-sa-test&token=MTYyMzIyMDgwMHxOd3dBTkRORVRsRXlWRXRJVjFCT1dGaE5SVFZKVFU5S01qTllVa1ZPVWtSSk1rWlpRVFJVVmt0R1ZUVTNNa1pOVFVoYVZrSTFWbEU9fAhbPb5tiW4pSxhH0HG9oOZTBbhPco2jniuEVPa5K6Qj
             */

            private String id;
            private String name;
            private String url;

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

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        public static class ActionsBean {
            /**
             * action : switch
             * attr : power
             * name : 开关
             */

            private String action;
            private String attr;
            private String name;

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public String getAttr() {
                return attr;
            }

            public void setAttr(String attr) {
                this.attr = attr;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
