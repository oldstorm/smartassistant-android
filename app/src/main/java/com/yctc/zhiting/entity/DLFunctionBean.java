package com.yctc.zhiting.entity;

import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.List;

public enum DLFunctionBean {
    DISPOSAL_PWD(UiUtil.getString(R.string.home_disposable_pwd)),
    USER_MANAGE(UiUtil.getString(R.string.home_user_manage)),
    DL_SETTING(UiUtil.getString(R.string.home_dl_setting)),
    ;

    private String name;

    DLFunctionBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<DLFunctionBean> getDLFunctionData() {
        List<DLFunctionBean> data = new ArrayList<>();
        data.add(DISPOSAL_PWD);
        data.add(USER_MANAGE);
        data.add(DL_SETTING);
        return data;
    }
}
