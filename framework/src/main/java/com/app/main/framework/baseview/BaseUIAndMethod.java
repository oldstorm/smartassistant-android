package com.app.main.framework.baseview;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.app.main.framework.view.titlebar.BaseTitleBar;

public interface BaseUIAndMethod {
    //无网络弹提示
    boolean isNetworkerConnectHint();

    //获取宿主Activity
    Activity getActivity();

    //显示隐藏加载中
    void showLoadingView();

    void hideLoadingView();

    //导航栏
    BaseTitleBar getTitleBar();

    void showHint(@Nullable String hintText);
}
