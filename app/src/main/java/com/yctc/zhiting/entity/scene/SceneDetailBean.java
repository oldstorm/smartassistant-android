package com.yctc.zhiting.entity.scene;

import java.util.List;

public class SceneDetailBean {

    /**
     * id : 1
     * name : 场景1
     * condition_logic : 1
     * time_period : 2
     * repeat_type : 3
     * repeat_date : 1234567
     * auto_run : true
     * is_on : true
     * creator_id : 1
     * create_time : 1623222423
     * effect_start_time : 1623168121
     * effect_end_time : 1623254399
     * scene_conditions : [{"id":1,"scene_id":1,"condition_type":1,"device_id":0,"condition_item":{"id":0,"scene_condition_id":0,"action":"","attribute":"","operator":"","action_val":""},"timing":1623168000,"device_info":{"name":"","location_name":"","logo_url":"","status":0}}]
     * scene_tasks : [{"id":1,"scene_id":1,"control_scene_id":0,"delay_seconds":240,"type":1,"scene_task_devices":[{"id":1,"scene_task_id":1,"device_id":2,"action":"switch","attribute":"power","action_val":"on"},{"id":2,"scene_task_id":1,"device_id":2,"action":"set_bright","attribute":"brightness","action_val":"39"},{"id":3,"scene_task_id":1,"device_id":2,"action":"set_color_temp","attribute":"color_temp","action_val":"45"}],"control_scene_info":{"name":"","status":0},"device_info":{"name":"yeelight_ceiling123","location_name":"room","logo_url":"http://sa.zhitingtech.com/static/test-sa-test/plugins/light/html/static/img/led.0bd29fdd.png","status":1}}]
     */

    private int id;
    private String name;
    private int condition_logic;
    private int time_period;
    private int repeat_type;
    private String repeat_date;
    private boolean auto_run;
    private boolean is_on;
    private int creator_id;
    private int create_time;
    private long effect_start_time;
    private long effect_end_time;
    private List<SceneConditionsBean> scene_conditions;
    private List<SceneTasksBean> scene_tasks;

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

    public int getCondition_logic() {
        return condition_logic;
    }

    public void setCondition_logic(int condition_logic) {
        this.condition_logic = condition_logic;
    }

    public int getTime_period() {
        return time_period;
    }

    public void setTime_period(int time_period) {
        this.time_period = time_period;
    }

    public int getRepeat_type() {
        return repeat_type;
    }

    public void setRepeat_type(int repeat_type) {
        this.repeat_type = repeat_type;
    }

    public String getRepeat_date() {
        return repeat_date;
    }

    public void setRepeat_date(String repeat_date) {
        this.repeat_date = repeat_date;
    }

    public boolean isAuto_run() {
        return auto_run;
    }

    public void setAuto_run(boolean auto_run) {
        this.auto_run = auto_run;
    }

    public boolean isIs_on() {
        return is_on;
    }

