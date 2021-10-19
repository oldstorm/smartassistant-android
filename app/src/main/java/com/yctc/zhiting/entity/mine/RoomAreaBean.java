package com.yctc.zhiting.entity.mine;

import java.io.Serializable;

public class RoomAreaBean implements Serializable {

    private int id;
    private String name;
    private int position;
    private boolean selected;

    public RoomAreaBean() {
    }

    public RoomAreaBean(String name) {
        this.name = name;
    }

    public RoomAreaBean(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public RoomAreaBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoomAreaBean(int id, String name, int position) {
        this.id = id;
        this.name = name;
        this.position = position;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
