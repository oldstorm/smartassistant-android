package com.yctc.zhiting.activity;


import android.view.View;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DLGuideContract;
import com.yctc.zhiting.activity.presenter.DLGuidePresenter;

import butterknife.OnClick;

/**
 *  门锁引导页
 */
public class DLGuideActivity extends MVPBaseActivity<DLGuideContract.View, DLGuidePresenter> implements DLGuideContract.View {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dl_guide;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.home_device_name));
    }

    @OnClick({R.id.tvAdd})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvAdd:
                switchToActivity(DisposablePwdActivity.class);
                break;
        }
    }
}