package com.yctc.zhiting.entity.mine;

import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.List;

public enum FeedbackTypeBean {
    MEET_PROBLEM(1, UiUtil.getString(R.string.mine_meet_problem), true),
    ADVICE_SUGGEST(2, UiUtil.getString(R.string.mine_advise_suggest), false),
    ;

    private int type;
    private String name;
    private boolean  selected;

    FeedbackTypeBean(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    FeedbackTypeBean(int type, String name, boolean selected) {
        this.type = type;
        this.name = name;
        this.selected = selected;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public static List<FeedbackTypeBean> getData() {
        List<FeedbackTypeBean> data = new ArrayList<>();
        data.add(MEET_PROBLEM);
        data.add(ADVICE_SUGGEST);
        for (FeedbackTypeBean feedbackTypeBean : data) {
            feedbackTypeBean.setSelected(false);
        }
        data.get(0).setSelected(true);
        return data;
    }
}
