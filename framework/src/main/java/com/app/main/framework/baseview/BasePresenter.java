package com.app.main.framework.baseview;

public interface BasePresenter<V extends BaseView> {
    void attachView(V view);

    void detachView();
}
