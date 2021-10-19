package com.yctc.zhiting.entity.scene;

import java.io.Serializable;

public class SceneConditionAttrEntity implements Serializable {

    private Integer id;
    private String attribute; // 开关:power(val 是String类型); 色温：color_temp(val 是Integer类型)；亮度:brightness(val 是Integer类型)
    private Object val;
    private String val_type;  // bool,int,string,float64
    private Integer instance_id;
    private Integer min; // val_type为数字是表示该值最小值
    private Integer max; // val_type为数字是表示该值最大值

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public String getVal_type() {
        return val_type;
    }

    public void setVal_type(String val_type) {
        this.val_type = val_type;
    }

    public Integer getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(Integer instance_id) {
        this.instance_id = instance_id;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }
}
