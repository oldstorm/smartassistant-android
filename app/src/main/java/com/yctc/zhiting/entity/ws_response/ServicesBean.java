package com.yctc.zhiting.entity.ws_response;

import java.io.Serializable;
import java.util.List;

public class ServicesBean implements Serializable {

    private String  type;
    private List<AttributesBean> attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AttributesBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributesBean> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "ServicesBean{" +
                "type='" + type + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
