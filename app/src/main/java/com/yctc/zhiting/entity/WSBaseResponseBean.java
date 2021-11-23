package com.yctc.zhiting.entity;

public class WSBaseResponseBean<T> {

    /**
     * id : 11
     * type : response
     * result : null
     * success : true
     */

    private int id;
    private String type;
    private T result;
    private boolean success;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
