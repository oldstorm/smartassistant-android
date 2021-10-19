package com.yctc.zhiting.entity.scene;

import java.util.List;

/**
 * 场景列表
 */
public class SceneListBean {


    private List<SceneBean> manual;
    private List<SceneBean> auto_run;

    public List<SceneBean> getManual() {
        return manual;
    }

    public void setManual(List<SceneBean> manual) {
        this.manual = manual;
    }

    public List<SceneBean> getAuto_run() {
        return auto_run;
    }

    public void setAuto_run(List<SceneBean> auto_run) {
        this.auto_run = auto_run;
    }
}
