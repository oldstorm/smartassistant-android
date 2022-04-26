package com.yctc.zhiting.entity.ws_request;

import com.yctc.zhiting.entity.ws_response.AttributesBean;

import java.util.List;

public class AttrRequestBean {
    List<AttributesBean> attributes;

    public AttrRequestBean(List<AttributesBean> attributes) {
        this.attributes = attributes;
    }

    public List<AttributesBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributesBean> attributes) {
        this.attributes = attributes;
    }
}
