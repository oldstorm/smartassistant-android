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
    private List<Integer> role_ids;

    public UpdateUserPost() {
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

    public List<Integer> getRole_ids() {
        return role_ids;
    }

    public void setRole_ids(List<Integer> role_ids) {
        this.role_ids = role_ids;
    }
}
