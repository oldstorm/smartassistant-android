package com.yctc.zhiting.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.app.main.framework.CommonFragmentPagerAdapter;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.BaseFragment;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.google.android.material.tabs.TabLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SupportBrand2Contract;
import com.yctc.zhiting.activity.presenter.SupportBrand2Presenter;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.fragment.SBCreateFragment;
import com.yctc.zhiting.fragment.SBSystemFragment;
import com.yctc.zhiting.utils.IntentConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 新的只支持品牌
 */
public class SupportBrand2Activity extends MVPBaseActivity<SupportBrand2Contract.View, SupportBrand2Presenter> implements SupportBrand2Contract.View {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.vpContent)
    ViewPager vpContent;

    private List<BaseFragment> fragments = new ArrayList<>();
    private int currentItem;

    private final int REQUEST_CODE = 100;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_support_brand2;
    }

    @Override
    protected void initUI() {
        super.initUI();
        initTabLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if (currentItem == 0){
                ((SBSystemFragment)fragments.get(0)).getData(true);
            }else {
                ((SBCreateFragment)fragments.get(1)).getData(true);
            }
        }
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    private void initTabLayout(){
        List<String> titles = Arrays.asList(getResources().getStringArray(R.array.support_brand_title));
        fragments.add(SBSystemFragment.getInstance());
        fragments.add(SBCreateFragment.getInstance());
        CommonFragmentPagerAdapter commonFragmentPagerAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles);
        vpContent.setOffscreenPageLimit(titles.size());
        vpContent.setAdapter(commonFragmentPagerAdapter);
        tabLayout.setupWithViewPager(vpContent, false);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setSelectTab(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setSelectTab(tab.getPosition(), false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setCustomTabIcons(titles);
        setSelectTab(0, true);
    }

    /**
     * @param position
     * @param select
     */
    private void setSelectTab(int position, boolean select) {
        if (tabLayout != null && tabLayout.getTabCount() > 0) {
            TabLayout.Tab tab = tabLayout.getTabAt(position);
            if (tab!=null) {
                RelativeLayout view = (RelativeLayout) tab.getCustomView();
                if (view != null) {
                    TextView tvText = view.findViewById(R.id.tvText);
                    View indicator = view.findViewById(R.id.indicator);
                    if (select) {
                        tabLayout.getTabAt(position).select();
                        currentItem = position;
                        indicator.setVisibility(View.VISIBLE);
                        tvText.setTextColor(UiUtil.getColor(R.color.appPurple));
                    } else {
                        indicator.setVisibility(View.INVISIBLE);
                        tvText.setTextColor(UiUtil.getColor(R.color.color_94a5be));
                    }
                }
            }
        }
    }

    /**
     * 自定义TabLayout 布局样式
     *
     * @param data
     */
    private void setCustomTabIcons(List<String> data) {
        for (int i = 0; i < data.size(); i++) {
            RelativeLayout view = (RelativeLayout) UiUtil.inflate(R.layout.item_tablayout);
            TextView tvText = view.findViewById(R.id.tvText);
            tvText.setText(data.get(i));
            tabLayout.getTabAt(i).setCustomView(view);
        }
    }

    @OnClick({R.id.ivBack, R.id.ivSearch})
    void onClick(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.ivBack:
                finish();
                break;

            case R.id.ivSearch:
                Bundle bundle = new Bundle();
                bundle.putInt(IntentConstant.TYPE, currentItem);
                switchToActivityForResult(SBSearchActivity.class, bundle, REQUEST_CODE);
                break;
        }
    }
}