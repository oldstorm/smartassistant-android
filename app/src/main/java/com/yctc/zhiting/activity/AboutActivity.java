package com.yctc.zhiting.activity;


import android.widget.TextView;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AboutContract;
import com.yctc.zhiting.activity.presenter.AboutPresenter;

import butterknife.BindView;

/**
 * 关于
 */
public class AboutActivity extends MVPBaseActivity<AboutContract.View, AboutPresenter> implements AboutContract.View {

    @BindView(R.id.tvVersion)
    TextView tvVersion;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.about));
        tvVersion.setText(UiUtil.getString(R.string.version_code)+AndroidUtil.getAppVersion());
    }
}