package com.yctc.zhiting.fragment;

import static com.yctc.zhiting.config.Constant.CurrentHome;
import static com.yctc.zhiting.config.Constant.wifiInfo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.app.main.framework.NetworkErrorConstant;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.NetworkUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BaseFragment;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.event.FourZeroFourEvent;
import com.app.main.framework.event.RefreshRoomListEvent;
import com.app.main.framework.event.TempChannelFailEvent;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.TempChannelUtil;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.httputil.cookie.PersistentCookieStore;
import com.app.main.framework.widget.StatusBarView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.CaptureNewActivity;
import com.yctc.zhiting.activity.CommonWebActivity;
import com.yctc.zhiting.activity.FindSAGuideActivity;
import com.yctc.zhiting.activity.ScanDevice2Activity;
import com.yctc.zhiting.adapter.HomeFragmentPagerAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.HomeSelectDialog;
import com.yctc.zhiting.dialog.RemovedTipsDialog;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.home.ApiVersionBean;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.AndroidAppVersionBean;
import com.yctc.zhiting.entity.mine.CheckBindSaBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;
import com.yctc.zhiting.entity.mine.UploadFileBean;
import com.yctc.zhiting.event.AfterFindIPEvent;
import com.yctc.zhiting.event.DeviceDataEvent;
import com.yctc.zhiting.event.DeviceRefreshEvent;
import com.yctc.zhiting.event.HomeEvent;
import com.yctc.zhiting.event.HomeSelectedEvent;
import com.yctc.zhiting.event.LogoutEvent;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.event.PermissionEvent;
import com.yctc.zhiting.event.RefreshHome;
import com.yctc.zhiting.event.RefreshHomeList;
import com.yctc.zhiting.event.SocketStatusEvent;
import com.yctc.zhiting.event.UpdateHeadImgEvent;
import com.yctc.zhiting.event.UpdateProfessionStatusEvent;
import com.yctc.zhiting.event.UpdateSaUserNameEvent;
import com.yctc.zhiting.fragment.contract.HomeFragmentContract;
import com.yctc.zhiting.fragment.presenter.HomeFragmentPresenter;
import com.yctc.zhiting.listener.IHomeView;
import com.yctc.zhiting.receiver.WifiReceiver;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.AnimationUtil;
import com.yctc.zhiting.utils.ChannelUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;

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

