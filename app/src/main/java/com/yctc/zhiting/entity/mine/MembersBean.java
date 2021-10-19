package com.yctc.zhiting.entity.mine;

import java.util.List;

public class MembersBean {


    /**
     * users : [{"nickname":"题属图","user_id":865,"role_name":"热儿"},{"nickname":"车重建严题月导况","user_id":8675,"role_name":"导龙认种平低开因"},{"nickname":"还参府道色人八","user_id":2306,"role_name":"酸资政"},{"nickname":"名音社具她还广","user_id":5461,"role_name":"增花"},{"nickname":"带运两历传","user_id":8734,"role_name":"名铁参也又"}]
     * is_creator : false
     * role_count : 35
     * self_id : 438
     */

    private boolean is_creator;
    private boolean is_owner;
    private int role_count;
    private int self_id;
    private List<UserBean> users;

    public boolean isIs_creator() {
        return is_creator;
    }

    public void setIs_creator(boolean is_creator) {
        this.is_creator = is_creator;
    }

    public boolean isIs_owner() {
        return is_owner;
    }

    public void setIs_owner(boolean is_owner) {
        this.is_owner = is_owner;
    }

    public int getRole_count() {
        return role_count;
    }

    public void setRole_count(int role_count) {
        this.role_count = role_count;
    }

    public int getSelf_id() {
        return self_id;
    }

    public void setSelf_id(int self_id) {
        this.self_id = self_id;
    }

    public List<UserBean> getUsers() {
        return users;
    }

    public void setUsers(List<UserBean> users) {
        this.users = users;
    }
}
