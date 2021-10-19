package com.yctc.zhiting.entity.scene;

import androidx.annotation.DrawableRes;

public class ConditionSelectBean {

    private @DrawableRes int drawable;
    private String title;
    private String subtitle;
    private boolean enabled;

    public ConditionSelectBean(int drawable, String title, String subtitle) {
        this.drawable = drawable;
        this.title = title;
        this.subtitle = subtitle;
    }

    public ConditionSelectBean(int drawable, String title, String subtitle, boolean enabled) {
        this.drawable = drawable;
        this.title = title;
        this.subtitle = subtitle;
        this.enabled = enabled;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
