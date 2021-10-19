package com.app.main.framework.view.titlebar;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

//接口说明：基础的导航栏接口，实例化后必须实现的方法（好处：方便管理，统一定义方法，可以实例化多种不同风格导航栏）
public interface BaseTitleBar {
    View getView();

    String getTitleStr();
    /*设置整体背景色*/
    BaseTitleBar setBgColor(int color);

    /*标题栏相关*/
    BaseTitleBar setTitle(@StringRes int StringResId);

    BaseTitleBar setTitle(String text);

    BaseTitleBar setTitleIcon(@DrawableRes int drawableId);

    BaseTitleBar setTitleTextColor(int color);

    BaseTitleBar showTitleBarMoreView();

    BaseTitleBar showTitleBarMoreText();

    BaseTitleBar setTitleBarMoreText(String text);

    BaseTitleBar setTitleBarMoreText(@StringRes int StringResId);

    BaseTitleBar setTitleBarMoreTextColor(@ColorRes int color);

    BaseTitleBar hideTitleBarMoreView();

    BaseTitleBar setTitleBarMoreClickListener(View.OnClickListener listener);

    BaseTitleBar setTitleBarMoreTextClickListener(View.OnClickListener listener);

    BaseTitleBar showTitleBarLeftView();

    BaseTitleBar hideTitleBarLeftVeiw();

    BaseTitleBar setTitleBarLeftText(String text);

    BaseTitleBar setTitleBarMoreIcon(Drawable drawable);

    BaseTitleBar setTitleBarLeftClickListener(View.OnClickListener listener);

    BaseTitleBar setTitleBarTextLeftClickListener(View.OnClickListener listener);

    BaseTitleBar setTitleBarMoreEnable(boolean enable);

    public TextView getTvTitleBarMoreText();

    int getId();
}
