package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;

/**
 * 开关状态
 */
public class SocketSwitchBean extends BaseEntity {

    private String event_type;
    private DataBean data;

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public class DataBean {
        private AttrBean attr;
        private String identity;
        private Integer instance_id;

        public AttrBean getAttr() {
            return attr;
        }

        public void setAttr(AttrBean attr) {
            this.attr = attr;
        }

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }

        public Integer getInstance_id() {
            return instance_id;
        }

        public void setInstance_id(Integer instance_id) {
            this.instance_id = instance_id;
        }

        public class AttrBean {
            private Integer id;
            private String attribute;
            private String val;
            private String val_type;

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

            public String getVal() {
                return val;
            }

            public void setVal(String val) {
                this.val = val;
            }

            public String getVal_type() {
                return val_type;
            }

            public void setVal_type(String val_type) {
                this.val_type = val_type;
            }
        }
    }
}
