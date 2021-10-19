package com.yctc.zhiting.entity.scene;

import java.io.Serializable;

public class SceneControlSceneInfoEntity implements Serializable {
    private String name; // 被控制的场景名称
    private int status;  // 场景状态;1正常,2被删除

    public SceneControlSceneInfoEntity() {
    }

    public SceneControlSceneInfoEntity(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
