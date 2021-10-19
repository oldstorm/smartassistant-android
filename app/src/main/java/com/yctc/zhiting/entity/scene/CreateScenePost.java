package com.yctc.zhiting.entity.scene;

import java.io.Serializable;
import java.util.List;

public class CreateScenePost {


    /**
     * name : cupidatat elit sint
     * condition_logic : -3.40708566484532E7
     * auto_run : true
     * time_period : 8.875310732161772E7
     * effect_start_time : -85733841
     * effect_end_time : -5244550
     * repeat_type : 4.036302661041483E7
     * repeat_date : veniam occaecat consequat aliqua
     * scene_conditions : [{"condition_type":2.1936936812908977E7,"timing":16620987,"device_id":-4.133312362076316E7,"condition_item":{"operator":"dolore Lorem","action":"sint dolore","action_val":"velit","attribute":"in cillum labore deserunt"}},{"condition_type":9.936452768458101E7,"timing":-58926802,"device_id":-6.074850339601721E7,"condition_item":{"operator":"commodo nulla mollit amet ut","action":"sit dolor reprehenderit elit","action_val":"consectetur Duis dolor dolo","attribute":"nisi ut id in"}},{"condition_type":-5.626900598192921E7,"timing":85295543,"device_id":-1.1576819629145205E7,"condition_item":{"operator":"nisi ea reprehenderit","action":"eu sunt","action_val":"reprehenderit sit dolor magna tempor","attribute":"ipsum ad Lo"}},{"condition_type":3.943851006131092E7,"timing":-99750154,"device_id":1.0325780979218856E7,"condition_item":{"operator":"voluptate ea Ut","action":"ea sed eius","action_val":"esse pariatur","attribute":"officia Duis velit ipsum"}}]
     * scene_tasks : [{"type":2.8085959317014143E7,"delay_seconds":7.289704832400322E7,"control_scene_id":-9.762338860838598E7,"scene_task_devices":[{"action":"aliquip officia occaecat ut Duis","device_id":-7823113.126918629,"action_val":"incididunt culpa laborum","attribute":"pariatur commod"},{"action":"esse","device_id":7.008760236407894E7,"action_val":"laboris Lorem sint aliqua","attribute":"cillum consectetur "}]},{"type":-7004501.940550163,"delay_seconds":7.47921422109012E7,"control_scene_id":-1.71385315196757E7,"scene_task_devices":[{"action":"ullamco do dolor elit","device_id":1.278004032087496E7,"action_val":"pariatur eu in","attribute":"est"},{"action":"sint in","device_id":-7.473641927939373E7,"action_val":"adipisicing proident ipsum deserunt","attribute":"dolor tempor Duis"},{"action":"ex eu in","device_id":2.6171482182557598E7,"action_val":"sed","attribute":"mollit in sunt laborum"}]},{"type":-7.432857146214564E7,"delay_seconds":7.740561237815395E7,"control_scene_id":-9.799139887923065E7,"scene_task_devices":[{"action":"aliqu","device_id":1.5840861281624943E7,"action_val":"Ut dolor in Duis","attribute":"aute cillum non"},{"action":"laboris tempor est cupidatat","device_id":-5464862.0383371115,"action_val":"dolore Duis","attribute":"sit minim ullamco"}]},{"type":-1.6044622857837036E7,"delay_seconds":3.0452390849122494E7,"control_scene_id":4.956499443249363E7,"scene_task_devices":[{"action":"ut","device_id":3.534869500667375E7,"action_val":"irure officia","attribute":"pariatur aliquip et tempor"},{"action":"consequat Lorem c","device_id":-5.724288785513787E7,"action_val":"laboris laborum tempor aute","attribute":"ut proident ex"},{"action":"commodo dolore nisi dolore cillum","device_id":-8.964095738919395E7,"action_val":"eiusmod ullamco consequat","attribute":"nulla"},{"action":"occaecat ad","device_id":9.908246039367059E7,"action_val":"tempor Lorem culpa Ut","attribute":"ex"}]}]
     */

