package com.yctc.zhiting.entity.mine;

public class RegisterPost{

    private String country_code;
    private String phone;
    private String password;
    private String captcha;
    private String captcha_id;
    private int login_type;

    public int getLogin_type() {
        return login_type;
    }

    public void setLogin_type(int login_type) {
        this.login_type = login_type;
    }

    public RegisterPost(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public RegisterPost(String country_code, String phone, String password, String captcha, String captcha_id) {
        this.country_code = country_code;
        this.phone = phone;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
