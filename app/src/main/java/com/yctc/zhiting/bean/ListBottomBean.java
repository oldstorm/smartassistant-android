package com.yctc.zhiting.bean;

import java.io.Serializable;

public class ListBottomBean implements Serializable {

    private int id;
    private String name;
    private boolean selected;
    private boolean enabled;

    public ListBottomBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ListBottomBean(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public ListBottomBean(int id, String name, boolean selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }

    public ListBottomBean(int id, String name, boolean selected, boolean enabled) {
        this.id = id;
        this.name = name;
        this.selected = selected;
        this.enabled = enabled;
    }

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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