public class HomeFragment extends MVPBaseFragment<HomeFragmentContract.View, HomeFragmentPresenter> implements
        HomeFragmentContract.View, IHomeView {

    private final String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.vpContent)
    ViewPager vpContent;
    @BindView(R.id.sbView)
    StatusBarView sbView;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.tvMyHome)
    TextView tvMyHome;
    @BindView(R.id.tvRefresh)
    TextView tvRefresh;
    @BindView(R.id.ivAddDevice)
    ImageView ivAddDevice;
    @BindView(R.id.llTips)
    LinearLayout llTips;
    @BindView(R.id.ivRefresh)
    ImageView ivRefresh;
    @BindView(R.id.ivGo)
    ImageView ivGo;

    private int currentItem;
    public static long homeLocalId;  // 当前家庭本地id值
    public static boolean addDeviceP = false; // 添加设备权限
    private boolean needLoading;
    private boolean isFirstInit = true;//是否第一次进入
    private boolean showRemoveDialog = true;
    private boolean hasLoadHomeList = false; // 家庭列表是否加载过

    private DBManager dbManager;
    private WeakReference<Context> mContext;
    private IWebSocketListener mWebSocketListener;
    private List<HomeItemFragment2> fragments = new ArrayList<>();
    public static List<HomeCompanyBean> mHomeList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragmemt_home;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void initUI() {
        SpUtil.init(getContext());
        initWebSocket();
        registerWifiReceiver();
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int colorId = UiUtil.getColor(R.color.white);
            float fraction = Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange();
            int alpha = changeAlpha(colorId, fraction);

            sbView.setBackgroundColor(alpha);
            appBarLayout.setBackgroundColor(alpha);
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = new WeakReference<>(getActivity());
        dbManager = DBManager.getInstance(mContext.get());
    }

    private void initWebSocket() {
        mWebSocketListener = new IWebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                handleTipStatus();
            }
        };
        WSocketManager.getInstance().addWebSocketListener(mWebSocketListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        startConnectSocket();
    }

    /**
     * 绑定SA&&不在SA环境&&没有登陆 显示
     */
    private void handleTipStatus() {
        setTipsRefreshVisible(true);
        startConnectSocket();
        if (UserUtils.isLogin()) {
            llTips.setVisibility(View.GONE);
            EventBus.getDefault().post(new SocketStatusEvent(false));
        } else {
            boolean isShowStatus = (HomeUtil.isHomeIdThanZero() && !HomeUtil.isSAEnvironment());
            llTips.setVisibility(isShowStatus ? View.VISIBLE : View.GONE);
            EventBus.getDefault().post(new SocketStatusEvent(isShowStatus));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LogoutEvent event) {
        handleTipStatus();
    }

    /**
     * 设置刷新是否可见
     */
    private void setTipsRefreshVisible(boolean showRefresh) {
        UiUtil.runInMainThread(new Runnable() {
            @Override
            public void run() {
                tvTips.setText(showRefresh ? UiUtil.getString(R.string.home_connect_fail) : UiUtil.getString(R.string.home_invalid_token));
                ivRefresh.setVisibility(showRefresh ? View.VISIBLE : View.GONE);
                tvRefresh.setVisibility(showRefresh ? View.VISIBLE : View.GONE);
                ivGo.setVisibility(showRefresh ? View.GONE : View.VISIBLE);
            }
        });
    }

    /**
     * Wifi 状态监听注册
     */
    private void registerWifiReceiver() {
        if (mWifiReceiver == null) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mWifiReceiver, filter);
    }

    /**
     * Wifi 状态监听注销
     */
    public void unRegisterWifiReceiver() {
        if (mWifiReceiver == null) return;
        getActivity().unregisterReceiver(mWifiReceiver);
    }

    /**
     * Wifi 状态接收器
     */
    private final WifiReceiver mWifiReceiver = new WifiReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                EventBus.getDefault().post(new UpdateProfessionStatusEvent());
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    LogUtil.e(TAG, "网络=wifi断开");
                    wifiInfo = null;
                    handleDisconnect();
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        wifiInfo = wifiManager.getConnectionInfo();
                        if (CurrentHome != null && !TextUtils.isEmpty(CurrentHome.getSa_lan_address())) {
                            handleTipStatus();
                            if (hasLoadHomeList) {
                                checkInterfaceEnabled();
                            }
                        }
                        //获取当前wifi名称
                        LogUtil.e(TAG, "网络=连接到网络1 " + wifiInfo.getSSID());
                        LogUtil.e(TAG, "网络=连接到网络2 " + GsonConverter.getGson().toJson(wifiInfo));
                        LogUtil.e(TAG, "网络=连接到网络3 " + wifiInfo.getBSSID());
                        LogUtil.e(TAG, "网络=连接到网络4 " + wifiInfo.getMacAddress());
                    }
                }
                if (isFirstInit) {
                    isFirstInit = false;
                    loadData();
                }
            }
        }
    };

    /**
     * wifi断开就不是SA环境了
     */
    private void handleDisconnect() {
        if (CurrentHome != null) {
            if (wifiInfo != null) {
                String address = wifiInfo.getBSSID();
                String macAddress = CurrentHome.getBSSID();

                if (TextUtils.isEmpty(macAddress)) {
                    if (!TextUtils.isEmpty(address)) {
                        CurrentHome.setBSSID(address);
                        dbManager.updateHomeMacAddress(CurrentHome.getLocalId(), address);
                    }
                } else {
                    if (!macAddress.equals(address))
                        CurrentHome.setBSSID("");
                }
            }

            getRoomList(false);
            handleTipStatus();
            EventBus.getDefault().post(new UpdateProfessionStatusEvent());
            startConnectSocket();
        }
    }

    /**
     * 开始连接socket
     */
    private void startConnectSocket() {
        if (!WSocketManager.isConnecting && HomeUtil.isBindSA()) {
            WSocketManager.getInstance().start();        LogUtil.e("WSocketManager1==");
            UiUtil.postDelayed(() -> {
                if (!WSocketManager.isConnecting)
                    WSocketManager.getInstance().start();        LogUtil.e("WSocketManager2==");
            }, 2000);
        }
    }

    @OnClick({R.id.ivScan, R.id.ivAddDevice, R.id.tvMyHome, R.id.ivRefresh, R.id.tvRefresh, R.id.tvTips, R.id.llTips})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivScan://扫码
                mRunnable = () -> switchToActivity(CaptureNewActivity.class);
                accessFineLocationTask();
                break;
            case R.id.ivAddDevice://添加设备
                mRunnable = () -> {
                    bundle.putLong(IntentConstant.ID, CurrentHome.getLocalId());
                    switchToActivity(ScanDevice2Activity.class, bundle);
                };
                accessFineLocationTask();
                break;
            case R.id.tvMyHome://我的家
                showSelectHomeDialog();
                break;
            case R.id.ivRefresh:
            case R.id.tvRefresh://刷新
                refreshSocketConnect();
                break;
            case R.id.tvTips:  // 离线帮助
                if (!HomeUtil.tokenIsInvalid) {
                    Bundle offlineBundle = new Bundle();
                    offlineBundle.putInt(IntentConstant.WEB_URL_TYPE, 4);
                    switchToActivity(CommonWebActivity.class, offlineBundle);
                }
                break;

            case R.id.llTips:
                if (HomeUtil.tokenIsInvalid) {
                    switchToActivity(FindSAGuideActivity.class);
                }
                break;
        }
    }

    @Override
    protected void hasPermissionTodo() {
        super.hasPermissionTodo();
        if (mRunnable != null) {
            registerWifiReceiver();
            UiUtil.runInMainThread(mRunnable);
        }
    }

    /**
     * 显示家庭列表对话框
     */
    private void showSelectHomeDialog() {
        addDeviceP = false;
        if (CollectionUtil.isNotEmpty(mHomeList)) {
            for (HomeCompanyBean home : mHomeList) {
                home.setSelected(home.getLocalId() == CurrentHome.getLocalId() || (home.getArea_id() > 0 && home.getArea_id() == CurrentHome.getArea_id()));
            }
        }
        HomeSelectDialog homeSelectDialog = new HomeSelectDialog(mHomeList);
        homeSelectDialog.setClickItemListener(homeCompanyBean -> {
            setCurrentHome(homeCompanyBean, true, false);
            homeSelectDialog.dismiss();
        });
        homeSelectDialog.show(this);
    }

    /**
     * 刷新socket连接状态
     */
    private void refreshSocketConnect() {
        AnimationUtil.rotationAnim(ivRefresh, 500, R.drawable.icon_scene_refreshing, R.drawable.icon_scene_refresh);
        if (HomeUtil.isBindSA()) WSocketManager.getInstance().start();        LogUtil.e("WSocketManager4==");
    }

    private void initTabLayout(List<LocationBean> titles) {
        fragments.clear();
        for (int i = 0; i < titles.size(); i++) {
            //fragments.add(HomeItemFragment.getInstance(titles.get(i), i));
        }
        HomeFragmentPagerAdapter pagerAdapter = new HomeFragmentPagerAdapter(getChildFragmentManager(), fragments, titles);
        vpContent.setOffscreenPageLimit(titles.size());
        vpContent.setAdapter(pagerAdapter);

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
        setSelectTab(currentItem, true);
        loadDevice();
        if (HomeUtil.isHomeIdThanZero()) {
            HttpConfig.addHeader(CurrentHome.getSa_user_token());
            mPresenter.getDeviceList(needLoading);
        }
    }

    /**
     * @param position
     * @param select
     */
    private void setSelectTab(int position, boolean select) {
        if (tabLayout != null && tabLayout.getTabCount() > 0) {
            TabLayout.Tab tab = tabLayout.getTabAt(position);
            if (tab != null) {
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
    private void setCustomTabIcons(List<LocationBean> data) {
        for (int i = 0; i < data.size(); i++) {
            RelativeLayout view = (RelativeLayout) UiUtil.inflate(R.layout.item_tablayout);
            TextView tvText = view.findViewById(R.id.tvText);
            tvText.setText(data.get(i).getName());
            tabLayout.getTabAt(i).setCustomView(view);
        }
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * 获取家庭数据
     */
    private void loadData() {
        if (UserUtils.isLogin()) {
            mPresenter.getHomeList();
        } else {
            loadLocalData();
        }
    }

    /**
     * 获取本地数据
     */
    private void loadLocalData() {
        loadLocalData(false);
    }

    /**
     * 加载本地家庭
     *
     * @param isFailed
     */
    private void loadLocalData(boolean isFailed) {
        UiUtil.starThread(() -> {
            List<HomeCompanyBean> homeList = dbManager.queryHomeCompanyList();
            if (CollectionUtil.isEmpty(homeList)) {// 本地没有家庭，则创建一个
                HomeCompanyBean homeCompanyBean = new HomeCompanyBean(getResources().getString(R.string.main_my_home));
                homeCompanyBean.setSelected(true);
                homeCompanyBean.setArea_type(Constant.HOME_MODE);
                dbManager.insertHomeCompany(homeCompanyBean, null, false);
                homeList.add(homeCompanyBean);
            }
            handleHomeList(homeList, false, isFailed);
        });
    }

    /**
     * 处理家庭列表
     *
     * @param homeList      家庭列表
     * @param isRefreshData 重新刷新数据
     */
    private synchronized void handleHomeList(List<HomeCompanyBean> homeList, boolean isRefreshData, boolean isFailed) {
        if (CollectionUtil.isEmpty(homeList)) return;
        mHomeList.clear();
        mHomeList.addAll(homeList);

        //删除本地云端家庭数据
        if (UserUtils.isLogin() && isRefreshData) {
            int cloudUserId = UserUtils.getCloudUserId();
            dbManager.removeFamilyNotPresentUserFamily(cloudUserId);

            //本地家庭列表
            List<HomeCompanyBean> userHomeCompanyList = dbManager.queryHomeCompanyList();
            //用于存储本地已绑云的数据
            List<Long> cloudIdList = new ArrayList<>();
            //本地绑定SA,没有同步到云端数据
            List<Long> areaIdList = new ArrayList<>();
            for (HomeCompanyBean home : userHomeCompanyList) {
                cloudIdList.add(home.getId());
                if (home.getId() == 0 && home.isIs_bind_sa()) {
                    areaIdList.add(home.getArea_id());
                }
            }

            //用于存储从服务获取的数据
            List<Long> serverIdList = new ArrayList<>();
            //遍历从云端获取的数据是否已存在本地
            for (HomeCompanyBean home : homeList) {
                //为家庭设置用户id
                home.setCloud_user_id(cloudUserId);
                long homeId = home.getId();
                serverIdList.add(homeId);
                if (areaIdList.contains(homeId)) {
                    dbManager.removeFamilyByAreaId(homeId);
                }
                //已存在，更新
                if (cloudIdList.contains(homeId)) {
                    dbManager.updateHomeCompanyByCloudId(home);
                } else {//不存在，插入
                    if (home.isIs_bind_sa()) {
                        home.setArea_id(home.getId());
                    }
                    dbManager.insertCloudHomeCompany(home);
                }
            }

            //移除sc已删除的数据,如果云端数据还在，继续，如果云端数据不在，则删除本地数据
            for (Long id : cloudIdList) {
                if (id > 0 && !serverIdList.contains(id)) {
                    dbManager.removeFamilyByCloudId(id);
                }
            }
            mHomeList = dbManager.queryHomeCompanyList();
        }

        HomeCompanyBean tempHome = mHomeList.get(0);
        if (homeLocalId > 0) {//用于置回之前选择的家庭
            for (HomeCompanyBean homeCompanyBean : mHomeList) {
                if (homeCompanyBean.getLocalId() == homeLocalId) {
                    tempHome = homeCompanyBean;
                    break;
                }
            }
        } else if (wifiInfo != null) {
            for (HomeCompanyBean home : mHomeList) {
                if (home.getBSSID() != null && home.getBSSID().
                        equalsIgnoreCase(wifiInfo.getBSSID()) && home.isIs_bind_sa()) {//当前sa环境
                    tempHome = home;
                    break;
                }
            }
        }
        setCurrentHome(tempHome, true, isFailed);
    }

    /**
     * 设置当前选中的家庭
     *
     * @param home
     * @param isReconnect 是否重新连接
     */
    public void setCurrentHome(HomeCompanyBean home, boolean isReconnect, boolean isFailed) {
        if (home == null) return;
        if (home.isIs_bind_sa() && !hasAccessFineLocationPermission()) {
            mRunnable = () -> registerWifiReceiver();
            accessFineLocationTask();
        }
        currentItem = 0;
        hasLoadHomeList = true;
        HomeUtil.tokenIsInvalid = false;

        UiUtil.runInMainThread(() -> {
            CurrentHome = home;
            homeLocalId = home.getLocalId();
            Constant.AREA_TYPE = home.getArea_type();

            String saLanAddress = home.getSa_lan_address();
            HttpUrlConfig.baseSAUrl = TextUtils.isEmpty(saLanAddress) ? "" : saLanAddress;
            HttpUrlConfig.apiSAUrl = HttpUrlConfig.baseSAUrl + HttpUrlConfig.API;
            if (!TextUtils.isEmpty(saLanAddress)) {
                TempChannelUtil.baseSAUrl = saLanAddress + HttpUrlConfig.API;
            }

            HttpConfig.clearHeader();
            HttpConfig.addHeader(home.getSa_user_token());
            SpUtil.put(SpConstant.SA_TOKEN, home.getSa_user_token());
            SpUtil.put(SpConstant.AREA_ID, String.valueOf(home.getId()));
            SpUtil.put(SpConstant.IS_BIND_SA, home.isIs_bind_sa());
            SpUtil.put(SpConstant.SA_ID, home.getSa_id());

            //UDP发现SA
            if (home.isIs_bind_sa() && TextUtils.isEmpty(saLanAddress)) {
                EventBus.getDefault().post(new FourZeroFourEvent());
            }
            //绑定了SA && 没有同步到云端
            if (UserUtils.isLogin() && home.getArea_id() > 0 && home.getId() == 0) {
                AllRequestUtil.bindCloudWithoutCreateHome(home);
            }

            EventBus.getDefault().postSticky(new HomeSelectedEvent());

            ChannelUtil.refreshHomeTempeChannel();//重新获取临时通道
            queryRooms(home.getLocalId());//先显示缓存数据

            if (isFailed) return;
            handleTipStatus();

            if (home.isIs_bind_sa() && home.getArea_id() > 0 && home.getId() == home.getArea_id() && TextUtils.isEmpty(home.getBSSID()) && isReconnect) {//需要绑定SA才检查接口
                checkInterfaceEnabled();
            } else {//如果没有
                getRoomList(false);
            }
        });
    }

    private void checkInterfaceEnabled() {
        if (CurrentHome == null) return;
        if (TextUtils.isEmpty(CurrentHome.getSa_lan_address())) {
            getRoomList(false);
        } else {
            UiUtil.postDelayed(() -> AllRequestUtil.checkUrl500(CurrentHome.getSa_lan_address(), new AllRequestUtil.onCheckUrlListener() {// 检查地址是否可以连接
                @Override
                public void onSuccess() {  // 可以连接上
                    LogUtil.e("checkUrl===onSuccess");
                    handleDisconnect();
                }

                @Override
                public void onError() {  // 连接失败
                    LogUtil.e("checkUrl===onError");
                    hideLoadingView();
                    if (UserUtils.isLogin()) {
                        getRoomList(false);
                    }
                }
            }), 500);
        }
    }

    /**
     * 查房间
     */
    private void queryRooms(long id) {
        if (dbManager.isOpen()) {
            UiUtil.starThread(() -> {
                List<LocationBean> roomList = dbManager.queryLocationList(id);
                LocationBean defaultRoom = new LocationBean(UiUtil.getString(R.string.home_all));
                roomList.add(0, defaultRoom);
                UiUtil.runInMainThread(() -> {
                    initTabLayout(roomList);
                });
            });
        }
    }

    /**
     * 更新HomeBean
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HomeEvent event) {
        if (event.isRefreshAll()) {
            selectTab();
        } else {
            HomeCompanyBean tempHome = event.getHomeCompanyBean();
            if (event.isAdd()) mHomeList.add(tempHome);
            for (HomeCompanyBean bean : mHomeList) {
                if (bean != null) {
                    bean.setSelected(false);
                    if (bean.getLocalId() == tempHome.getLocalId()) {
                        bean.setSelected(true);
                        bean.setIs_bind_sa(tempHome.isIs_bind_sa());
                        bean.setArea_id(tempHome.getArea_id());
                        bean.setBSSID(tempHome.getBSSID());
                        bean.setSa_user_token(tempHome.getSa_user_token());
                        bean.setUser_id(tempHome.getUser_id());
                        bean.setSa_lan_address(tempHome.getSa_lan_address());
                        bean.setSa_id(tempHome.getSa_id());
                    }
                }
            }
            setCurrentHome(tempHome, true, false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DeviceRefreshEvent event) {
        mPresenter.getDeviceList(needLoading);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HomeSelectedEvent event) {
        tvMyHome.setText(CurrentHome.getName());
        if (event.isLoad())
            getRoomList(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshRoomListEvent event) {
        getRoomList(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshHome event) {
        if (UserUtils.isLogin()) {
            mPresenter.getHomeList();
        } else {
            loadLocalData();
        }
    }

    /**
     * 刷新家庭列表，更新对话框家庭列表
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshHomeList event) {
        String name = event.getName();
        if (!TextUtils.isEmpty(name)) {
            tvMyHome.setText(name);
        }
        mHomeList = dbManager.queryHomeCompanyList();
        if (!UserUtils.isLogin()) {
            queryRooms(CurrentHome.getLocalId());
        }
    }

    /**
     * 找回ip地址后
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AfterFindIPEvent event) {
        for (HomeCompanyBean homeCompanyBean : mHomeList) {
            if (homeCompanyBean.getLocalId() == CurrentHome.getLocalId()) {
                homeCompanyBean.setSa_lan_address(CurrentHome.getSa_lan_address());
                break;
            }
        }
        setCurrentHome(CurrentHome, true, false);
    }

    /**
     * 从服务器获取数据
     */
    private void getRoomList(boolean showLoading) {
        if (CurrentHome == null) return;
        needLoading = showLoading;
        if (HomeUtil.isHomeIdThanZero()) {//satoken不为空
            HttpConfig.addHeader(CurrentHome.getSa_user_token());
            if (NetworkUtil.isNetworkAvailable()) { //网络可用
                if (UserUtils.isLogin() || (HomeUtil.isBindSA() && !TextUtils.isEmpty(CurrentHome.getSa_lan_address()))) {
                    mPresenter.getDetail(HomeUtil.isSAEnvironment() ? CurrentHome.getArea_id() : CurrentHome.getId(), showLoading);
                } else {
                    queryRooms(CurrentHome.getLocalId());
                }
            } else {
                queryRooms(CurrentHome.getLocalId());
            }
        } else {
            HttpConfig.clearHeader();
            if (UserUtils.isLogin() && HomeUtil.getHomeId() > 0) {//登录了，从接口获取
                mPresenter.getRoomList(CurrentHome.getArea_type(), false);
            } else if (!UserUtils.isLogin()) {
                queryRooms(CurrentHome.getLocalId());
            }
        }
    }

    /**
     * 房间列表成功
     *
     * @param roomListBean
     */
    @Override
    public void getRoomListSuccess(RoomListBean roomListBean) {
        if (roomListBean != null) {
            //默认创建一个房间
            List<LocationBean> roomList = new ArrayList<>();
            LocationBean location = new LocationBean(UiUtil.getString(R.string.home_all));
            roomList.add(location);

            List<LocationBean> serverRoomList = null;
            if (CurrentHome.getArea_type() == 1 && CollectionUtil.isNotEmpty(roomListBean.getLocations())) {//家庭
                serverRoomList = roomListBean.getLocations();
                roomList.addAll(roomListBean.getLocations());
            } else if (CurrentHome.getArea_type() == 2 && CollectionUtil.isNotEmpty(roomListBean.getDepartments())) {//公司
                serverRoomList = roomListBean.getDepartments();
                roomList.addAll(roomListBean.getDepartments());
            }

            initTabLayout(roomList);
            saveRooms(serverRoomList);
        }
    }

    @Override
    public void getRoomListFailed(int errorCode, String msg) {

    }

    /**
     * 设备列表
     *
     * @param roomListBean
     */
    @Override
    public void getDeviceListSuccess(RoomDeviceListBean roomListBean) {
        if (roomListBean != null) {
            List<DeviceMultipleBean> deviceList = roomListBean.getDevices();
            if (CollectionUtil.isNotEmpty(deviceList)) {
                for (DeviceMultipleBean deviceMultipleBean : deviceList) {
                    deviceMultipleBean.setItemType(DeviceMultipleBean.DEVICE);
                }
            }
            EventBus.getDefault().post(new DeviceDataEvent(deviceList));
            saveDevices(deviceList);
        }
    }

    /**
     * 用户权限
     *
     * @param permissionBean
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean != null) {
            addDeviceP = permissionBean.getPermissions().isAdd_device();
            ivAddDevice.setVisibility(permissionBean.getPermissions().isAdd_device() ? View.VISIBLE : View.GONE);
            EventBus.getDefault().post(new PermissionEvent(permissionBean.getPermissions()));
        }
    }

    /**
     * 请求失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void requestFail(int errorCode, String msg) {
        EventBus.getDefault().post(new TempChannelFailEvent());
    }

    /**
     * 获取设备列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDeviceFail(int errorCode, String msg) {
        if (errorCode != 5012 && errorCode != 5027) {
            ToastUtil.show(msg);
        } else if (errorCode == -1) {
            loadDevice();
        } else if (errorCode == 5012 || errorCode == 5027) {
            EventBus.getDefault().post(new DeviceDataEvent(null));
        }
        EventBus.getDefault().post(new TempChannelFailEvent());
    }

    /**
     * 家庭详情
     *
     * @param homeCompanyBean
     */
    @Override
    public void getDetailSuccess(HomeCompanyBean homeCompanyBean) {
        if (homeCompanyBean == null) return;
        HttpConfig.addHeader(CurrentHome.getSa_user_token());
        EventBus.getDefault().post(new UpdateHeadImgEvent());
        tvMyHome.setText(homeCompanyBean.getName());
        if (HomeUtil.isHomeIdThanZero()) {
            mPresenter.getPermissions(CurrentHome.getUser_id());
        }
        mPresenter.getRoomList(CurrentHome.getArea_type(), false);
        CurrentHome.setName(homeCompanyBean.getName());
        for (HomeCompanyBean hc : mHomeList) {
            if (hc.getSa_user_token() != null)
                if (hc.getSa_user_token().equals(CurrentHome.getSa_user_token())) {
                    hc.setName(homeCompanyBean.getName());
                    break;
                }
        }
        if (UserUtils.isLogin() && HomeUtil.isHomeIdThanZero()) {
            EventBus.getDefault().post(new UpdateSaUserNameEvent());
        }
        UiUtil.starThread(() -> dbManager.updateHCNameByToken(CurrentHome.getSa_user_token(), homeCompanyBean.getName()));
    }

    /**
     * 家庭详情失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDetailFail(int errorCode, String msg) {
        if (errorCode == 5012 || errorCode == 5027) {
            if (UserUtils.isLogin()) { // 用户登录SC情况
                long areaId = HomeUtil.isSAEnvironment() ? CurrentHome.getArea_id() : CurrentHome.getId();
                NameValuePair nameValuePair = new NameValuePair("area_id", String.valueOf(areaId));
                List<NameValuePair> requestData = new ArrayList<>();
                requestData.add(nameValuePair);
                mPresenter.getSAToken(CurrentHome.getCloud_user_id(), requestData);//sc的用户id, sc上的家庭id
            } else {
                removeLocalFamily();
            }
        } else if (errorCode == 5003) {  // 被移除家庭
            removeLocalFamily();
        } else if (errorCode == 100001) {
            SpUtil.put(CurrentHome.getSa_user_token(), "");
            mPresenter.getDetail(CurrentHome.getId(), false);
        }
        EventBus.getDefault().post(new TempChannelFailEvent());
    }


    /**
     * 移除本地家庭
     */
    private void removeLocalFamily() {
        if (showRemoveDialog) {
            RemovedTipsDialog removedTipsDialog = new RemovedTipsDialog(String.format(UiUtil.getString(R.string.common_remove_home), CurrentHome.getName()));
            removedTipsDialog.setKnowListener(() -> showRemoveDialog = true);
            removedTipsDialog.show(this);
        }
        showRemoveDialog = false;
        dbManager.removeFamily(CurrentHome.getLocalId());
        loadLocalData();
    }

    /**
     * 获取家庭列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getHomeListFail(int errorCode, String msg) {
        if (errorCode == NetworkErrorConstant.LOGIN_INVALID_1 || errorCode == NetworkErrorConstant.LOGIN_INVALID_2) {//用户未登录/登录失效
            UserUtils.saveUser(null);
            PersistentCookieStore.getInstance().removeAll();
            EventBus.getDefault().post(new MineUserInfoEvent(false));
        } else if (errorCode != NetworkErrorConstant.PWD_MODIFIED) {
            ToastUtil.show(msg);
        }
        loadLocalData();
    }

    /**
     * 获取家庭列表成功
     *
     * @param areas
     */
    @Override
    public void getHomeListSuccess(List<HomeCompanyBean> areas) {
        if (CollectionUtil.isNotEmpty(areas)) {
            UiUtil.starThread(() -> handleHomeList(areas, true, false));
        } else {
            loadLocalData();
        }
    }

    /**
     * 通过sc找回sa的用户凭证成功
     *
     * @param findSATokenBean
     */
    @Override
    public void getSATokenSuccess(FindSATokenBean findSATokenBean) {
        if (findSATokenBean != null) {
            String saToken = findSATokenBean.getSa_token();
            if (!TextUtils.isEmpty(saToken)) {
                HomeUtil.tokenIsInvalid = false;
                CurrentHome.setSa_user_token(saToken);
                HttpConfig.addHeader(CurrentHome.getSa_user_token());
                mPresenter.getDetail(CurrentHome.getId(), false);
                UiUtil.starThread(() -> dbManager.updateSATokenByLocalId(CurrentHome.getLocalId(), saToken));
            }
        }
    }

    /**
     * 通过sc找回sa的用户凭证是失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getSATokenFail(int errorCode, String msg) {
        if (errorCode == 2011 || errorCode == 2010) {//凭证获取失败，状态码2011，无权限
            HomeUtil.tokenIsInvalid = true;
            setTipsRefreshVisible(false);
            llTips.setVisibility(View.VISIBLE);
            tvTips.setText(getResources().getString(R.string.home_invalid_token));
            EventBus.getDefault().post(new DeviceDataEvent(null));
        } else if (errorCode == 3002) {//状态码3002，提示被管理员移除家庭
            removeLocalFamily();
        } else {
            ToastUtil.show(msg);
        }
    }

    @Override
    public void onCheckSaAddressSuccess() {

    }

    @Override
    public void onCheckSaAddressFailed() {

    }

    @Override
    public void uploadAvatarSuccess(UploadFileBean uploadFileBean) {

    }

    @Override
    public void uploadAvatarFail(int errorCode, String msg) {

    }

    @Override
    public void updateMemberSuccess() {

    }

    @Override
    public void updateMemberFail(int errorCode, String msg) {

    }

    @Override
    public void getSACheckSuccess(CheckBindSaBean checkBindSaBean) {

    }

    @Override
    public void getSACheckFail(int errorCode, String msg) {

    }

    @Override
    public void getSupportApiSuccess(ApiVersionBean apiVersionBean) {

    }

    @Override
    public void getSupportApiFail(int errorCode, String msg) {

    }

    @Override
    public void getAppSupportApiSuccess(ApiVersionBean apiVersionBean) {

    }

    @Override
    public void getAppSupportApiFail(int errorCode, String msg) {

    }

    @Override
    public void getAppVersionInfoSuccess(AndroidAppVersionBean androidBean) {

    }

    @Override
    public void getAppVersionInfoFailed(int errorCode, String msg) {

    }

    /**
     * 加载本地设备列表
     */
    private void loadDevice() {
        if (CurrentHome.isIs_bind_sa()) {
            UiUtil.starThread(() -> {
                List<DeviceMultipleBean> deviceMultipleBeans = dbManager.queryDeviceListBySaToken(CurrentHome.getSa_user_token());
                UiUtil.runInMainThread(() -> {
                    if (CollectionUtil.isNotEmpty(deviceMultipleBeans)) {
                        for (DeviceMultipleBean deviceMultipleBean : deviceMultipleBeans) {
                            deviceMultipleBean.setItemType(DeviceMultipleBean.DEVICE);
                        }
                    }
                    EventBus.getDefault().post(new DeviceDataEvent(deviceMultipleBeans));
                });
            });
        } else {
            EventBus.getDefault().post(new DeviceDataEvent(null));
        }
    }

    /**
     * 保存房间数据
     */
    public void saveRooms(List<LocationBean> roomList) {
        if (CollectionUtil.isEmpty(roomList)) return;

        for (LocationBean locationBean : roomList) {
            locationBean.setLocationId(locationBean.getId());
            locationBean.setSa_user_token(CurrentHome.getSa_user_token());
            locationBean.setArea_id(CurrentHome.getLocalId());
        }
        UiUtil.starThread(() -> {
            dbManager.removeLocationBySaToken(CurrentHome.getSa_user_token());
            dbManager.insertLocationList(CurrentHome.getLocalId(), roomList);
        });
    }

    /**
     * 保存设备数据
     */
    public void saveDevices(List<DeviceMultipleBean> deviceList) {
        UiUtil.starThread(() -> {
            dbManager.removeDeviceBySaToken(CurrentHome.getSa_user_token());
            dbManager.insertDeviceList(deviceList, CurrentHome.getSa_user_token(), CurrentHome.getLocalId());
        });
    }

    @Override
    public void checkTokenFail(int errorCode, String msg, String homeName) {
        LogUtil.e("checkTokenFail==" + errorCode + ",msg=" + msg);
        if (errorCode == 5003 || errorCode == 3002) {
            RemovedTipsDialog removedTipsDialog = new RemovedTipsDialog(String.format(UiUtil.getString(R.string.common_remove_home), homeName));
            removedTipsDialog.setKnowListener(() -> showRemoveDialog = true);
            removedTipsDialog.show(this);
            UiUtil.starThread(() -> dbManager.removeFamily(CurrentHome.getLocalId()));
        }
    }

    @Override
    public void selectTab() {
        if (Constant.isUnregisterSuccess) {
            loadLocalData(false);
            Constant.isUnregisterSuccess = false;
            return;
        }
        if (CurrentHome!=null && UserUtils.isLogin()) {//登录sc情况
            mPresenter.checkToken(CurrentHome.getId(), CurrentHome.getName());
            mPresenter.getHomeList();
        } else {
            getRoomList(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeLocalId = 0;
        CurrentHome = null;
        hasLoadHomeList = false;
        HomeUtil.tokenIsInvalid = false;
        unRegisterWifiReceiver();
        if (mWebSocketListener != null) {
            WSocketManager.getInstance().removeWebSocketListener(mWebSocketListener);
        }
    }
}
