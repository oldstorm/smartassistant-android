package com.yctc.zhiting.widget;

import com.app.main.framework.baseutil.UiUtil;

public class Dot {
    int x;
    int y;
    float radius;
    int color;
    float alpha = 255;

    public Dot(int x, int y, float radius, int color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    /**
     * 获取改变透明度后的颜色值
     *
     * @return
     */
    public int changeAlpha() {
        return UiUtil.changeAlpha(color, (int) alpha);
    }
}
