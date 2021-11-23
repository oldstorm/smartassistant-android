package com.yctc.zhiting.entity;

public class GenerateCodeJson {
    private String qr_code;
    private String url;
    private long area_id;//家庭id
    private String area_name;
    private String saToken;
    private String saId;

    public GenerateCodeJson(String qr_code, String url, long area_id, String area_name) {
        this.qr_code = qr_code;
        this.url = url;
        this.area_id = area_id;
        this.area_name = area_name;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getArea_id() {
        return area_id;
    }

    public void setArea_id(long area_id) {
        this.area_id = area_id;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getSaToken() {
        return saToken;
    }

    public void setSaToken(String saToken) {
        this.saToken = saToken;
    }

    public String getSaId() {
        return saId;
    }

    public void setSaId(String saId) {
        this.saId = saId;
    }
}
