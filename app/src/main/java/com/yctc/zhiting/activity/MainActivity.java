
package com.yctc.zhiting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.entity.HttpResult;
import com.app.main.framework.widget.CustomFragmentTabHost;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.MainContract;
import com.yctc.zhiting.activity.presenter.MainPresenter;
import com.yctc.zhiting.application.Application;
import com.yctc.zhiting.fragment.HomeFragment;
import com.yctc.zhiting.fragment.MinFragment;
import com.yctc.zhiting.fragment.SceneFragment;
import com.yctc.zhiting.listener.IHomeView;
import com.yctc.zhiting.listener.IMinView;
import com.yctc.zhiting.listener.ISceneView;
import com.yctc.zhiting.utils.FastUtil;
import com.yctc.zhiting.utils.IntentConstant;

/**
 * App主页 dev1.3.0
 */
public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View {

    private CustomFragmentTabHost mTabHost;
    private LayoutInflater layoutInflater;
    private final int DEFAULT_TAB = 0;

    private String[] mTextViewArray = new String[]{UiUtil.getString(R.string.main_home), UiUtil.getString(R.string.main_scene), UiUtil.getString(R.string.main_mine)};
    private Class[] fragmentArray = new Class[]{HomeFragment.class, SceneFragment.class, MinFragment.class};
    private int[] drawableArray = new int[]{R.drawable.tab_home, R.drawable.tab_scene, R.drawable.tab_mine};
    private View[] indexView = new View[fragmentArray.length];
    private IMinView mIMinView;
    private IHomeView mIHomeView;
    private ISceneView mISceneView;

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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        type = intent.getStringExtra(IntentConstant.TYPE);
        needPermissions = intent.getStringExtra(IntentConstant.NEED_PERMISSION);
        appName = intent.getStringExtra(IntentConstant.APP_NAME);
        initTab(this);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mTabHost.setCurrentTab(0);
    }

    /**
     * 初始化底部的导航栏
     *
     * @param activity
     */
    private void initTab(MainActivity activity) {
        mTabHost = findViewById(R.id.custom_tabhost);
        layoutInflater = LayoutInflater.from(activity);
        mTabHost.setup(activity, getSupportFragmentManager(), R.id.contentPanel);
        int fragmentCount = fragmentArray.length;
        for (int i = 0; i < fragmentCount; ++i) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().setDividerDrawable(null);
        }

        mTabHost.setOnTabChangedListener(tabId -> {
            if (mTabHost.getCurrentTab() == 0) {
                if (mIHomeView == null) mIHomeView = (IHomeView) mTabHost.getCurrentFragment();
                mIHomeView.selectTab();
            } else if (mTabHost.getCurrentTab() == 1) {
                if (mISceneView == null) mISceneView = (ISceneView) mTabHost.getCurrentFragment();
                mISceneView.selectTab();
            } else if (mTabHost.getCurrentTab() == 2) {//我的
                if (mIMinView == null) mIMinView = (IMinView) mTabHost.getCurrentFragment();
                mIMinView.selectTab();
            }
            LogUtil.e("mTabHost==" + mTabHost.getCurrentTab());
        });
    }

    /**
     * 返回你对应的view
     *
     * @param index
     * @return
     */
    private View getTabItemView(int index) {
        indexView[index] = layoutInflater.inflate(R.layout.home_tab, null);
        TextView title = indexView[index].findViewById(R.id.title);
        ImageView imageView = indexView[index].findViewById(R.id.tab_image);
        title.setText(mTextViewArray[index]);
        imageView.setImageResource(drawableArray[index]);
        return indexView[index];
    }

    @Override
    public void postOrderCheckSuccess(HttpResult msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Application.releaseResource();
    }

    /**
     * 到授权登录界面
     */
    public void toAuth() {
        if (!TextUtils.isEmpty(type) && type.equals("1")) {
            Bundle bundle = new Bundle();
            bundle.putString(IntentConstant.NEED_PERMISSION, needPermissions);
            bundle.putString(IntentConstant.APP_NAME, appName);
            switchToActivity(AuthorizeActivity.class, bundle);
        }
    }

    @Override
    public void onBackPressed() {
        if (mTabHost.getCurrentTab() == DEFAULT_TAB) {
            if (FastUtil.isDoubleClick()){
                super.onBackPressed();
            }else{
                ToastUtil.show(UiUtil.getString(R.string.main_exit_tip));
            }
        } else {
            mTabHost.setCurrentTab(DEFAULT_TAB);
        }
    }
}
