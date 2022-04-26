package com.app.main.framework.baseview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityOptionsCompat;

import com.app.main.framework.R;
import com.app.main.framework.baseutil.AndroidBugsSolution;
import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.KeyboardHelper;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.NetworkUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.LoadingDialog;
import com.app.main.framework.disklrucache.DiskLruCacheHelper;
import com.app.main.framework.entity.AppEventEntity;
import com.app.main.framework.view.loading.LoadingView;
import com.app.main.framework.view.titlebar.BaseTitleBar;
import com.app.main.framework.view.titlebar.TitleBarManager;
import com.app.main.framework.widget.ReplaceViewHelper;
import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.ButterKnife;

public abstract class BaseActivity extends BasePermissionsAndStackActivity implements BaseView {
    public static final String SHARE_ANIM_KEY = "SHARE_ANIM_KEY";
    public String TAG = getClass().getSimpleName() + "====";
    //根容器布局
    private RelativeLayout mContainer;
    /*导航栏*/
    private BaseTitleBar titleBar;
    /*加载框子*/
    private LoadingView loadingView;
    private View statesBar;

    public ReplaceViewHelper helper = new ReplaceViewHelper(LibLoader.getCurrentActivity());
    public DiskLruCacheHelper cacheHelper;//数据的缓存
    private Runnable mTimeCloseRunnable;//延时关闭loading的runnable

    private LoadingDialog loadingDialog;
    public Bundle bundle;

