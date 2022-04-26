package com.yctc.zhiting.fragment;

import static com.yctc.zhiting.config.Constant.CurrentHome;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.event.FourZeroFourEvent;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.CommonWebActivity;
import com.yctc.zhiting.activity.FindSAGuideActivity;
import com.yctc.zhiting.activity.HowCreateSceneActivity;
import com.yctc.zhiting.activity.LogActivity;
import com.yctc.zhiting.activity.SceneDetailActivity;
import com.yctc.zhiting.adapter.SceneFragmentStatePagerAdapter;
import com.yctc.zhiting.adapter.TabAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.HomeSelectDialog;
import com.yctc.zhiting.dialog.RemovedTipsDialog;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.TabBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.scene.SceneBean;
import com.yctc.zhiting.entity.scene.SceneListBean;
import com.yctc.zhiting.event.AfterFindIPEvent;
import com.yctc.zhiting.event.HomeSelectedEvent;
import com.yctc.zhiting.event.PermissionEvent;
import com.yctc.zhiting.event.RefreshSceneEvent;
import com.yctc.zhiting.event.SocketStatusEvent;
import com.yctc.zhiting.fragment.contract.SceneFragmentContract;
import com.yctc.zhiting.fragment.presenter.SceneFragmentPresenter;
import com.yctc.zhiting.listener.ISceneView;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.AnimationUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;
import com.yctc.zhiting.widget.NoScrollViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * 首页-场景
 */
