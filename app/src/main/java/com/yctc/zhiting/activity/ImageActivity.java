package com.yctc.zhiting.activity;



import android.content.Intent;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.app.main.framework.imageutil.GlideUtil;
import com.yctc.zhiting.R;
import com.yctc.zhiting.utils.IntentConstant;

import butterknife.BindView;

public class ImageActivity extends BaseActivity {

    @BindView(R.id.ivPic)
    ImageView ivPic;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }


    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        String url = intent.getStringExtra(IntentConstant.URL);
        GlideUtil.load(url).into(ivPic);
    }
}