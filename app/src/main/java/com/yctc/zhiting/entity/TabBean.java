package com.yctc.zhiting.entity;

import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.List;

public enum TabBean {
    TAB_MANUAL(UiUtil.getString(R.string.scene_manual)),
    TAB_AUTOMATIC(UiUtil.getString(R.string.scene_automatic)),
    TAB_DL_MANAGER(UiUtil.getString(R.string.home_dl_manager)),
    TAB_DL_USER(UiUtil.getString(R.string.home_dl_user)),
    TAB_DL_VISITOR(UiUtil.getString(R.string.home_dl_visitor)),
    TAB_DL_FORCED_USER(UiUtil.getString(R.string.home_dl_forced_user))
    ;
    private String name;
    private boolean selected;


    TabBean() {
    }

    TabBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static List<TabBean> getDLAuthWayData() {
        List<TabBean> data = new ArrayList<>();
        data.add(TAB_DL_MANAGER);
        data.add(TAB_DL_USER);
        data.add(TAB_DL_VISITOR);
        data.add(TAB_DL_FORCED_USER);
        return data;
    }
}
