package com.app.main.framework.baseview;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.NetworkUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.disklrucache.DiskLruCacheHelper;
import com.app.main.framework.entity.AppEventEntity;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.view.titlebar.BaseTitleBar;
import com.app.main.framework.widget.ReplaceViewHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment implements BaseView {

    public String TAG = "当前组件=========" + getClass().getSimpleName() + "=======";
    public String TAG_NAME = getClass().getSimpleName() + "=======";
    public static final String SHARE_ANIM_KEY = "SHARE_ANIM_KEY";
    public View mContentView;
    public FrameLayout rootView;
    public Intent intent = new Intent();
    public Bundle bundle = new Bundle();
    public Fragment currentFragment = new Fragment();
    public List<NameValuePair> requestData = new ArrayList<>();
    public ReplaceViewHelper helper = new ReplaceViewHelper(LibLoader.getCurrentActivity());
    public DiskLruCacheHelper cacheHelper;//数据的缓存

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            //初始化布局
            rootView = new FrameLayout(getActivity());
            mContentView = inflater.inflate(getLayoutId(), null, false);
            ButterKnife.bind(this, mContentView);
            rootView.addView(mContentView, getLayoutParams());
            //初始化数据
            initUI();
            Bundle arguments = getArguments();
            if (arguments != null) {
                initArgument(arguments);
            }
            initData();
        } else {
            // 不为null时，需要把自身从父布局中移除，因为ViewPager会再次添加
            ViewParent parent = rootView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(rootView);
            }
            updateData();
        }
        if (bindEventBus()) {//注册EventBus
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
                EventBus.getDefault().register(this);
                LogUtil.e("Base=注册===EventBus2===" + TAG);
            }
        }
        LogUtil.e("当前组件======" + TAG);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            cacheHelper = new DiskLruCacheHelper(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------EventBus--------------------------------------------------

    /**
     * 是否需要绑定EventBus，默认是不需要的
     * 如果有需要绑定的界面，重写返回true即可
     *
     * @return
     */
    public boolean bindEventBus() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(String text) {
        onReceiveEvent(text);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(AppEventEntity entity) {
        onReceiveEvent(entity);
    }

    public void onReceiveEvent(String key) {

    }

    public void onReceiveEvent(AppEventEntity entity) {

    }

    //-------------------------------------------------------EventBus---------------------------------------------------
    @Override
    public boolean isNetworkerConnectHint() {
        boolean networkerConnect = NetworkUtil.isNetworkerConnect();
        if (!networkerConnect) {
            showHint(getString(R.string.network_error));
        }
        return networkerConnect;
    }

    @Override
    public BaseTitleBar getTitleBar() {
        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) {
            return baseActivity.getTitleBar();
        }
        return null;
    }

    @Override
    public void showHint(@Nullable String hintText) {

    }

    /**
     * 设置资源id
     *
     * @return 资源id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化数据，只有第一次初始化Fragment才会调用
     */
    protected void initData() {

    }

    /**
     * 初始化控件
     */
    protected void initUI() {

    }

    /**
     * 接收fragment传递过来的参数
     */
    protected void initArgument(Bundle bundle) {

    }

    /**
     * 更新数据
     */
    protected void updateData() {

    }

    private FrameLayout.LayoutParams getLayoutParams() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return lp;
    }

    /*获取容器Activity*/
    public BaseActivity getBaseActivity() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            return (BaseActivity) activity;
        }
        return null;
    }

    @Override
    public void showLoadingView() {
        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) {
            baseActivity.showLoadingView();
        }
    }

    @Override
    public void hideLoadingView() {
        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) {
            baseActivity.hideLoadingView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoadingView();
        if (bindEventBus()) {//注册EventBus
            EventBus.getDefault().unregister(this);
        }
    }

    protected FragmentTransaction switchFragment(@IdRes int layoutId, Fragment targetFragment) {
        FragmentTransaction transaction = ((AppCompatActivity) Objects.requireNonNull(LibLoader.getCurrentActivity())).getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下  
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(layoutId, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        return transaction;
    }

    //    界面之间的跳转
    public void switchToActivity(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    public void switchToActivityClear(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void switchToActivity(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        startActivity(intent);
    }

    public void switchToActivityClear(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchToActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchToActivity(Context context, Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchToActivityForResult(Context context, Class<?> clazz,
                                          Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    public void switchToActivityForResult(Class<?> clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    public void switchToActivityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivityForResult(intent, requestCode);
    }

    public void switchToActivityForResult(Context context, Class<?> clazz, int requestCode) {
        Intent intent = new Intent(context, clazz);
        startActivityForResult(intent, requestCode);
    }

    public void switchToWelcomeActivity(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        //分开写会覆盖了
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
//    public void switchToActivity(Class<?> clazz) {
//        Intent intent = new Intent(getActivity(), clazz);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//    }
//
//    public void switchToActivityClear(Class<?> clazz) {
//        Intent intent = new Intent(getActivity(), clazz);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//    }
//
//    public void switchToActivity(Context context, Class<?> clazz) {
//        Intent intent = new Intent(context, clazz);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//    }
//
//    public void switchToActivityClear(Class<?> clazz, Bundle bundle) {
//        Intent intent = new Intent(getActivity(), clazz);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtras(bundle);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//    }
//
//    public void switchToActivity(Class<?> clazz, Bundle bundle) {
//        Intent intent = new Intent(getActivity(), clazz);
//        intent.putExtras(bundle);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//    }
//
//    public void switchToActivity(Context context, Class<?> clazz, Bundle bundle) {
//        Intent intent = new Intent(context, clazz);
//        intent.putExtras(bundle);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//    }
//
//    public void switchToActivityForResult(Context context, Class<?> clazz,
//                                          Bundle bundle, int requestCode) {
//        Intent intent = new Intent(context, clazz);
//        if (bundle != null) {
//            intent.putExtras(bundle);
//        }
//        startActivityForResult(intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//    }
//
//    public void switchToActivityForResult(Class<?> clazz, Bundle bundle, int requestCode) {
//        Intent intent = new Intent(getActivity(), clazz);
//        if (bundle != null) {
//            intent.putExtras(bundle);
//        }
//        startActivityForResult(intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//    }
//
//    public void switchToActivityForResult(Class<?> clazz, int requestCode) {
//        Intent intent = new Intent(getActivity(), clazz);
//        startActivityForResult(intent, requestCode,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//    }
//
//    public void switchToActivityForResult(Context context, Class<?> clazz, int requestCode) {
//        Intent intent = new Intent(context, clazz);
//        startActivityForResult(intent, requestCode,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
//    }
//
//    public void switchToWelcomeActivity(Context context, Class<?> clazz) {
//        Intent intent = new Intent(context, clazz);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        //分开写会覆盖了
////        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

    /**
     * 共享控件动画启动Activity
     *
     * @param clazz        跳转的activity
     * @param bundle       参数
     * @param shareView    需要共享的view
     * @param shareElement 共享配置标签
     */
    public void switchToActivityWithTextShareAnim(Class<?> clazz, Bundle bundle, View shareView, String shareElement) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null)
            intent.putExtras(bundle);
        intent.putExtra(SHARE_ANIM_KEY, true);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), shareView, shareElement);
        startActivity(intent, options.toBundle());
    }

    public void switchToActivityWithTextShareAnim(Class<?> clazz, Bundle bundle, View shareView) {
        switchToActivityWithTextShareAnim(clazz, bundle, shareView, UiUtil.getString(R.string.shareElement_txt));
    }

    public void switchToActivityWithTextShareAnim(Class<?> clazz, View shareView) {
        switchToActivityWithTextShareAnim(clazz, null, shareView, UiUtil.getString(R.string.shareElement_txt));
    }

    public void switchToActivityWithImageShareAnim(Class<?> clazz, Bundle bundle, View shareView) {
        switchToActivityWithTextShareAnim(clazz, bundle, shareView, UiUtil.getString(R.string.shareElement_img));
    }

    public void switchToActivityWithImageShareAnim(Class<?> clazz, View shareView) {
        switchToActivityWithTextShareAnim(clazz, null, shareView, UiUtil.getString(R.string.shareElement_img));
    }

    public void switchToActivityForResultShareAnim(Class<?> clazz, Bundle bundle, View shareView, String shareElement,int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null){
            intent.putExtras(bundle);
        }
        intent.putExtra(SHARE_ANIM_KEY, true);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), shareView, shareElement);
        startActivityForResult(intent, requestCode,options.toBundle());
    }

    public void switchToActivityResultTextShareAnim(Class<?> clazz, Bundle bundle, View shareView,int requestCode) {
        switchToActivityForResultShareAnim(clazz, bundle, shareView, UiUtil.getString(R.string.shareElement_txt),requestCode);
    }

    public void switchToActivityResultTextShareAnim(Class<?> clazz, View shareView,int requestCode) {
        switchToActivityForResultShareAnim(clazz, null, shareView, UiUtil.getString(R.string.shareElement_txt),requestCode);
    }

    public void switchToActivityResultImageShareAnim(Class<?> clazz, Bundle bundle, View shareView,int requestCode) {
        switchToActivityForResultShareAnim(clazz, bundle, shareView, UiUtil.getString(R.string.shareElement_img),requestCode);
    }

    public void switchToActivityResultImageShareAnim(Class<?> clazz, View shareView,int requestCode) {
        switchToActivityForResultShareAnim(clazz, null, shareView, UiUtil.getString(R.string.shareElement_img),requestCode);
    }
    //---------------------------------空白页----------------------------------------------

    /**
     * 有动作的空白页
     *
     * @param targetView 替换的view
     * @param resource   显示的图片资源
     * @param desc       文字的描述
     * @param action     刷新按钮的文本
     */
    public void showEmpty(View targetView, int resource, String desc, String action) {
        helper.replaceView(targetView).
                image(resource).descText(desc).actionText(action).
                action(() -> {
                    emptyAction();
                }).show();
    }

    public void showEmpty(View targetView, int resource, String desc) {
        helper.replaceView(targetView).
                image(resource).descText(desc).
                action(() -> {
                    emptyAction();
                }).show();
    }


    public void showEmpty(View targetView, int resource, String desc, String action, int listCount) {

        if (listCount == 0) {
            helper.replaceView(targetView).
                    image(resource).descText(desc).actionText(action).
                    action(() -> {
                        emptyAction();
                    }).show();
        } else {
            LogUtil.e("showEmpty===隐藏");
            hideEmpty();
        }
    }

    public void showEmpty(View targetView, int resource, String desc, int listCount) {

        if (listCount == 0) {
            helper.replaceView(targetView).
                    image(resource).descText(desc).
                    action(() -> {
                        emptyAction();
                    }).show();
        } else {
            hideEmpty();
        }
    }

    /**
     * 隐藏空白页
     */
    public void hideEmpty() {
        helper.hide();
    }

    /**
     * 刷新数据
     */
    public void emptyAction() {
    }
}
