package com.yctc.zhiting.entity;

import com.yctc.zhiting.entity.scene.CreateScenePost;
import com.yctc.zhiting.utils.StringUtil;

import java.io.Serializable;
import java.util.List;

public class SceneTaskBean implements Serializable {

    private Integer id;  // 任务id（修改时才有，从服务器获取）

    private String title;  // 左边名称
    private int type;  // 1:控制设备；2:手动执行场景;3:开启自动执行场景;4:关闭自动执行场景
    private int delay_seconds;  // 可不传，延迟秒数
    private int control_scene_id; // type为1时可不传
    private String subTitle; // 右边名称
    private String logo; // 设备logo
    private int hour;  // 延时时
    private int minute; // 延时分
    private int seconds;// 延时秒
    private String location; // 设备位置
    private String deviceType; // 设备类型
    private String timeStr;  // 延时字符串
    private int deviceStatus; // 设备状态设备状态:1为正常;2为被删除;3为离线
    private int sceneStatus; // 场景状态 1正常,2被删除
    private List<CreateScenePost.SceneTasksBean.SceneTaskDevicesBean> scene_task_devices;

    public SceneTaskBean() {
    }

    public SceneTaskBean(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDelay_seconds() {
        return delay_seconds;
    }

    public void setDelay_seconds(int delay_seconds) {
        this.delay_seconds = delay_seconds;
    }

    public int getControl_scene_id() {
        return control_scene_id;
    }

    public void setControl_scene_id(int control_scene_id) {
        this.control_scene_id = control_scene_id;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<CreateScenePost.SceneTasksBean.SceneTaskDevicesBean> getScene_task_devices() {
        return scene_task_devices;
    }

    public void setScene_task_devices(List<CreateScenePost.SceneTasksBean.SceneTaskDevicesBean> scene_task_devices) {
        this.scene_task_devices = scene_task_devices;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public int getSceneStatus() {
        return sceneStatus;
    }

    public void setSceneStatus(int sceneStatus) {
        this.sceneStatus = sceneStatus;
    }
}
