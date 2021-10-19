package com.yctc.zhiting.entity.scene;

import java.util.List;

/**
 * 场景详情（重构的数据）
 */
public class SceneDetailEntity {

    private Integer id; //场景id
    private String name; // 场景名称
    private Integer condition_logic; // 1，为满足全部条件，2为满足任一条件
    private Integer time_period; // 生效时间配置：1为全天，2为时间段
    private Long effect_start_time; // 生效开始时间,time_period为1时应传某天0点;
    private Long effect_end_time; // 生效开始时间,time_period为1时应传某天0点;
    private Integer repeat_type; // 重复执行的配置1为每天，2为工作日，3为自定义
    private String repeat_date;  // repeat_type为1时应"1234567".工作日：12345，3:自定义
    private Boolean auto_run; // true为自动，false为手动
    private Boolean is_on;  // 场景的开启或关闭状态。自动场景默认为true
    private Integer creator_id; // 创建者id
    private Long create_time; // 创建时间
    private List<SceneConditionEntity> scene_conditions; // 触发条件
    private List<SceneTaskEntity> scene_tasks; //执行任务列表

    private List<Integer> del_condition_ids; // 要移除的触发条件(修改时才有)，本地字段
    private List<Integer> del_task_ids; // 要移除的任务(修改时才有)，本地字段

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

    public Boolean getAuto_run() {
        return auto_run;
    }

    public void setAuto_run(Boolean auto_run) {
        this.auto_run = auto_run;
    }

    public Boolean getIs_on() {
        return is_on;
    }

    public void setIs_on(Boolean is_on) {
        this.is_on = is_on;
    }

    public Integer getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(Integer creator_id) {
        this.creator_id = creator_id;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public List<SceneConditionEntity> getScene_conditions() {
        return scene_conditions;
    }

    public void setScene_conditions(List<SceneConditionEntity> scene_conditions) {
        this.scene_conditions = scene_conditions;
    }

    public List<SceneTaskEntity> getScene_tasks() {
        return scene_tasks;
    }

    public void setScene_tasks(List<SceneTaskEntity> scene_tasks) {
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
}
