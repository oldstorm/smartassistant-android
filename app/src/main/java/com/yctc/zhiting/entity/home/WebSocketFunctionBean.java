package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;

/**
 * 功能
 */
public class WebSocketFunctionBean extends BaseEntity {

    @SerializedName("id")
    private int id;
    @SerializedName("result")
    private ResultBean result;
    @SerializedName("success")
    private boolean success;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class ResultBean {
        @SerializedName("actions")
        private ActionsBean actions;

        public ActionsBean getActions() {
            return actions;
        }

        public void setActions(ActionsBean actions) {
            this.actions = actions;
        }

        public static class ActionsBean {
            @SerializedName("set_bright")
            private SetBrightBean setBright;
            @SerializedName("set_color_temp")
            private SetBrightBean setColorTemp;
            @SerializedName("switch")
            private SetBrightBean switchX;

            public SetBrightBean getSetBright() {
                return setBright;
            }

            public void setSetBright(SetBrightBean setBright) {
                this.setBright = setBright;
            }

            public SetBrightBean getSetColorTemp() {
                return setColorTemp;
            }

            public void setSetColorTemp(SetBrightBean setColorTemp) {
                this.setColorTemp = setColorTemp;
            }

            public SetBrightBean getSwitchX() {
                return switchX;
            }

            public void setSwitchX(SetBrightBean switchX) {
                this.switchX = switchX;
            }

            public static class SetBrightBean {
                @SerializedName("cmd")
                private String cmd;
                @SerializedName("name")
                private String name;
                @SerializedName("is_permit")
                private boolean isPermit;

                public String getCmd() {
                    return cmd;
                }

                public void setCmd(String cmd) {
                    this.cmd = cmd;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public boolean isIsPermit() {
                    return isPermit;
                }

                public void setIsPermit(boolean isPermit) {
                    this.isPermit = isPermit;
                }
            }
        }
    }
}
