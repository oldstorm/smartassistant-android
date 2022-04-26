package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class UnregisterUserRequest extends Request {

    private String captcha; // 验证码
    private String captcha_id; // 验证码id

    public UnregisterUserRequest(String captcha, String captcha_id) {
        this.captcha = captcha;
        this.captcha_id = captcha_id;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptcha_id() {
        return captcha_id;
    }

    public void setCaptcha_id(String captcha_id) {
        this.captcha_id = captcha_id;
    }
}
