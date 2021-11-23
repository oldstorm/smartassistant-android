package com.yctc.zhiting.activity;



import com.app.main.framework.baseview.BaseActivity;
import com.yctc.zhiting.R;

public class FindSAGuideActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_saguide;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.home_how_to_find_user_sa_token));
    }
}