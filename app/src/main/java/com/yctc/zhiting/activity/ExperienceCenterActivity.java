package com.yctc.zhiting.activity;



import com.app.main.framework.baseview.BaseActivity;
import com.yctc.zhiting.R;

/**
 * 体验中心
 */
public class ExperienceCenterActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_experience_center;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.experience_center));
    }
}