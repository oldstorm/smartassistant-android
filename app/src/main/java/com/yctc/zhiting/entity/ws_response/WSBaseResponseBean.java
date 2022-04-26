package com.yctc.zhiting.entity.ws_response;

public class WSBaseResponseBean<T> {

    /**
     * id : 11
     * type : response
     * result : null
     * success : true
     */

    private long id;
    private String type;
    private String event_type;
    private String domain;
    private T result;
    private T data;
    private boolean success;
    private ErrorBean error;

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public class ErrorBean {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Override
    public String toString() {
        return "WSBaseResponseBean{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", event_type='" + event_type + '\'' +
                ", domain='" + domain + '\'' +
                ", result=" + result +
                ", data=" + data +
                ", success=" + success +
                ", error=" + error +
                '}';
    }
}
