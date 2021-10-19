package com.yctc.zhiting.entity.home;

import com.app.main.framework.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;


/**
 * 发现设备
 */
public class ScanResultBean extends BaseEntity {

    @SerializedName("id")
    private long id;
    @SerializedName("result")
    private ResultBean result;
    @SerializedName("success")
    private boolean success;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        @SerializedName("device")
        private DeviceBean device;

        public DeviceBean getDevice() {
            return device;
        }

        public void setDevice(DeviceBean device) {
            this.device = device;
        }
    }
}
