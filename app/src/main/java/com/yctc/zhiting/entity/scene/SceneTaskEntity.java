package com.yctc.zhiting.entity.scene;

import java.io.Serializable;
import java.util.List;

public class SceneTaskEntity implements Serializable {

    private Integer id; // 执行任务Id
    private Integer scene_id; // 场景Id
    private Integer device_id; // 设备id
    private int delay_seconds; //延迟执行时间
    private int type; // 执行任务类型;1为控制设备, 2 3 4为控制场景
    private Integer control_scene_id; // 被控制的场景id
    private SceneControlSceneInfoEntity control_scene_info; // 被控制场景信息
    private SceneDeviceInfoEntity device_info; // 设备的信息
    private List<SceneConditionAttrEntity> attributes; // 控制设备时，对应的设备属性

    public SceneTaskEntity() {
    }

    public SceneTaskEntity(int type) {
        this.type = type;
    }

    public SceneTaskEntity(int type, Integer control_scene_id) {
        this.type = type;
        this.control_scene_id = control_scene_id;
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

    public Integer getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Integer device_id) {
        this.device_id = device_id;
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

    public Integer getControl_scene_id() {
        return control_scene_id;
    }

    public void setControl_scene_id(Integer control_scene_id) {
        this.control_scene_id = control_scene_id;
    }

    public SceneControlSceneInfoEntity getControl_scene_info() {
        return control_scene_info;
    }

    public void setControl_scene_info(SceneControlSceneInfoEntity control_scene_info) {
        this.control_scene_info = control_scene_info;
    }

    public SceneDeviceInfoEntity getDevice_info() {
        return device_info;
    }

    public void setDevice_info(SceneDeviceInfoEntity device_info) {
        this.device_info = device_info;
    }

    public List<SceneConditionAttrEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<SceneConditionAttrEntity> attributes) {
        this.attributes = attributes;
    }
}
