package com.yctc.zhiting.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.BaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.widget.NoScrollViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GuideActivity extends BaseActivity {

    private NoScrollViewPager vpGuide;

    private List<View> guideViews = new ArrayList<>();
    private int[] imgArr = {R.drawable.img_guide_one, R.drawable.img_guide_two, R.drawable.img_guide_three, R.drawable.img_guide_four};
    private int[] nextArr = {R.string.guide_next_1, R.string.guide_next_2, R.string.guide_next_3, R.string.guide_next_4};
    private int mCurrentItem=0;


    /**
     * 1 授权登录
     */
    private String type;
    /**
     * 第三方应用需要的权限
     */
    private String needPermissions;
    /**
     * 第三方app的名称
     */
    private String appName;

    private List<HomeCompanyBean> mHomeList;

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        type = intent.getStringExtra(IntentConstant.TYPE);
        needPermissions = intent.getStringExtra(IntentConstant.NEED_PERMISSION);
        appName = intent.getStringExtra(IntentConstant.APP_NAME);
        mHomeList = (List<HomeCompanyBean>) intent.getSerializableExtra(IntentConstant.BEAN_LIST);
    }

    @Override
    protected boolean isSetStateBar() {
        return false;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    public void onBackPressed() {
        if (mCurrentItem>0){
            mCurrentItem-=1;
            vpGuide.setCurrentItem(mCurrentItem);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void initUI() {
        vpGuide = findViewById(R.id.vpGuide);
        for (int i=0; i<imgArr.length; i++) {
            View view = View.inflate(this, R.layout.view_guide, null);
            ImageView imgGuide = view.findViewById(R.id.imgGuide);
            RelativeLayout.LayoutParams rlParam = (RelativeLayout.LayoutParams) imgGuide.getLayoutParams();
            rlParam.height = (int) (UiUtil.getScreenHeight()/1.4);
            rlParam.bottomMargin = (int) (UiUtil.getScreenHeight()/20.8);
            imgGuide.setLayoutParams(rlParam);
            imgGuide.setImageResource(imgArr[i]);
            TextView tvNext = view.findViewById(R.id.tvNext);
            RelativeLayout.LayoutParams tvNextParam = (RelativeLayout.LayoutParams) tvNext.getLayoutParams();
            tvNextParam.width = (int) (UiUtil.getScreenWidth()/1.8);
            tvNextParam.bottomMargin = (int) (UiUtil.getScreenHeight()/10.1);
            tvNext.setLayoutParams(tvNextParam);
            tvNext.setText(nextArr[i]);
            int finalI = i;
            tvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nextListener(finalI);
                }
            });
            guideViews.add(view);
        }
        vpGuide.setAdapter(new GuidePageAdapter());
        vpGuide.setCurrentItem(0);

    }

    private void nextListener(int position) {
        if (position<3) {
            mCurrentItem = position+1;
            vpGuide.setCurrentItem(mCurrentItem);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(IntentConstant.TYPE, type);
            bundle.putString(IntentConstant.NEED_PERMISSION, needPermissions);
            bundle.putString(IntentConstant.APP_NAME, appName);
            bundle.putSerializable(IntentConstant.BEAN_LIST, (Serializable) mHomeList);
            // 如果type不为空且为1的情况下到授权界面，否则直接到主界面
            switchToActivity(type != null && type.equals("1") ? AuthorizeActivity.class : MainActivity.class, bundle);
            SpUtil.put(Constant.GUIDED, true);
            finish();
        }
    }

    class GuidePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return guideViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(guideViews.get(position));
            return guideViews.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}