package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseview.BaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.utils.IntentConstant;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 如何创建场景
 */
public class HowCreateSceneActivity extends BaseActivity {

    @BindView(R.id.tvCreate)
    TextView tvCreate;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_how_create_scene;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.scene_how_to_create));
    }

    @Override
    protected void initData() {
        super.initData();
        boolean hasAddP = getIntent().getBooleanExtra(IntentConstant.HOW_CREATE, false);
        tvCreate.setVisibility( hasAddP ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.tvCreate)
    void onClickCreate(){
        switchToActivity(CreateSceneActivity.class);
    }
}