package com.yctc.zhiting.entity.scene;

/**
 * 执行任务列表 和  触发条件
 */
public class SceneItemBean {
    /**
     * type : 1
     * logo_url : http://sa.zhitingtech.com/static/test-sa/plugins/light/html/static/img/lamp.da1b67cc.png
     * status : 1
     */

    /***
     * 执行任务列表时 设备状态:1为正常,2为已删除,3为离线;场景状态:1为正常,2为已删除
     * 触发条件时 触发条件类型;1为定时任务, 2为设备
     */
    private int type;

    /***
     * 执行任务列表时 设备图片
     * 触发条件为设备时返回设备图片url
     */
    private String logo_url;

    /***
     * 执行任务列表时 设备状态:1为正常,2为已删除,3为离线;场景状态:1为正常,2为已删除
     * 触发条件时 设备状态:1正常2已删除3离线
     */
    private int status;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
