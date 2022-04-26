package com.yctc.zhiting.request;

import java.util.List;

public class FeedbackRequest {

    private int feedback_type; // 类型(1:遇到问题 2:提建议)
    /**
     * 分类	(1: 应用问题  2: 注册和登录问题  3: 用户数据问题 4: 设备问题 5: 场景问题 6: 其他问题 7: 应用功能建议 8: 设备功能建议 9: 场景功能建议 10: 其他功能建议)
     */
    private int type;
    private String description;  // 描述
    private List<Integer> file_ids; // 图片/视频
    private String contact_information; // 联系方式
    private boolean is_auth; // 是否同意获取信息
    private String api_version; // 固件版本号
    private String app_version; // app版本号
    private String phone_model; // 设备型号
    private String phone_system; // 手机系统及版本
    private String sa_id; // sa的id

    public int getFeedback_type() {
        return feedback_type;
    }

    public void setFeedback_type(int feedback_type) {
        this.feedback_type = feedback_type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getFile_ids() {
        return file_ids;
    }

    public void setFile_ids(List<Integer> file_ids) {
        this.file_ids = file_ids;
    }

    public String getContact_information() {
        return contact_information;
    }

    public void setContact_information(String contact_information) {
        this.contact_information = contact_information;
    }

    public boolean isIs_auth() {
        return is_auth;
    }

    public void setIs_auth(boolean is_auth) {
        this.is_auth = is_auth;
    }

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getPhone_model() {
        return phone_model;
    }

    public void setPhone_model(String phone_model) {
        this.phone_model = phone_model;
    }

    public String getPhone_system() {
        return phone_system;
    }

    public void setPhone_system(String phone_system) {
        this.phone_system = phone_system;
    }

    public String getSa_id() {
        return sa_id;
    }

    public void setSa_id(String sa_id) {
        this.sa_id = sa_id;
    }
}
