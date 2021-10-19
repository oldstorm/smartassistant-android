package com.yctc.zhiting.entity.mine;

import java.util.List;

/**
 * 房间区域详情
 */
public class RADetailBean {


    /**
     * name : 角维入
     * devices : [{"id":5844,"logo_url":"http://dummyimage.com/'200x200'/79a3f2')","name":"务基上"},{"id":780,"logo_url":"http://dummyimage.com/'200x200'/c6f279')","name":"管育铁族带极"},{"id":1124,"logo_url":"http://dummyimage.com/'200x200'/f279ea')","name":"工政每"},{"id":5525,"logo_url":"http://dummyimage.com/'200x200'/79f2d7')","name":"真在家育易能活何"},{"id":347,"logo_url":"http://dummyimage.com/'200x200'/f2b379')","name":"建听活西农"}]
     */

    private String name;
    private List<DevicesBean> devices;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DevicesBean> getDevices() {
        return devices;
    }

    public void setDevices(List<DevicesBean> devices) {
        this.devices = devices;
    }


}
