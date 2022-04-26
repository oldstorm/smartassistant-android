package com.yctc.zhiting.entity.mine;

import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;

import java.util.ArrayList;
import java.util.List;

public enum FeedbackCategoryBean {
    PROBLEM_APP(1, UiUtil.getString(R.string.mine_app_problem), true),
    PROBLEM_REGISTER_LOGIN(2, UiUtil.getString(R.string.mine_register_login_problem), false),
    PROBLEM_USER_DATA(3, UiUtil.getString(R.string.mine_user_data_problem), false),
    PROBLEM_DEVICE(4, UiUtil.getString(R.string.mine_device_problem), false),
    PROBLEM_SCENE(5, UiUtil.getString(R.string.mine_scene_problem), false),
    PROBLEM_OTHER(6, UiUtil.getString(R.string.mine_other_problem), false),

    ADVICE_SUGGEST_APP(7, UiUtil.getString(R.string.mine_app_function_suggest), true),
    ADVICE_SUGGEST_DEVICE(8, UiUtil.getString(R.string.mine_device_function_suggest), false),
    ADVICE_SUGGEST_SCENE(9, UiUtil.getString(R.string.mine_scene_function_suggest), false),
    ADVICE_SUGGEST_OTHER(10, UiUtil.getString(R.string.mine_other_function_suggest), false),
    ;

    private int type;
    private String name;
    private boolean  selected;

    FeedbackCategoryBean(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    FeedbackCategoryBean(int type, String name, boolean selected) {
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

    public static List<FeedbackCategoryBean> getProblemData() {
        List<FeedbackCategoryBean> problemData = new ArrayList<>();
        problemData.add(PROBLEM_APP);
        problemData.add(PROBLEM_REGISTER_LOGIN);
        problemData.add(PROBLEM_USER_DATA);
        problemData.add(PROBLEM_DEVICE);
        problemData.add(PROBLEM_SCENE);
        problemData.add(PROBLEM_OTHER);
        for (FeedbackCategoryBean feedbackCategoryBean : problemData) {
            feedbackCategoryBean.setSelected(false);
        }
        return problemData;
    }

    public static List<FeedbackCategoryBean> getSuggestData() {
        List<FeedbackCategoryBean> suggestData = new ArrayList<>();
        suggestData.add(ADVICE_SUGGEST_APP);
        suggestData.add(ADVICE_SUGGEST_DEVICE);
        suggestData.add(ADVICE_SUGGEST_SCENE);
        suggestData.add(ADVICE_SUGGEST_OTHER);
        for (FeedbackCategoryBean feedbackCategoryBean : suggestData) {
            feedbackCategoryBean.setSelected(false);
        }
        return suggestData;
    }
}