    protected LoadingDialog loadingDialogInAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .init();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        AndroidBugsSolution.assistActivity(this, null); //adjustResize无效解决
        Intent intent = getIntent();
        initUI();
        initListener();
        if (intent != null) {
            initIntent(getIntent());
        }
        initData();
        if (bindEventBus()) {//注册EventBus
            if (!EventBus.getDefault().isRegistered(this)) {
                LogUtil.e("Base=注册===EventBus1===" + TAG);
//                EventBus.getDefault().unregister(this);
                EventBus.getDefault().register(this);
            }
        }
        LogUtil.e("当前组件======" + TAG);
        try {
            cacheHelper = new DiskLruCacheHelper(BaseActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bundle = new Bundle();
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
//-------------------------------------------------------EventBus--------------------------------------------------

    /**
     * 是否加载默认标题
     *
     * @return 是否加载
     */
    protected boolean isLoadTitleBar() {
        return true;
    }

    /**
     * 是否设置状态栏布局
     *
     * @return
     */
    protected boolean isSetStateBar() {
        return true;
    }

    /**
     * 是否加载标题右边菜单
     *
     * @return
     */
    protected boolean isLoadRightTitleMenu() {
        return false;
    }

    /**
     * 设置资源id
     *
     * @return 资源id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 初始化各种监听事件
     */
    protected void initIntent(Intent intent) {
    }

    /**
     * 初始化界面控件
     */
    protected void initUI() {
    }

    /**
     * 初始化监听器
     */
    protected void initListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setTheme(R.style.AppTheme);
        timerCloseLoadingView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AndroidUtil.hideSoftInput(BaseActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        if (bindEventBus()) {//注册EventBus
            EventBus.getDefault().unregister(this);
        }
        AndroidUtil.hideSoftInput(BaseActivity.this);
    }

    private void initLoadingView() {
        if (loadingView == null) {
            loadingView = new LoadingView();
            mContainer.addView(loadingView.getRootView(), getLayoutParams());
        }
    }

    protected void setStatesBarColor(@IdRes int colorId) {
        statesBar.setBackgroundColor(UiUtil.getColor(colorId));
    }

    public void initTitleBar() {
        if (isSetStateBar()) {
            statesBar = new View(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    UiUtil.getStateBarHeight());
            statesBar.setBackgroundColor(UiUtil.getColor(R.color.white));
            statesBar.setId(R.id.base_states_bar);
            mContainer.addView(statesBar, lp);
        }
        if (isLoadTitleBar()) {
            titleBar = new TitleBarManager(this);
            titleBar.setTitleBarLeftClickListener(v -> onBackPressed());
            titleBar.setTitleBarTextLeftClickListener(v -> onBackPressed());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    UiUtil.getDimens(R.dimen.dp_44));
            if (statesBar != null) {
                lp.addRule(RelativeLayout.BELOW, R.id.base_states_bar);
            }
            mContainer.addView(titleBar.getView(), lp);
            if (!isLoadRightTitleMenu()) {
                titleBar.hideTitleBarMoreView();
            }
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(initContentView(view));
    }

    protected View initContentView(View view) {
        mContainer = new RelativeLayout(this);
        initTitleBar();
        mContainer.addView(view, getLayoutParams());
        return mContainer;
    }

    private RelativeLayout.LayoutParams getLayoutParams() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (titleBar != null) {
            lp.addRule(RelativeLayout.BELOW, titleBar.getId());
        } else if (statesBar != null) {
            lp.addRule(RelativeLayout.BELOW, R.id.base_states_bar);
        }
        return lp;
    }

    public void setTitleStr(String text) {
        if (titleBar != null)
            titleBar.setTitle(text);
    }

    @SuppressLint("ResourceType")
    public void setTitleStr(@StringRes int resId) {
        setTitleStr(getString(resId));
    }

    public void setBackListener(View.OnClickListener listener) {
        if (titleBar != null)
            titleBar.setTitleBarLeftClickListener(listener);
    }

    @Override
    public boolean isNetworkerConnectHint() {
        boolean networkerConnect = NetworkUtil.isNetworkerConnect();
        if (!networkerConnect) {
            showHint(getString(R.string.network_error));
        }
        return networkerConnect;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showLoadingView() {
        runOnUiThread(() -> {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(this);
                loadingDialog.setOnShowListener(dialog -> loadingDialog.reset());

                loadingDialog.setOnDismissListener(dialog -> loadingDialog.stop());
            }
            if (!loadingDialog.isShowing()) {
                loadingDialog.show();
                timerCloseLoadingView();
            }
        });
    }

    @Override
    public void hideLoadingView() {
        if (!isDestroyed() && loadingDialog != null && loadingDialog.isShowing()) {
            runOnUiThread(() -> {
                loadingDialog.dismiss();
            });
        }
    }

    /**
     * 加载弹窗
     */
    public void showLoadingDialogInAct() {
        if (loadingDialogInAct == null) {
            loadingDialogInAct = new LoadingDialog(this);
            loadingDialogInAct.setOnShowListener(dialog -> loadingDialogInAct.reset());

            loadingDialogInAct.setOnDismissListener(dialog -> loadingDialogInAct.stop());
        }
        loadingDialogInAct.show();
    }

    public void dismissLoadingDialogInAct() {
        if (loadingDialogInAct != null && loadingDialogInAct.isShowing()) {
            loadingDialogInAct.dismiss();
        }
    }

    private void timerCloseLoadingView() {
        if (mTimeCloseRunnable == null) {
            mTimeCloseRunnable = () -> {
                if (loadingDialog != null && loadingDialog.isShowing() && !isDestroyed())
                    loadingDialog.dismiss();
            };
        }
        UiUtil.postDelayed(mTimeCloseRunnable, 30000);
    }

    public void showLoadingView(@ColorRes int colorRes) {
        runOnUiThread(() -> {
            initLoadingView();
            if (loadingView.getRootView().getVisibility() != View.VISIBLE) {
                loadingView.getRootView().setBackgroundColor(UiUtil.getColor(colorRes));
                loadingView.getRootView().setVisibility(View.VISIBLE);
                KeyboardHelper.hideKeyboard(loadingView.getRootView());
                UiUtil.postDelayed(this::hideLoadingView, 30000);
            }
        });
    }

    @Override
    public BaseTitleBar getTitleBar() {
        return titleBar;
    }

    @Override
    public void showHint(@Nullable String hintText) {
        if (!TextUtils.isEmpty(hintText)) {
            Toast.makeText(this, hintText, Toast.LENGTH_LONG).show();
        }
    }

    //界面之间的跳转
    public void switchToActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    public void switchToActivityClear(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void switchToActivity(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        startActivity(intent);
    }

    public void switchToActivityClear(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchToActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
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
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    public void switchToActivityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
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
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 共享控件动画启动Activity
     *
     * @param clazz        跳转的activity
     * @param bundle       参数
     * @param shareView    需要共享的view
     * @param shareElement 共享配置标签
     */
    public void switchToActivityWithTextShareAnim(Class<?> clazz, Bundle bundle, View shareView, String shareElement) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null)
            intent.putExtras(bundle);
        intent.putExtra(SHARE_ANIM_KEY, true);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, shareView, shareElement);
        startActivity(intent);
    }
//    public void switchToActivity(Class<?> clazz) {
//        Intent intent = new Intent(this, clazz);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//    }
//
//    public void switchToActivityClear(Class<?> clazz) {
//        Intent intent = new Intent(this, clazz);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//    }
//
//    public void switchToActivity(Context context, Class<?> clazz) {
//        Intent intent = new Intent(context, clazz);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//    }
//
//    public void switchToActivityClear(Class<?> clazz, Bundle bundle) {
//        Intent intent = new Intent(this, clazz);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtras(bundle);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//    }
//
//    public void switchToActivity(Class<?> clazz, Bundle bundle) {
//        Intent intent = new Intent(this, clazz);
//        intent.putExtras(bundle);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//    }
//
//    public void switchToActivity(Context context, Class<?> clazz, Bundle bundle) {
//        Intent intent = new Intent(context, clazz);
//        intent.putExtras(bundle);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//    }
//
//    public void switchToActivityForResult(Context context, Class<?> clazz,
//                                          Bundle bundle, int requestCode) {
//        Intent intent = new Intent(context, clazz);
//        if (bundle != null) {
//            intent.putExtras(bundle);
//        }
//        startActivityForResult(intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//    }
//
//    public void switchToActivityForResult(Class<?> clazz, Bundle bundle, int requestCode) {
//        Intent intent = new Intent(this, clazz);
//        if (bundle != null) {
//            intent.putExtras(bundle);
//        }
//        startActivityForResult(intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//    }
//
//    public void switchToActivityForResult(Class<?> clazz, int requestCode) {
//        Intent intent = new Intent(this, clazz);
//        startActivityForResult(intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//    }
//
//    public void switchToActivityForResult(Context context, Class<?> clazz, int requestCode) {
//        Intent intent = new Intent(context, clazz);
//        startActivityForResult(intent, requestCode, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//    }
//
//    public void switchToWelcomeActivity(Context context, Class<?> clazz) {
//        Intent intent = new Intent(context, clazz);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        //分开写会覆盖了
//        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
//
//    /**
//     * 共享控件动画启动Activity
//     *
//     * @param clazz        跳转的activity
//     * @param bundle       参数
//     * @param shareView    需要共享的view
//     * @param shareElement 共享配置标签
//     */
//    public void switchToActivityWithTextShareAnim(Class<?> clazz, Bundle bundle, View shareView, String shareElement) {
//        Intent intent = new Intent(this, clazz);
//        if (bundle != null)
//            intent.putExtras(bundle);
//        intent.putExtra(SHARE_ANIM_KEY, true);
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, shareView, shareElement);
//        startActivity(intent, options.toBundle());
//    }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }
//---------------------------------------TitleBar------------------------------------------------------

    /**
     * 设置标题栏中间的文字
     *
     * @param text
     */
    public void setTitleCenter(String text) {
        titleBar.setTitle(text);
    }

    /**
     * 设置左边的文字
     *
     * @param text
     */
    public void setTitleLeftText(String text) {
        titleBar.setTitleBarLeftText(text);
    }

    /**
     * 设置右边按钮是否可用
     *
     * @param enable
     */
    public void setTitleRightEnable(boolean enable) {
        titleBar.setTitleBarMoreEnable(enable);
    }

    /**
     * 设置右边的文字
     *
     * @param text
     */
    public void setTitleRightText(String text) {
        titleBar.setTitleBarMoreText(text);
    }

    public void setTitleRightText(int StringResId) {
        titleBar.setTitleBarMoreText(StringResId);
    }

    public TextView getRightTitleText() {
        return titleBar.getTvTitleBarMoreText();
    }

    /**
     * 设置左边按钮不可用
     *
     * @param
     */
    public void setTitleLeftIconEnable() {
        titleBar.hideTitleBarLeftVeiw();
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

    public void showEmptyTop(View targetView, int resource, String desc, int listCount) {
        if (listCount == 0) {
            helper.replaceView(targetView).
                    image(resource).descText(desc).showTop().
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

    /**
     * 显示键盘
     *
     * @param view
     */
    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * 隐藏键盘
     *
     * @param view
     */
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 键盘显示开关
     *
     * @param view
     * @param show
     */
    public void keyboardSwitch(View view, boolean show) {
        if (show) {
            showKeyboard(view);
        } else {
            hideKeyboard(view);
        }
    }
}