    private Integer id; // 场景Id(修改时才有)
    private String name;  // 场景名称
    private Integer condition_logic;  // auto_run 为false时可不传，1满足所有，2，满足任一
    private boolean auto_run;  // true 为自动，false为手动
    private Integer time_period;  // 生效时间类型，全天为1，时间段为2,auto_run为false可不传
    private Long effect_start_time;  // 生效开始时间,time_period为1时应传某天0点;auto_run为false可不传
    private Long effect_end_time;  // 生效结束时间,time_period为1时应传某天24点;auto_run为false可不传
    private Integer repeat_type;  // 重复执行的类型；1：每天; 2:工作日 ；3：自定义;auto_run为false可不传
    private String repeat_date;  // 只能传长度为7包含1-7的数字；"1122"视为不合法传参;repeat_type为1时:"1234567"; 2:12345; 3：任意
    private List<SceneConditionsBean> scene_conditions; //  执行条件，  auto_run 为手动时不传
    private List<SceneTasksBean> scene_tasks;  // 执行任务

    private List<Integer> del_condition_ids; // 要移除的触发条件(修改时才有)
    private List<Integer> del_task_ids; // 要移除的任务(修改时才有)

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCondition_logic() {
        return condition_logic;
    }

    public void setCondition_logic(Integer condition_logic) {
        this.condition_logic = condition_logic;
    }

    public boolean isAuto_run() {
        return auto_run;
    }

    public void setAuto_run(boolean auto_run) {
        this.auto_run = auto_run;
    }

    public Integer getTime_period() {
        return time_period;
    }

    public void setTime_period(Integer time_period) {
        this.time_period = time_period;
    }

    public Long getEffect_start_time() {
        return effect_start_time;
    }

    public void setEffect_start_time(Long effect_start_time) {
        this.effect_start_time = effect_start_time;
    }

    public Long getEffect_end_time() {
        return effect_end_time;
    }

    public void setEffect_end_time(Long effect_end_time) {
        this.effect_end_time = effect_end_time;
    }

    public Integer getRepeat_type() {
        return repeat_type;
    }

    public void setRepeat_type(Integer repeat_type) {
        this.repeat_type = repeat_type;
    }

    public String getRepeat_date() {
        return repeat_date;
    }

