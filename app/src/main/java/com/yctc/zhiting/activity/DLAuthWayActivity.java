package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.main.framework.CommonFragmentPagerAdapter;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.BaseFragment;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.google.android.material.tabs.TabLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DLAuthWayContract;
import com.yctc.zhiting.activity.presenter.DLAuthWayPresenter;
import com.yctc.zhiting.adapter.TabAdapter;
import com.yctc.zhiting.dialog.TipDialog;
import com.yctc.zhiting.entity.TabBean;
import com.yctc.zhiting.fragment.DLAuthWayFragment;
import com.yctc.zhiting.fragment.SBCreateFragment;
import com.yctc.zhiting.fragment.SBSystemFragment;
import com.yctc.zhiting.widget.NoScrollViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 门锁验证方式
 */
public class DLAuthWayActivity extends MVPBaseActivity<DLAuthWayContract.View, DLAuthWayPresenter> implements DLAuthWayContract.View {

    @BindView(R.id.vpContent)
    NoScrollViewPager vpContent;
    @BindView(R.id.llBottom)
    LinearLayout llBottom;
    @BindView(R.id.tvBulk)
    TextView tvBulk;
    @BindView(R.id.rvTab)
    RecyclerView rvTab;

    private List<BaseFragment> fragments = new ArrayList<>();
    private int currentItem;
    private boolean isBulk;

    private TabAdapter mTabAdapter; // tab适配器
    private TipDialog mTipDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dlauth_way;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.home_dl_auth_way));
        initTabLayout();
    }

    private void initTabLayout() {
        List<String> titles = Arrays.asList(getResources().getStringArray(R.array.dl_auth_way_title));
        for (int i = 0; i < titles.size(); i++) {
            fragments.add(DLAuthWayFragment.getInstance(i));
        }
        vpContent.setScroll(true);
        CommonFragmentPagerAdapter commonFragmentPagerAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles);
        vpContent.setOffscreenPageLimit(titles.size());
        vpContent.setAdapter(commonFragmentPagerAdapter);
        rvTab.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTabAdapter = new TabAdapter();
        TabBean.getDLAuthWayData().get(0).setSelected(true);
        mTabAdapter.setNewData(TabBean.getDLAuthWayData());
        rvTab.setAdapter(mTabAdapter);
        mTabAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (isBulk) return;
            setTabStatus(position);
            vpContent.setCurrentItem(position);
        });
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        setTabStatus(0);
        super.onDestroy();
    }

    /**
     * 设置tab的状态
     *
     * @param position
     */
    private void setTabStatus(int position) {
        TabBean tabBean = mTabAdapter.getItem(position);
        if (tabBean == null) return;
        if (tabBean.isSelected()) return;
        for (TabBean tb : mTabAdapter.getData()) {
            tb.setSelected(false);
        }
        tabBean.setSelected(true);
        mTabAdapter.notifyDataSetChanged();
    }



    /**
     * 设置是否批量转
     */
    private void setBulkStatus() {
        if (isBulk) {
            vpContent.setScroll(false);
            llBottom.setVisibility(View.VISIBLE);
            tvBulk.setVisibility(View.INVISIBLE);
            tvBulk.setEnabled(false);
        } else {
            vpContent.setScroll(true);
            llBottom.setVisibility(View.GONE);
            tvBulk.setVisibility(View.VISIBLE);
            tvBulk.setEnabled(true);
        }
    }

    @OnClick({R.id.tvBulk, R.id.tvCancel, R.id.tvBind})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvBulk:
                isBulk = true;
//                setBulkStatus();
                showTipDialog();
                break;

            case R.id.tvCancel:
                isBulk = false;
                setBulkStatus();
                break;

            case R.id.tvBind:

                break;
        }
    }

    /**
     *  提示窗
     */
    private void showTipDialog() {
        if (mTipDialog == null) {
            mTipDialog = TipDialog.getInstance(UiUtil.getString(R.string.home_dl_no_user), UiUtil.getString(R.string.cancel), UiUtil.getString(R.string.home_dl_add_now));
            mTipDialog.setOperateListener(new TipDialog.OnOperateListener() {
                @Override
                public void onClickLeft() {
                    mTipDialog.dismiss();
                }

                @Override
                public void onClickRight() {

                }
            });
        }
        if (mTipDialog!=null && !mTipDialog.isShowing()) {
            mTipDialog.show(this);
        }
    }

}