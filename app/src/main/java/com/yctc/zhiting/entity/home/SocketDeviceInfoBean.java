package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 初始化状态
 */
public class SocketDeviceInfoBean extends BaseEntity {

    private Integer id;
    private String type;
    private ResultBean result;
    private Boolean success;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public Boolean isSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public static class ResultBean implements Serializable {
        private DeviceBean device;

        public DeviceBean getDevice() {
            return device;
        }

        public void setDevice(DeviceBean device) {
            this.device = device;
        }

        public static class DeviceBean implements Serializable {
            private String identity;
            private String type;
            private List<InstancesBean> instances;

            public String getIdentity() {
                return identity;
            }

            public void setIdentity(String identity) {
                this.identity = identity;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<InstancesBean> getInstances() {
                return instances;
            }

            public void setInstances(List<InstancesBean> instances) {
                this.instances = instances;
            }

            public static class InstancesBean implements Serializable {
                private String type;
                private Integer instance_id;
                private List<AttributesBean> attributes;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public Integer getInstance_id() {
                    return instance_id;
                }

                public void setInstance_id(Integer instance_id) {
                    this.instance_id = instance_id;
                }

                public List<AttributesBean> getAttributes() {
                    return attributes;
                }

                public void setAttributes(List<AttributesBean> attributes) {
                    this.attributes = attributes;
                }

                public static class AttributesBean implements Serializable{
                    private Integer id;
                    private String attribute;
                    private Object val;
                    private String val_type;
                    private Boolean can_control;
                    private Integer min;
                    private Integer max;

                    public Integer getId() {
                        return id;
                    }

                    public void setId(Integer id) {
                        this.id = id;
                    }

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

                    public Boolean isCan_control() {
                        return can_control;
                    }

                    public void setCan_control(Boolean can_control) {
                        this.can_control = can_control;
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
            }
        }
    }
}
