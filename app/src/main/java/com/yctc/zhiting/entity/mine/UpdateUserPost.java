package com.yctc.zhiting.entity.mine;

import com.app.main.framework.httputil.request.Request;

import java.util.List;

public class UpdateUserPost extends Request {

    /**
     * nickname : Lor
     * account_name : consectetur cillum
     * password : eu elit Lorem
     * role_ids : [40347057,45076557,-95401413]
     */

    private String nickname;
    private String account_name;
    private String password;

    private Integer avatar_id; // 头像id，上传头像后使用
    private String avatar_url; // 头像url，更新头像为这个url地址

    private List<Integer> role_ids;
    private List<Integer> department_ids;

    private String old_password;
    private String new_password;

    public UpdateUserPost() {
    }

    public UpdateUserPost(String old_password, String new_password) {
        this.old_password = old_password;
        this.new_password = new_password;
    }

    public UpdateUserPost(String name) {
        this.nickname = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public Integer getAvatar_id() {
        return avatar_id;
    }

    public void setAvatar_id(Integer avatar_id) {
        this.avatar_id = avatar_id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public List<Integer> getRole_ids() {
        return role_ids;
    }

    public void setRole_ids(List<Integer> role_ids) {
        this.role_ids = role_ids;
    }

    public List<Integer> getDepartment_ids() {
        return department_ids;
    }

    public void setDepartment_ids(List<Integer> department_ids) {
        this.department_ids = department_ids;
    }
}
