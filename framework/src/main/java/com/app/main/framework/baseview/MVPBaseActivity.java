package com.app.main.framework.baseview;

import android.os.Bundle;

import com.app.main.framework.baseutil.LogUtil;

import java.lang.reflect.ParameterizedType;

public abstract class MVPBaseActivity<V extends BaseView,T extends BasePresenterImpl<V>> extends BaseActivity {
    public T mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPresenter = getInstance(this,1);
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
            mPresenter.init();
        }
        super.onCreate(savedInstanceState);
        LogUtil.e("BaseActivity父类=======");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter.clear();
        }
    }

    private <T> T getInstance(Object o, int i){
        try {
            return ((Class<T>)((ParameterizedType)(o.getClass().getGenericSuperclass())).getActualTypeArguments()[i]).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
