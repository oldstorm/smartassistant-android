package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

public class ForgetPwdRequest extends Request {
    private String country_code;
    private String phone;
    private String new_password; // 新密码
    private String captcha;
    private String captcha_id;

    public ForgetPwdRequest(String country_code, String phone, String new_password, String captcha, String captcha_id) {
        this.country_code = country_code;
        this.phone = phone;
        this.new_password = new_password;
        this.captcha = captcha;
        this.captcha_id = captcha_id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
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
