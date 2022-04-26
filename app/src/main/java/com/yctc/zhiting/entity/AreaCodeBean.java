package com.yctc.zhiting.entity;

public class AreaCodeBean {
    /**
     * cn : 中国
     * en : China
     * code : 86
     */

    private String cn;
    private String en;
    private String code;
    private boolean selected;

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
