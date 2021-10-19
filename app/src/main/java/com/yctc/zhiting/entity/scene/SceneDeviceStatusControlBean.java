package com.yctc.zhiting.entity.scene;

public class SceneDeviceStatusControlBean {

    /**
     * 1. 开关
     * 2. 亮度
     * 3. 色温
     */
    private int type;
    private String name;
    private String value;

    private Integer id; // 修改场景才有
    private Integer scene_task_id;  // 场景任务id 修改场景才有

    /**
     * 开关:power; 色温：color_temp；亮度:brightness
     */
    private String alias;

    public SceneDeviceStatusControlBean(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public SceneDeviceStatusControlBean(int type, String name, String alias) {
        this.type = type;
        this.name = name;
        this.alias = alias;
    }

    public SceneDeviceStatusControlBean(int type, String name, String value, String alias) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.alias = alias;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
