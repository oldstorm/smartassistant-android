package com.yctc.zhiting.entity;

import androidx.annotation.DrawableRes;

import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.List;

public enum ACProductBean {
    ZHITING(R.drawable.icon_zhiting_circle_logo, UiUtil.getString(R.string.mine_zhiting_family_cloud)),
    YUNPAN(R.drawable.icon_yunpan_circle_logo, UiUtil.getString(R.string.mine_zhiting_yunpan))
    ;

    private @DrawableRes int logo;
    private String name;

    ACProductBean(int logo, String name) {
        this.logo = logo;
        this.name = name;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<ACProductBean> getData(){
        List<ACProductBean> data = new ArrayList<>();
        data.add(ZHITING);
        data.add(YUNPAN);
        return data;
    }
}
