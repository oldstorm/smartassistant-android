package com.yctc.zhiting.entity.mine;

import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.List;

public enum ABFunctionBean {
    PWD_MODIFY(UiUtil.getString(R.string.mine_pwd_modify)),
    ACCOUNT_CANCELLATION(UiUtil.getString(R.string.mine_account_cancellation)),
    ;

    private String name;

    ABFunctionBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<ABFunctionBean> getData() {
        List<ABFunctionBean> data = new ArrayList<>();
        data.add(PWD_MODIFY);
        data.add(ACCOUNT_CANCELLATION);
        return data;
    }
}
