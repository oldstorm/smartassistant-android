package com.yctc.zhiting.bean;

import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.List;

public enum FuncBottomBean {
    ADD_FROM_UNBIND(UiUtil.getString(R.string.home_dl_add_from_unbind)),
    ADD_FROM_DOOR_LOCK(UiUtil.getString(R.string.home_dl_add_from_door_lock)),
    ;
    private String name;

    FuncBottomBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 门锁添加密码方式
     * @return
     */
    public static List<FuncBottomBean> getDLPwdWayData() {
        List<FuncBottomBean> data = new ArrayList<>();
        data.add(ADD_FROM_UNBIND);
        data.add(ADD_FROM_DOOR_LOCK);
        return data;
    }
}
