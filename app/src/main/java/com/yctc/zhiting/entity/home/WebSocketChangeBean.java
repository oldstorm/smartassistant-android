package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;

/**
 * 设备状态变更
 */
public class WebSocketChangeBean extends BaseEntity {

    @SerializedName("event_type")
    private String eventType;
    @SerializedName("data")
    private DataBean data;
    @SerializedName("origin")
    private String origin;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public static class DataBean {
        @SerializedName("device_id")
        private int deviceId;
        @SerializedName("state")
        private StateBean state;

        public int getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public StateBean getState() {
            return state;
        }

        public void setState(StateBean state) {
            this.state = state;
        }

        public static class StateBean {
            @SerializedName("is_online")
            private boolean isOnline;
            @SerializedName("power")
            private String power;
            @SerializedName("brightness")
            private int brightness;
            @SerializedName("color_temp")
            private int colorTemp;

            public boolean isIsOnline() {
                return isOnline;
            }

            public void setIsOnline(boolean isOnline) {
                this.isOnline = isOnline;
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