public class SceneFragment extends MVPBaseFragment<SceneFragmentContract.View, SceneFragmentPresenter> implements
        SceneFragmentContract.View, ISceneView {

    @BindView(R.id.ivRefresh)
    ImageView ivRefresh;
    @BindView(R.id.ivReconnect)
    ImageView ivReconnect;
    @BindView(R.id.viewEmpty)
    View viewEmpty;
    @BindView(R.id.tvMyHome)
    TextView tvMyHome;
    @BindView(R.id.refresh)
    TextView tvRefresh;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.viewTips)
    View viewTips;
    @BindView(R.id.ivEmpty)
    ImageView ivEmpty;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.tvReconnect)
    TextView tvReconnect;
    @BindView(R.id.ivAddScene)
    ImageView ivAddScene;
    @BindView(R.id.ivLog)
    ImageView ivLog;
    @BindView(R.id.ivGo)
    ImageView ivGo;
    @BindView(R.id.rlInvalid)
    RelativeLayout rlInvalid;
    @BindView(R.id.llReconnect)
    LinearLayout llReconnect;

    @BindView(R.id.vpContent)
    NoScrollViewPager vpContent;
    @BindView(R.id.tvSort)
    TextView tvSort;
    @BindView(R.id.rvTab)
    RecyclerView rvTab;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.llTab)
    LinearLayout llTab;
    @BindView(R.id.nsvEmpty)
    NestedScrollView nsvEmpty;

    private int mCurrentItem; // 当前项

    private WeakReference<Context> mContext;
    private DBManager dbManager;

    private boolean notFirst;
    private boolean hasLocal;
    private boolean showLoading;
    private boolean hasAddP; //添加场景权限
    public static boolean hasUpdateP; // 修改场景的权限
    public static boolean hasDelP; // 删除场景的权限
    private boolean needLoadData = true; // 是否加载数据

    private TabAdapter mTabAdapter; // tab适配器
    private List<TabBean> tabData = new ArrayList<>();  // tab数据
    private IWebSocketListener mWebSocketListener;
    private List<SceneItemFragment> mFragments = new ArrayList<>();
    private SceneFragmentStatePagerAdapter commonFragmentPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragmemt_scene;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void initUI() {
        mContext = new WeakReference<>(getActivity());
        dbManager = DBManager.getInstance(mContext.get());
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int colorId = UiUtil.getColor(R.color.white);
            float fraction = Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange();
            int alpha = changeAlpha(colorId, fraction);
            appBarLayout.setBackgroundColor(alpha);
        });
        setNextStatus(false);
        initWebSocket();
        showSaStatus();
        initTabLayout();
    }

    /**
     * sa状态
     */
    private void showSaStatus() {
        if (CurrentHome != null) {
            if (CurrentHome.isIs_bind_sa()) {
                handleTipStatus(!HomeUtil.isSAEnvironment());
            } else {
                handleTipStatus(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (notFirst && isVisible()) {
            wsConnectStatus(WSocketManager.isConnecting);
        }
        notFirst = true;
    }

    @Override
    protected void initData() {
        super.initData();
        if (CurrentHome != null) {
            tvMyHome.setText(CurrentHome.getName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebSocketListener != null) {
            WSocketManager.getInstance().removeWebSocketListener(mWebSocketListener);
        }
        notFirst = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(HomeSelectedEvent event) {
        if (!HomeUtil.tokenIsInvalid && needLoadData)
            homeChange();
        tvMyHome.setText(CurrentHome.getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PermissionEvent event) {
        ivAddScene.setVisibility(event.getPermissions().isAdd_scene() ? View.VISIBLE : View.GONE);
        hasAddP = event.getPermissions().isAdd_scene();
    }

    //socket状态统一有HomeFragment发通知管理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SocketStatusEvent event) {
        handleTipStatus(event.isShowTip());
    }

    // 重试/创建场景
    private void setNextStatus(boolean enabled) {
        llReconnect.setEnabled(enabled);
        llReconnect.setAlpha(llReconnect.isEnabled() ? 1 : 0.5f);
    }

    /**
     * 显示连接智慧中心失败
     *
     * @param showTip
     */
    private void handleTipStatus(boolean showTip) {
        if (HomeUtil.tokenIsInvalid) {  // token失效
            setInvalidSAToken();
        } else {  // token没失效
            rlInvalid.setVisibility(View.GONE);
            if (UserUtils.isLogin()) { // 登录sc
                viewTips.setVisibility(View.GONE);
            } else {  // 没登录sc
                setTipsRefreshVisible(true);
                tvTips.setText(getResources().getString(R.string.home_connect_fail));
                ivGo.setVisibility(View.GONE);
                ivRefresh.setVisibility(View.VISIBLE);
                tvRefresh.setVisibility(View.VISIBLE);
                viewTips.setVisibility(showTip ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * SAToken失效
     */
    private void setInvalidSAToken() {
        tvTips.setText(getResources().getString(R.string.home_invalid_token));
        ivGo.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.GONE);
        tvRefresh.setVisibility(View.GONE);
        viewTips.setVisibility(View.VISIBLE);
        viewEmpty.setVisibility(View.GONE);
        llTab.setVisibility(View.GONE);
        vpContent.setVisibility(View.GONE);
        rlInvalid.setVisibility(View.VISIBLE);
        nsvEmpty.setVisibility(View.VISIBLE);
    }

    /**
     * WebSocket 连接
     */
    private void initWebSocket() {
        if (isAdded()) {
            wsConnectStatus(WSocketManager.isConnecting);

            mWebSocketListener = new IWebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    wsConnectStatus(WSocketManager.isConnecting);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                    wsConnectStatus(WSocketManager.isConnecting);
                }
            };
            WSocketManager.getInstance().addWebSocketListener(mWebSocketListener);
        }
    }

    /**
     * websocket 的连接状态
     */
    private void wsConnectStatus(boolean isConnect) {
        ivLog.setEnabled(HomeUtil.isSAEnvironment() || UserUtils.isLogin());
        ivAddScene.setEnabled(HomeUtil.isSAEnvironment() || UserUtils.isLogin());

        if (HomeUtil.tokenIsInvalid) {
            rlInvalid.setVisibility(View.VISIBLE);
            nsvEmpty.setVisibility(View.VISIBLE);
            return;
        }
        if (HomeUtil.isSAEnvironment() || UserUtils.isLogin()) {//sa环境或登录sc
            getData(true);
        } else if (!hasLocal) {
            loadLocalScene();
        } else if (!CurrentHome.isIs_bind_sa()) {
            setNullView(true);
        }
    }

    @OnClick({R.id.llRefresh, R.id.llReconnect, R.id.tvMyHome, R.id.ivLog, R.id.ivAddScene, R.id.llHow, R.id.tvTips, R.id.viewTips, R.id.tvSort})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.llRefresh:  // 刷新
                AnimationUtil.rotationAnim(ivRefresh, 500, R.drawable.icon_scene_refreshing, R.drawable.icon_scene_refresh);
                if (WSocketManager.isConnecting) {
                    getData(true);
                } else {
                    WSocketManager.getInstance().start();
                    LogUtil.e("WSocketManager5==");
                }
                break;

            case R.id.llReconnect: // 刷新
                if (HomeUtil.isSAEnvironment() && !TextUtils.isEmpty(CurrentHome.getSa_user_token()) || UserUtils.isLogin()) {
                    if (hasAddP) {
                        switchToActivity(SceneDetailActivity.class);
                    } else {
                        ToastUtil.show(getResources().getString(R.string.scene_no_permission));
                    }
                } else {
                    AnimationUtil.rotationAnim(ivReconnect, 500, R.drawable.icon_scene_refreshing_white, R.drawable.icon_scenerefresh_white);
                    WSocketManager.getInstance().start();
                    LogUtil.e("WSocketManager6==");
                }
                break;

            case R.id.tvMyHome: // 家庭
                showHomeDialog();
                break;

            case R.id.ivLog:  // 日志
                switchToActivity(LogActivity.class);
                break;

            case R.id.ivAddScene:  // 添加场景
                switchToActivity(SceneDetailActivity.class);
                break;

            case R.id.llHow:  // 如何创建
                bundle.putBoolean(IntentConstant.HOW_CREATE, hasAddP);
                switchToActivity(HowCreateSceneActivity.class, bundle);
                break;

            case R.id.tvTips:  // 离线帮助
                if (!HomeUtil.tokenIsInvalid) {
                    Bundle offlineBundle = new Bundle();
                    offlineBundle.putInt(IntentConstant.WEB_URL_TYPE, 4);
                    switchToActivity(CommonWebActivity.class, offlineBundle);
                }
                break;

            case R.id.viewTips:
                if (HomeUtil.tokenIsInvalid) {
                    switchToActivity(FindSAGuideActivity.class);
                }
                break;

            case R.id.tvSort:  // 排序
                sort(true);
                break;
        }
    }

    /**
     * 排序场景
     */
    private void sort(boolean click) {
        boolean isSelected = tvSort.isSelected();
        vpContent.setScroll(isSelected);
        tvSort.setText(isSelected ? UiUtil.getString(R.string.scene_sort) : UiUtil.getString(R.string.scene_finish));
        if (CollectionUtil.isNotEmpty(mFragments) && click)
            mFragments.get(mCurrentItem).setSorting(!isSelected);
        tvSort.setSelected(!isSelected);
    }

    /**
     * 家庭列表弹窗
     */
    private void showHomeDialog() {
        if (CollectionUtil.isNotEmpty(HomeFragment2.mHomeList)) {
            for (HomeCompanyBean home : HomeFragment2.mHomeList) {
                home.setSelected(home.getLocalId() == CurrentHome.getLocalId() || (home.getArea_id() > 0 && home.getArea_id() == CurrentHome.getArea_id()));
            }
        }
        HomeSelectDialog homeSelectDialog = new HomeSelectDialog(HomeFragment2.mHomeList);
        homeSelectDialog.setClickItemListener(homeCompanyBean -> {
            hasAddP = false;
            HomeUtil.isInLAN = false;
            HomeFragment2.homeLocalId = homeCompanyBean.getLocalId();
            CurrentHome = homeCompanyBean;
            HomeUtil.tokenIsInvalid = false;
            needLoadData = false;
            EventBus.getDefault().post(new HomeSelectedEvent());
            SpUtil.put(SpConstant.SA_TOKEN, homeCompanyBean.getSa_user_token());
            SpUtil.put(SpConstant.AREA_ID, String.valueOf(homeCompanyBean.getId()));

            homeSelectDialog.dismiss();
            String saLanAddress = homeCompanyBean.getSa_lan_address();
            HttpUrlConfig.baseSAUrl = TextUtils.isEmpty(saLanAddress) ? "" : saLanAddress;
            HttpUrlConfig.apiSAUrl = HttpUrlConfig.baseSAUrl + HttpUrlConfig.API;
            //检测接口是否有用
            if (CurrentHome.isIs_bind_sa() && TextUtils.isEmpty(CurrentHome.getBSSID()) && !TextUtils.isEmpty(CurrentHome.getSa_lan_address())) { // 绑定了sa，mac地址为空，sa_lan_address不为空
                checkUrl();
            } else {
                if (HomeUtil.isSAEnvironment() || UserUtils.isLogin()) {
                    WSocketManager.getInstance().start();
                } else {
                    setNullView(true);
                }
                LogUtil.e("WSocketManager7==");
            }
            showSaStatus();
        });
        homeSelectDialog.show(this);
    }

    /**
     * 找回ip地址后
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AfterFindIPEvent event) {
        checkUrl();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshSceneEvent event) {
        getData(false);
    }

    private void checkUrl() {
        if (TextUtils.isEmpty(CurrentHome.getSa_lan_address())) {
            EventBus.getDefault().post(new FourZeroFourEvent());
        } else {
            AllRequestUtil.checkUrl(CurrentHome.getSa_lan_address(), new AllRequestUtil.onCheckUrlListener() {
                @Override
                public void onSuccess() {
                    WifiInfo wifiInfo = Constant.wifiInfo;
                    if (wifiInfo != null && wifiInfo.getBSSID() != null) {
                        CurrentHome.setBSSID(wifiInfo.getBSSID());
                        dbManager.updateHomeMacAddress(CurrentHome.getLocalId(), wifiInfo.getBSSID());
                    }
                    WSocketManager.getInstance().start();
                    LogUtil.e("WSocketManager8==");
                }

                @Override
                public void onError() {
                    CurrentHome.setBSSID("");
                    WSocketManager.getInstance().start();
                    LogUtil.e("WSocketManager9==");
                }
            });
        }
    }

    /**
     * 切换家庭刷新数据
     */
    private void homeChange() {
        if (CurrentHome.isIs_bind_sa()) {
            if (WSocketManager.isConnecting) {
                getData(true);
            } else {
                loadLocalScene();
            }
        } else {
            setNullView(true);
        }
    }

    /**
     * 加载数据
     */
    private void getData(boolean showLoading) {
        if (CurrentHome != null) {
            if (HomeUtil.isSAEnvironment() || UserUtils.isLogin()) {
                this.showLoading = showLoading;
                HttpConfig.addHeader(CurrentHome.getSa_user_token());
                if (mPresenter != null) {
                    mPresenter.getPermissions(CurrentHome.getUser_id());
                }
            } else {
                loadLocalScene();
            }
        }
    }

    /**
     * 场景列表成功
     *
     * @param data
     */
    @Override
    public void getSceneListSuccess(SceneListBean data) {
        if (CollectionUtil.isNotEmpty(mFragments)) {
            mFragments.get(mCurrentItem).setRefreshLayoutEnable();
        }
        if (data != null) {
            saveScenes(data);
            loadData(data);
        } else {
            setNullView(true);
        }
    }

    /**
     * 加载数据
     *
     * @param data
     */
    private void loadData(SceneListBean data) {
        List<SceneBean> manualSceneData = data.getManual();
        List<SceneBean> autoSceneData = data.getAuto_run();
        tvSort.setSelected(true);
        sort(false);
        if (CollectionUtil.isNotEmpty(manualSceneData) || CollectionUtil.isNotEmpty(autoSceneData)) {
            setNullView(false);
            tabData.clear();
            mFragments.clear();
            if (CollectionUtil.isNotEmpty(manualSceneData)) {  // 手动不为空
                if (!TabBean.TAB_AUTOMATIC.isSelected())
                    TabBean.TAB_MANUAL.setSelected(true);
                tabData.add(TabBean.TAB_MANUAL);
                SceneItemFragment sceneItemFragment = SceneItemFragment.getInstance(0, manualSceneData);
                mFragments.add(sceneItemFragment);
            } else {
                TabBean.TAB_MANUAL.setSelected(false);
            }
            if (CollectionUtil.isNotEmpty(autoSceneData)) {  // 自动不为空
                if (!TabBean.TAB_MANUAL.isSelected())
                    TabBean.TAB_AUTOMATIC.setSelected(true);
                tabData.add(TabBean.TAB_AUTOMATIC);
                SceneItemFragment sceneItemFragment = SceneItemFragment.getInstance(1, autoSceneData);
                mFragments.add(sceneItemFragment);
            } else {
                TabBean.TAB_AUTOMATIC.setSelected(false);
            }
            commonFragmentPagerAdapter.updateFragments(mFragments);
            if (CollectionUtil.isNotEmpty(tabData)) {
                for (int i = 0; i < tabData.size(); i++) {
                    TabBean tabBean = tabData.get(i);
                    if (tabBean.isSelected()) {
                        mCurrentItem = i;
                        break;
                    }
                }
                vpContent.setCurrentItem(mCurrentItem);
            } else {
                mCurrentItem = 0;
            }
            mTabAdapter.notifyDataSetChanged();
        } else {
            setNullView(true);
        }
    }

    /**
     * 场景列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getSceneListError(int errorCode, String msg) {
        LogUtil.e("SceneFragment错误码："+errorCode);
        setNullView(true);
        if (CollectionUtil.isNotEmpty(mFragments)) {
            mFragments.get(mCurrentItem).setRefreshLayoutEnable();
        }
    }

    /**
     * 执行成功
     */
    @Override
    public void performSuccess(int position) {
        String tip = UiUtil.getString(R.string.scene_perform_success);
        ToastUtil.show(tip);
    }

    /**
     * 执行失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void performFail(int errorCode, int position, String msg) {
        if (errorCode == 5012 || errorCode == 5027) {
            findSAToken();
        } else {
            ToastUtil.show(msg);
        }
    }

    @Override
    public void onPermissionsFail(int errorCode, String msg) {
        if (CollectionUtil.isNotEmpty(mFragments)) {
            mFragments.get(mCurrentItem).setRefreshLayoutEnable();
        }
        if (errorCode == 5012 || errorCode == 5027) {
            findSAToken();
        }
    }

    /**
     * 找回用户凭证
     */
    private void findSAToken() {
        if (UserUtils.isLogin()) {
            NameValuePair nameValuePair = new NameValuePair("area_id", String.valueOf(CurrentHome.getId()));
            List<NameValuePair> requestData = new ArrayList<>();
            requestData.add(nameValuePair);
            mPresenter.getSAToken(CurrentHome.getCloud_user_id(), requestData);  // sc的用户id, sc上的家庭id
        } else {
            showRemovedTipsDialog();
        }
    }

    /**
     * 移除家庭
     */
    private void showRemovedTipsDialog() {
        HomeUtil.isInLAN = false;
        if (notFirst) {
            RemovedTipsDialog removedTipsDialog = new RemovedTipsDialog(String.format(UiUtil.getString(R.string.common_remove_home), CurrentHome.getName()));
            removedTipsDialog.show(this);
            dbManager.removeFamilyByToken(CurrentHome.getSa_user_token());
            if (CollectionUtil.isNotEmpty(HomeFragment2.mHomeList)) {
                for (HomeCompanyBean homeCompanyBean : HomeFragment2.mHomeList) {
                    if (!TextUtils.isEmpty(homeCompanyBean.getSa_user_token()) && homeCompanyBean.getSa_user_token().equalsIgnoreCase(CurrentHome.getSa_user_token())) {
                        HomeFragment2.mHomeList.remove(homeCompanyBean);
                        break;
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(HomeFragment2.mHomeList)) {  // 如果还有家庭
                CurrentHome = HomeFragment2.mHomeList.get(0);
                refreshHome();
            } else {  // 如果没有家庭，就新建一个
                createLocalHome();
            }
        }
    }

    /**
     * 刷新家庭
     */
    private void refreshHome() {
        HomeUtil.tokenIsInvalid = false;
        EventBus.getDefault().post(new HomeSelectedEvent());
        setNullView(true);
    }

    /**
     * 创建本地将他
     */
    private void createLocalHome() {
        UiUtil.starThread(() -> {
            HomeCompanyBean homeCompanyBean = new HomeCompanyBean(1, getResources().getString(R.string.main_my_home));
            homeCompanyBean.setIs_bind_sa(false);
            homeCompanyBean.setSa_user_token(null);
            homeCompanyBean.setSa_lan_address(null);
            homeCompanyBean.setUser_id(1);
            homeCompanyBean.setSelected(true);
            homeCompanyBean.setArea_type(Constant.HOME_MODE);
            long count = dbManager.insertHomeCompany(homeCompanyBean, null, false);
            UiUtil.runInMainThread(() -> {
                CurrentHome = homeCompanyBean;
                HomeFragment2.mHomeList.add(homeCompanyBean);
                refreshHome();
            });
        });
    }

    /**
     * 获取权限成功
     *
     * @param permissionBean
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean != null) {
            PermissionBean.PermissionsBean pb = permissionBean.getPermissions();
            if (pb != null) {
                mPresenter.getSceneList(showLoading);
                ivAddScene.setVisibility(pb.isAdd_scene() ? View.VISIBLE : View.GONE);
                hasAddP = pb.isAdd_scene();
                hasUpdateP = pb.isUpdate_scene();
                hasDelP = pb.isDelete_scene();
                tvSort.setVisibility(hasUpdateP ? View.VISIBLE : View.GONE);
            }
        }
        needLoadData = true;
    }

    /**
     * 找回token成功
     *
     * @param findSATokenBean
     */
    @Override
    public void getSATokenSuccess(FindSATokenBean findSATokenBean) {
        if (findSATokenBean != null) {
            HomeUtil.tokenIsInvalid = false;
            String saToken = findSATokenBean.getSa_token();
            CurrentHome.setSa_user_token(saToken);
            getData(false);
            UiUtil.starThread(() -> dbManager.updateSATokenByLocalId(CurrentHome.getLocalId(), saToken));
        }
    }

    /**
     * 找回token失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getSATokenFail(int errorCode, String msg) {
        if (errorCode == 2011) {    //凭证获取失败，状态码2011，无权限
            HomeUtil.tokenIsInvalid = true;
            setInvalidSAToken();
            setTipsRefreshVisible(false);
        } else if (errorCode == 3002) {  //状态码3002，提示被管理员移除家庭
            showRemovedTipsDialog();
        } else {
            ToastUtil.show(msg);
        }
    }

    /**
     * 设置刷新是否可见
     */
    private void setTipsRefreshVisible(boolean showRefresh) {
        tvTips.setText(showRefresh ? getResources().getString(R.string.home_connect_fail) : getResources().getString(R.string.home_invalid_token));
        ivRefresh.setVisibility(showRefresh ? View.VISIBLE : View.GONE);
        tvRefresh.setVisibility(showRefresh ? View.VISIBLE : View.GONE);
        ivGo.setVisibility(showRefresh ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置空视图
     *
     * @param visible
     */
    private void setNullView(boolean visible) {
        if (CurrentHome == null) return;
        setNextStatus(HomeUtil.isSAEnvironment() || UserUtils.isLogin());
        rlInvalid.setVisibility(View.GONE);
        viewEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
        nsvEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
        llTab.setVisibility(visible ? View.GONE : View.VISIBLE);
        vpContent.setVisibility(visible ? View.GONE : View.VISIBLE);
        if ((HomeUtil.isSAEnvironment() && CurrentHome.isIs_bind_sa()) || !CurrentHome.isIs_bind_sa() || UserUtils.isLogin()) {
            ivEmpty.setImageResource(R.drawable.icon_scene_null);
            tvEmpty.setText(UiUtil.getString(R.string.scene_not));
            tvReconnect.setText(UiUtil.getString(R.string.scene_add_scene));
            ivReconnect.setVisibility(View.GONE);
        } else {
            ivEmpty.setImageResource(R.drawable.icon_scene_empty);
            tvEmpty.setText(UiUtil.getString(R.string.scene_fail_to_intelligence_or_unavailable));
            tvReconnect.setText(UiUtil.getString(R.string.scene_reconnect));
            ivReconnect.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 保存场景数据
     */
    public void saveScenes(SceneListBean data) {
        UiUtil.starThread(() -> {
            String sceneStr = GsonConverter.getGson().toJson(data);
            dbManager.insertScene(CurrentHome.getSa_user_token(), sceneStr);
        });
    }

    /**
     * 加载本地缓存数据
     */
    private void loadLocalScene() {
        if (HomeUtil.isHomeIdThanZero()) {
            UiUtil.starThread(() -> {
                String scene = dbManager.getScene(CurrentHome.getSa_user_token());
                UiUtil.runInMainThread(() -> {
                    if (!TextUtils.isEmpty(scene)) {
                        hasLocal = true;
                        SceneListBean sceneListBean = GsonConverter.getGson().fromJson(scene, SceneListBean.class);
                        loadData(sceneListBean);
                    } else {
                        setNullView(true);
                    }
                });
            });
        } else {
            setNullView(true);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            WSocketManager.getInstance().removeWebSocketListener(mWebSocketListener);
        } else {
            needLoadData = true;
            if (CurrentHome != null)
                tvMyHome.setText(CurrentHome.getName());
            if (HomeUtil.tokenIsInvalid) { // satoken有效
                setInvalidSAToken();
            } else {
                showSaStatus();
                initWebSocket();
            }
        }
    }

    @Override
    public void selectTab() {
    }

    /**
     * 初始化频道
     */
    private void initTabLayout() {
        commonFragmentPagerAdapter = new SceneFragmentStatePagerAdapter(getChildFragmentManager());
        vpContent.setAdapter(commonFragmentPagerAdapter);

        vpContent.setScroll(true);
        rvTab.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mTabAdapter = new TabAdapter();
        mTabAdapter.setNewData(tabData);
        rvTab.setAdapter(mTabAdapter);
        mTabAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (tvSort.isSelected()) return;
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

    /**
     * 设置tab的状态
     *
     * @param position
     */
    private void setTabStatus(int position) {
        mCurrentItem = position;
        TabBean tabBean = mTabAdapter.getItem(position);
        if (tabBean == null) return;
        if (tabBean.isSelected()) return;
        for (TabBean tb : mTabAdapter.getData()) {
            tb.setSelected(false);
        }
        tabBean.setSelected(true);
        mTabAdapter.notifyDataSetChanged();
    }
}
