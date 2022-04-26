package com.yctc.zhiting.utils;

import java.util.Arrays;
import java.util.List;

public class NeedDealAttrConstant {
    // 创建场景时设备状态变化时需要显示的属性
    public static final String[] conditionDealAttrs = {"on_off", "power", "powers_1", "powers_2", "powers_3", "brightness",
            "color_temp", "rgb", "humidity", "temperature", "detected", "window_door_close", "leak_detected",
            "target_state", "target_position", "switch_event", "motion_detected", "contact_sensor_state"};

    // 创建场景时控制设备需要显示的属性
    public static final String[] taskDelaAttrs = {"on_off", "power", "powers_1", "powers_2", "powers_3", "brightness",
            "color_temp", "rgb", "target_state", "target_position"};

    /**
     * 是否在设备状态变化时需要显示的属性
     *
     * @param attr
     * @return
     */
    public static boolean isContainConditionDealAttrs(String attr) {
        List<String> conditionAttrList = Arrays.asList(conditionDealAttrs.clone());
        return conditionAttrList.contains(attr);
    }

    /**
     * 是否在控制设备需要显示的属性
     *
     * @param attr
     * @return
     */
    public static boolean inContainTaskDelaAttrs(String attr) {
        List<String> taskAttrList = Arrays.asList(taskDelaAttrs.clone());
        return taskAttrList.contains(attr);
    }
}