    public void setIs_on(boolean is_on) {
        this.is_on = is_on;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public long getEffect_start_time() {
        return effect_start_time;
    }

    public void setEffect_start_time(long effect_start_time) {
        this.effect_start_time = effect_start_time;
    }

    public long getEffect_end_time() {
        return effect_end_time;
    }

    public void setEffect_end_time(long effect_end_time) {
        this.effect_end_time = effect_end_time;
    }

    public List<SceneConditionsBean> getScene_conditions() {
        return scene_conditions;
    }

    public void setScene_conditions(List<SceneConditionsBean> scene_conditions) {
        this.scene_conditions = scene_conditions;
    }

    public List<SceneTasksBean> getScene_tasks() {
        return scene_tasks;
    }

    public void setScene_tasks(List<SceneTasksBean> scene_tasks) {
        this.scene_tasks = scene_tasks;
    }

    public static class SceneConditionsBean {
        /**
         * id : 1
         * scene_id : 1
         * condition_type : 1
         * device_id : 0
         * condition_item : {"id":0,"scene_condition_id":0,"action":"","attribute":"","operator":"","action_val":""}
         * timing : 1623168000
         * device_info : {"name":"","location_name":"","logo_url":"","status":0}
         */

        private int id;
        private int scene_id;
        private int condition_type;
        private int device_id;
        private ConditionItemBean condition_item;
        private long timing;
        private DeviceInfoBean device_info;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getScene_id() {
            return scene_id;
        }

        public void setScene_id(int scene_id) {
            this.scene_id = scene_id;
        }

        public int getCondition_type() {
            return condition_type;
        }

        public void setCondition_type(int condition_type) {
            this.condition_type = condition_type;
        }

        public int getDevice_id() {
            return device_id;
        }

        public void setDevice_id(int device_id) {
            this.device_id = device_id;
        }

        public ConditionItemBean getCondition_item() {
            return condition_item;
        }

        public void setCondition_item(ConditionItemBean condition_item) {
            this.condition_item = condition_item;
        }

        public long getTiming() {
            return timing;
        }

        public void setTiming(long timing) {
            this.timing = timing;
        }

        public DeviceInfoBean getDevice_info() {
            return device_info;
        }

        public void setDevice_info(DeviceInfoBean device_info) {
            this.device_info = device_info;
        }

        public static class ConditionItemBean {
            /**
             * id : 0
             * scene_condition_id : 0
             * action :
             * attribute :
             * operator :
             * action_val :
             */

            private int id;
            private int scene_condition_id;
            private String action;
            private String attribute;
            private String operator;
            private String action_val;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getScene_condition_id() {
                return scene_condition_id;
            }

            public void setScene_condition_id(int scene_condition_id) {
                this.scene_condition_id = scene_condition_id;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public String getAttribute() {
                return attribute;
            }

            public void setAttribute(String attribute) {
                this.attribute = attribute;
            }

            public String getOperator() {
                return operator;
            }

            public void setOperator(String operator) {
                this.operator = operator;
            }

            public String getAction_val() {
                return action_val;
            }

            public void setAction_val(String action_val) {
                this.action_val = action_val;
            }
        }


    }

    public static class SceneTasksBean {
        /**
         * id : 1
         * scene_id : 1
         * control_scene_id : 0
         * delay_seconds : 240
         * type : 1
         * scene_task_devices : [{"id":1,"scene_task_id":1,"device_id":2,"action":"switch","attribute":"power","action_val":"on"},{"id":2,"scene_task_id":1,"device_id":2,"action":"set_bright","attribute":"brightness","action_val":"39"},{"id":3,"scene_task_id":1,"device_id":2,"action":"set_color_temp","attribute":"color_temp","action_val":"45"}]
         * control_scene_info : {"name":"","status":0}
         * device_info : {"name":"yeelight_ceiling123","location_name":"room","logo_url":"http://sa.zhitingtech.com/static/test-sa-test/plugins/light/html/static/img/led.0bd29fdd.png","status":1}
         */

        private int id;
        private int scene_id;
        private int control_scene_id;
        private int delay_seconds;
        private int type;
        private ControlSceneInfoBean control_scene_info;
        private DeviceInfoBean device_info;
        private List<SceneTaskDevicesBean> scene_task_devices;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getScene_id() {
            return scene_id;
        }

        public void setScene_id(int scene_id) {
            this.scene_id = scene_id;
        }

        public int getControl_scene_id() {
            return control_scene_id;
        }

        public void setControl_scene_id(int control_scene_id) {
            this.control_scene_id = control_scene_id;
        }

        public int getDelay_seconds() {
            return delay_seconds;
        }

        public void setDelay_seconds(int delay_seconds) {
            this.delay_seconds = delay_seconds;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public ControlSceneInfoBean getControl_scene_info() {
            return control_scene_info;
        }

        public void setControl_scene_info(ControlSceneInfoBean control_scene_info) {
            this.control_scene_info = control_scene_info;
        }

        public DeviceInfoBean getDevice_info() {
            return device_info;
        }

        public void setDevice_info(DeviceInfoBean device_info) {
            this.device_info = device_info;
        }

        public List<SceneTaskDevicesBean> getScene_task_devices() {
            return scene_task_devices;
        }

        public void setScene_task_devices(List<SceneTaskDevicesBean> scene_task_devices) {
            this.scene_task_devices = scene_task_devices;
        }

        public static class ControlSceneInfoBean {
            /**
             * name :
             * status : 0
             */

            private String name;
            private int status;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }


        public static class SceneTaskDevicesBean {
            /**
             * id : 1
             * scene_task_id : 1
             * device_id : 2
             * action : switch
             * attribute : power
             * action_val : on
             */

            private int id;
            private int scene_task_id;
            private int device_id;
            private String action;
            private String attribute;
            private String action_val;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getScene_task_id() {
                return scene_task_id;
            }

            public void setScene_task_id(int scene_task_id) {
                this.scene_task_id = scene_task_id;
            }

            public int getDevice_id() {
                return device_id;
            }

            public void setDevice_id(int device_id) {
                this.device_id = device_id;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public String getAttribute() {
                return attribute;
            }

            public void setAttribute(String attribute) {
                this.attribute = attribute;
            }

            public String getAction_val() {
                return action_val;
            }

            public void setAction_val(String action_val) {
                this.action_val = action_val;
            }
        }
    }

    public static class DeviceInfoBean {
        /**
         * name :
         * location_name :
         * logo_url :
         * status : 0
         */

        private String name;
        private String location_name;
        private String logo_url;
        private int status;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation_name() {
            return location_name;
        }

        public void setLocation_name(String location_name) {
            this.location_name = location_name;
        }

        public String getLogo_url() {
            return logo_url;
        }

        public void setLogo_url(String logo_url) {
            this.logo_url = logo_url;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
