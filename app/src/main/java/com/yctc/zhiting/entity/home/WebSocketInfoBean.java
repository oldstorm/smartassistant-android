package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;

/**
 * 设备信息
 */
public class WebSocketInfoBean extends BaseEntity {

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
        @SerializedName("state")
        private StateBean state;

        public StateBean getState() {
            return state;
        }

        public void setState(StateBean state) {
            this.state = state;
        }

        public static class StateBean {
            @SerializedName("power")
            private String power;
            @SerializedName("brightness")
            private int brightness;
            @SerializedName("color_temp")
            private int colorTemp;
            private boolean is_online;

            public boolean isIs_online() {
                return is_online;
            }

            public void setIs_online(boolean is_online) {
                this.is_online = is_online;
            }

            public String getPower() {
                return power;
            }

            public void setPower(String power) {
                this.power = power;
            }

            public int getBrightness() {
                return brightness;
            }

            public void setBrightness(int brightness) {
                this.brightness = brightness;
            }

            public int getColorTemp() {
                return colorTemp;
            }

            public void setColorTemp(int colorTemp) {
                this.colorTemp = colorTemp;
            }
        }
    }
}