    public void setRepeat_date(String repeat_date) {
        this.repeat_date = repeat_date;
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


    public List<Integer> getDel_condition_ids() {
        return del_condition_ids;
    }

    public void setDel_condition_ids(List<Integer> del_condition_ids) {
        this.del_condition_ids = del_condition_ids;
    }

    public List<Integer> getDel_task_ids() {
        return del_task_ids;
    }

    public void setDel_task_ids(List<Integer> del_task_ids) {
        this.del_task_ids = del_task_ids;
    }

    public static class SceneConditionsBean {
        /**
         * condition_type : 2.1936936812908977E7
         * timing : 16620987
         * device_id : -4.133312362076316E7
         * condition_item : {"operator":"dolore Lorem","action":"sint dolore","action_val":"velit","attribute":"in cillum labore deserunt"}
         */


        private int id;// 修改才有
        private int scene_id; // 修改才有

        private int condition_type;
        private long timing;
        private int device_id;
        private ConditionItemBean condition_item;

        public SceneConditionsBean(int condition_type, long timing, int device_id, ConditionItemBean condition_item) {
            this.condition_type = condition_type;
            this.timing = timing;
            this.device_id = device_id;
            this.condition_item = condition_item;
        }

        public SceneConditionsBean(int id, int scene_id, int condition_type, long timing, int device_id, ConditionItemBean condition_item) {
            this.id = id;
            this.scene_id = scene_id;
            this.condition_type = condition_type;
            this.timing = timing;
            this.device_id = device_id;
            this.condition_item = condition_item;
        }

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

        public long getTiming() {
            return timing;
        }

        public void setTiming(long timing) {
            this.timing = timing;
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

        public static class ConditionItemBean  implements Serializable {
            /**
             * operator : dolore Lorem
             * action : sint dolore
             * action_val : velit
             * attribute : in cillum labore deserunt
             */

            private int id;  // 触发条件id，修改才有
            private int scene_condition_id; // 场景条件id，修改才有

            private String operator;
            private String action;
            private String action_val;
            private String attribute;

            public ConditionItemBean(String operator, String action, String action_val, String attribute) {
                this.operator = operator;
                this.action = action;
                this.action_val = action_val;
                this.attribute = attribute;
            }

            public ConditionItemBean(int id, int scene_condition_id, String operator, String action, String action_val, String attribute) {
                this.id = id;
                this.scene_condition_id = scene_condition_id;
                this.operator = operator;
                this.action = action;
                this.action_val = action_val;
                this.attribute = attribute;
            }

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

            public String getOperator() {
                return operator;
            }

            public void setOperator(String operator) {
                this.operator = operator;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public String getAction_val() {
                return action_val;
            }

            public void setAction_val(String action_val) {
                this.action_val = action_val;
            }

            public String getAttribute() {
                return attribute;
            }

            public void setAttribute(String attribute) {
                this.attribute = attribute;
            }
        }
    }

    public static class SceneTasksBean {
        /**
         * type : 2.8085959317014143E7
         * delay_seconds : 7.289704832400322E7
         * control_scene_id : -9.762338860838598E7
         * scene_task_devices : [{"action":"aliquip officia occaecat ut Duis","device_id":-7823113.126918629,"action_val":"incididunt culpa laborum","attribute":"pariatur commod"},{"action":"esse","device_id":7.008760236407894E7,"action_val":"laboris Lorem sint aliqua","attribute":"cillum consectetur "}]
         */

        private Integer id;  // 执行任务Id,修改该执行任务时必须传   修改才有
        private Integer scene_id;  // 场景Id  修改才有
        private Integer type;  // 1:控制设备；2:手动执行场景;3:开启自动执行场景;4:关闭自动执行场景
        private Integer delay_seconds;  // 延迟秒数;表示任务延迟执行的时间
        private Integer control_scene_id;  // 控制场景的id；type为1时可不传
        private List<SceneTaskDevicesBean> scene_task_devices;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getScene_id() {
            return scene_id;
        }

        public void setScene_id(Integer scene_id) {
            this.scene_id = scene_id;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getDelay_seconds() {
            return delay_seconds;
        }

        public void setDelay_seconds(Integer delay_seconds) {
            this.delay_seconds = delay_seconds;
        }

        public Integer getControl_scene_id() {
            return control_scene_id;
        }

        public void setControl_scene_id(Integer control_scene_id) {
            this.control_scene_id = control_scene_id;
        }

        public List<SceneTaskDevicesBean> getScene_task_devices() {
            return scene_task_devices;
        }

        public void setScene_task_devices(List<SceneTaskDevicesBean> scene_task_devices) {
            this.scene_task_devices = scene_task_devices;
        }

        public static class SceneTaskDevicesBean implements Serializable{
            /**
             * action : aliquip officia occaecat ut Duis
             * device_id : -7823113.126918629
             * action_val : incididunt culpa laborum
             * attribute : pariatur commod
             */

            private String action;
            private int device_id;
            private String action_val;
            private String attribute;

            private Integer id; // 修改场景才有
            private Integer scene_task_id;  // 场景任务id 修改场景才有


            public SceneTaskDevicesBean(String action, int device_id, String action_val, String attribute) {
                this.action = action;
                this.device_id = device_id;
                this.action_val = action_val;
                this.attribute = attribute;
            }

            public SceneTaskDevicesBean(String action, int device_id, String action_val, String attribute, Integer id, Integer scene_task_id) {
                this.action = action;
                this.device_id = device_id;
                this.action_val = action_val;
                this.attribute = attribute;
                this.id = id;
                this.scene_task_id = scene_task_id;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public int getDevice_id() {
                return device_id;
            }

            public void setDevice_id(int device_id) {
                this.device_id = device_id;
            }

            public String getAction_val() {
                return action_val;
            }

            public void setAction_val(String action_val) {
                this.action_val = action_val;
            }

            public String getAttribute() {
                return attribute;
            }

            public void setAttribute(String attribute) {
                this.attribute = attribute;
            }

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public Integer getScene_task_id() {
                return scene_task_id;
            }

            public void setScene_task_id(Integer scene_task_id) {
                this.scene_task_id = scene_task_id;
            }
        }
    }
}
