package com.yctc.zhiting.entity.scene;

import java.io.Serializable;

/**
 * 场景触发条件
 */
public class SceneConditionEntity implements Serializable {
    private Integer id; //触发条件Id
    private Integer scene_id; // 场景Id
    private int condition_type; //0手动（本地设定）， 1为定时，2为设备状态变化时
    private Integer device_id; // 设备id
    private SceneDeviceInfoEntity device_info; // 触发条件为设备时,设备的信息 备注: 触发条件为设备时,设备的信息
    private String operator; // =, >, <
    private SceneConditionAttrEntity condition_attr; // 触发条件为设备时对应的设备属性变化 备注: 触发条件为设备时对应的设备属性变化
    private Long timing; //触发条件为定时的值

    public SceneConditionEntity() {
    }

    public SceneConditionEntity(int condition_type) {
        this.condition_type = condition_type;
    }

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

    public int getCondition_type() {
        return condition_type;
    }

    public void setCondition_type(int condition_type) {
        this.condition_type = condition_type;
    }

    public Integer getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Integer device_id) {
        this.device_id = device_id;
    }

    public SceneDeviceInfoEntity getDevice_info() {
        return device_info;
    }

    public void setDevice_info(SceneDeviceInfoEntity device_info) {
        this.device_info = device_info;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public SceneConditionAttrEntity getCondition_attr() {
        return condition_attr;
    }

    public void setCondition_attr(SceneConditionAttrEntity condition_attr) {
        this.condition_attr = condition_attr;
    }

    public Long getTiming() {
        return timing;
    }

    public void setTiming(Long timing) {
        this.timing = timing;
    }
}
