package com.yctc.zhiting.entity.scene;

import java.util.List;

public class SceneBean {
    /**
     * id : 2
     * name : 场景2
     * control_permission : true
     * is_on : true
     * condition : {"type":1,"logo_url":"","status":0}
     * items : [{"type":1,"logo_url":"http://sa.zhitingtech.com/static/test-sa/plugins/light/html/static/img/led.0bd29fdd.png","status":1},{"type":2,"logo_url":"","status":1}]
     */

    private int id;
    private String name;
    private boolean control_permission;
    private boolean is_on;  // 自动才有
    private boolean performing; // 执行中
    private SceneItemBean condition; // 自动才有
    private List<SceneItemBean> items;

    private boolean selected;

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

    public boolean isControl_permission() {
        return control_permission;
    }

    public void setControl_permission(boolean control_permission) {
        this.control_permission = control_permission;
    }

    public boolean isIs_on() {
        return is_on;
    }

    public void setIs_on(boolean is_on) {
        this.is_on = is_on;
    }

    public SceneItemBean getCondition() {
        return condition;
    }

    public void setCondition(SceneItemBean condition) {
        this.condition = condition;
    }

    public boolean isPerforming() {
        return performing;
    }

    public void setPerforming(boolean performing) {
        this.performing = performing;
    }

    public List<SceneItemBean> getItems() {
        return items;
    }

    public void setItems(List<SceneItemBean> items) {
        this.items = items;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
