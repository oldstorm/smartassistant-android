package com.yctc.zhiting.entity.scene;

import java.io.Serializable;

public class ConditionBean implements Serializable {

    private Integer id;  // 条件id（修改时才有，从服务器获取）

    private String name;  // 名称
    private int type;  // 类型  0.手动 1.定时 2.场景
    private String location;  // 场景时才有，设备位置
    private long timing;  // 定时才有， 定时时长
    private String timingStr; //定时才有， 定时时长字符串
    private int deviceId; // 场景时才有，设备id
    private String deviceName; // 场景时才有，设备名称
    private String deviceType; // 设备类型
    private String logoUrl; // 设备logo
    private int scene_id; // 场景id，修改才有用
    private CreateScenePost.SceneConditionsBean.ConditionItemBean condition_item; // 场景时才有，设备条件

    public ConditionBean(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getScene_id() {
        return scene_id;
    }

    public void setScene_id(int scene_id) {
        this.scene_id = scene_id;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getTiming() {
        return timing;
    }

    public void setTiming(long timing) {
        this.timing = timing;
    }

    public String getTimingStr() {
        return timingStr;
    }

    public void setTimingStr(String timingStr) {
        this.timingStr = timingStr;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public CreateScenePost.SceneConditionsBean.ConditionItemBean getCondition_item() {
        return condition_item;
    }

    public void setCondition_item(CreateScenePost.SceneConditionsBean.ConditionItemBean condition_item) {
        this.condition_item = condition_item;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
