package com.yctc.zhiting.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BaseFragment;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.widget.StatusBarView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.CaptureNewActivity;
import com.yctc.zhiting.activity.MainActivity;
import com.yctc.zhiting.activity.ScanDeviceActivity;
import com.yctc.zhiting.adapter.HomeFragmentPagerAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.HomeSelectDialog;
import com.yctc.zhiting.dialog.RemovedTipsDialog;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;
import com.yctc.zhiting.event.DeviceDataEvent;
import com.yctc.zhiting.event.DeviceRefreshEvent;
import com.yctc.zhiting.event.HomeEvent;
import com.yctc.zhiting.event.HomeSelectedEvent;
import com.yctc.zhiting.event.PermissionEvent;
import com.yctc.zhiting.event.RefreshHome;
import com.yctc.zhiting.event.RefreshHomeList;
import com.yctc.zhiting.event.SocketStatusEvent;
import com.yctc.zhiting.event.UpdateProfessionStatusEvent;
import com.yctc.zhiting.event.UpdateSaUserNameEvent;
import com.yctc.zhiting.fragment.contract.HomeFragmentContract;
import com.yctc.zhiting.fragment.presenter.HomeFragmentPresenter;
import com.yctc.zhiting.listener.IHomeView;
import com.yctc.zhiting.receiver.WifiReceiver;
import com.yctc.zhiting.request.BindCloudRequest;
import com.yctc.zhiting.utils.AnimationUtil;
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

import static com.yctc.zhiting.config.Constant.CurrentHome;
import static com.yctc.zhiting.config.Constant.wifiInfo;

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
    @BindView(R.id.ivAddDevice)
    ImageView ivAddDevice;
    @BindView(R.id.llTips)
    LinearLayout llTips;
    @BindView(R.id.ivRefresh)
    ImageView ivRefresh;

    public static boolean addDeviceP = false; // 添加设备权限
    public static List<HomeCompanyBean> mHomeList = new ArrayList<>();
    private List<BaseFragment> fragments = new ArrayList<>();
    private List<HomeCompanyBean> unBindHomes = new ArrayList<>(); // 用于存储云端未绑sa的数据
    private DBManager dbManager;
    private WeakReference<Context> mContext;
    private boolean isFirstInit = true;//是否第一次进入
    private boolean needLoading;
    private boolean showRemoveDialog = true;
    public static long homeLocalId;  // 当前家庭本地id值
    private IWebSocketListener mWebSocketListener;

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
            int scrollRangle = appBarLayout.getTotalScrollRange();
            sbView.setBackgroundColor(changeAlpha(getResources().getColor(R.color.white), Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange()));
            appBarLayout.setBackgroundColor(changeAlpha(getResources().getColor(R.color.white), Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange()));
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
                handleTipStatus(false);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                //setCurrentHome(CurrentHome, false);
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
     * socket的显示状态控制
     *
     * @param showTip
     */
    private void handleTipStatus(boolean showTip) {
        //llTips.setVisibility(showTip ? View.VISIBLE : View.GONE);
        //EventBus.getDefault().post(new SocketStatusEvent(showTip));
    }

    /**
     * 绑定SA&&不在SA环境&&没有登陆 显示
     */
    private void handleTipStatus1() {
        LogUtil.e("handleTipStatus1=" + CurrentHome);
        boolean isShowStatus = (HomeUtil.isBindSA() && !HomeUtil.isSAEnvironment() && !UserUtils.isLogin());
        llTips.setVisibility(isShowStatus ? View.VISIBLE : View.GONE);
        EventBus.getDefault().post(new SocketStatusEvent(isShowStatus));
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
                    wifiInfo = null;
                    LogUtil.e(TAG, "网络=wifi断开");
                    handleTipStatus(true);
                    handleDisconnect("");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        wifiInfo = wifiManager.getConnectionInfo();
                        if (CurrentHome != null && !TextUtils.isEmpty(CurrentHome.getSa_lan_address())) {
                            mPresenter.checkInterfaceEnable(CurrentHome.getSa_lan_address());
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
    private void handleDisconnect(String address) {
        if (CurrentHome != null) {
            CurrentHome.setMac_address(address);
            getRoomList(false);
            handleTipStatus1();
            startConnectSocket();
        }
    }

    /**
     * 开始连接socket
     */
    private void startConnectSocket() {
        if (!WSocketManager.isConnecting && CurrentHome != null && CurrentHome.isIs_bind_sa()) {
            WSocketManager.getInstance().start();
            UiUtil.postDelayed(() -> {
                if (!WSocketManager.isConnecting)
                    WSocketManager.getInstance().start();
            }, 2000);
        }
    }

    @OnClick({R.id.ivScan, R.id.ivAddDevice, R.id.tvMyHome, R.id.ivRefresh, R.id.tvRefresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivScan:  // 扫码
                switchToActivity(CaptureNewActivity.class);
                break;
            case R.id.ivAddDevice:  // 添加设备
                bundle.putLong(IntentConstant.ID, CurrentHome.getLocalId());
                switchToActivity(ScanDeviceActivity.class, bundle);
                break;
            case R.id.tvMyHome:  // 我的家
                showSelectHomeDialog();
                break;
            case R.id.ivRefresh:
            case R.id.tvRefresh:  // 刷新
                refreshSocketConnect();
                break;
        }
    }

    /**
     * 显示家庭列表对话框
     */
    private void showSelectHomeDialog() {
        addDeviceP = false;
        HomeSelectDialog homeSelectDialog = new HomeSelectDialog(mHomeList);
        homeSelectDialog.setClickItemListener(homeCompanyBean -> {
            setCurrentHome(homeCompanyBean, true);
            homeSelectDialog.dismiss();
        });
        homeSelectDialog.show(this);
    }

    /**
     * 刷新socket连接状态
     */
    private void refreshSocketConnect() {
        AnimationUtil.rotationAnim(ivRefresh, 500, R.drawable.icon_scene_refreshing, R.drawable.icon_scene_refresh);
        if (CurrentHome != null && CurrentHome.isIs_bind_sa())
            WSocketManager.getInstance().start();
    }

    private void initTabLayout(List<LocationBean> titles) {
        fragments.clear();
        for (int i = 0; i < titles.size(); i++) {
            fragments.add(HomeItemFragment.getInstance(titles.get(i), i));
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
        setSelectTab(0, true);
        loadDevice();
        if (HomeUtil.isBindSA()){
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
            RelativeLayout view = (RelativeLayout) tabLayout.getTabAt(position).getCustomView();
            if (view != null) {
                TextView tvText = view.findViewById(R.id.tvText);
                View indicator = view.findViewById(R.id.indicator);
                if (select) {
                    tabLayout.getTabAt(position).select();
                    indicator.setVisibility(View.VISIBLE);
                    tvText.setTextColor(UiUtil.getColor(R.color.appPurple));
                } else {
                    indicator.setVisibility(View.INVISIBLE);
                    tvText.setTextColor(UiUtil.getColor(R.color.color_94a5be));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeLocalId = 0;
        unRegisterWifiReceiver();
        if (mWebSocketListener != null) {
            WSocketManager.getInstance().removeWebSocketListener(mWebSocketListener);
        }
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
        UiUtil.starThread(() -> {
            List<HomeCompanyBean> homeList = dbManager.queryHomeCompanyList();
            handleHomeList(homeList, false);
        });
    }

    /**
     * 处理家庭列表
     *
     * @param homeList      家庭列表
     * @param isRefreshData 重新刷新数据
     */
    private void handleHomeList(List<HomeCompanyBean> homeList, boolean isRefreshData) {
        if (CollectionUtil.isNotEmpty(homeList)) {
            mHomeList.clear();
            mHomeList.addAll(homeList);

            //删除本地云端家庭数据
            if (UserUtils.isLogin() && isRefreshData) {
                dbManager.removeFamilyNotPresentUserFamily(UserUtils.getCloudUserId());
                int cloudUserId = UserUtils.getCloudUserId();
                for (HomeCompanyBean homeBean : homeList) {
                    homeBean.setCloud_user_id(cloudUserId);
                }
                List<HomeCompanyBean> userHomeCompanyList = dbManager.queryHomeCompanyListByCloudUserId(cloudUserId);
                // 用于存储本地已绑云的数据
                List<Integer> cloudIdList = new ArrayList<>();
                for (HomeCompanyBean hcb : userHomeCompanyList) {
                    cloudIdList.add(hcb.getId());
                }

                // 用于存储从服务获取的数据
                List<Integer> serverIdList = new ArrayList<>();
                // 遍历从云端获取的数据是否已存在本地
                unBindHomes.clear();
                for (HomeCompanyBean area : homeList) {
                    serverIdList.add(area.getId());
                    if (!area.isIs_bind_sa()) {
                        unBindHomes.add(area);
                    }
                    // 已存在，更新
                    if (cloudIdList.contains(area.getId())) {
                        dbManager.updateHomeCompanyByCloudId(area.getId(), area.getName());
                    } else { // 不存在，插入
                        dbManager.insertCloudHomeCompany(area);
                    }
                }

                // 移除sc已删除的数据
                for (Integer id : cloudIdList) {
                    if (serverIdList.contains(id)) {  // 如果云端数据还在，继续
                        continue;
                    } else { // 如果云端数据不在，则删除本地数据
                        dbManager.removeFamilyByCloudId(id);
                    }
                }

                mHomeList = dbManager.queryHomeCompanyList();
            }
            HomeCompanyBean tempHome = mHomeList.get(0);

            if (wifiInfo != null) {
                for (HomeCompanyBean home : mHomeList) {
                    if (home.getMac_address() != null && home.getMac_address().
                            equalsIgnoreCase(wifiInfo.getBSSID()) && home.isIs_bind_sa()) { // 当前sa环境
                        tempHome = home;
                        break;
                    }
                }
            }

            if (homeLocalId > 0) {  // 用于置回之前选择的家庭
                for (HomeCompanyBean homeCompanyBean : mHomeList) {
                    if (homeCompanyBean.getLocalId() == homeLocalId) {
                        tempHome = homeCompanyBean;
                        break;
                    }
                }
            }
            CurrentHome = tempHome;
            setCurrentHome(tempHome, true);
            if (getActivity() != null) {
                ((MainActivity) getActivity()).toAuth();
            }
        }
    }

    /**
     * 设置当前选中的家庭
     *
     * @param home
     * @param isReconnect 是否重新连接
     */
    public void setCurrentHome(HomeCompanyBean home, boolean isReconnect) {
        if (home == null) return;
        UiUtil.runInMainThread(() -> {
            CurrentHome = home;
            homeLocalId = CurrentHome.getLocalId();
            HttpConfig.addHeader(CurrentHome.getSa_user_token());
            SpUtil.put(SpConstant.SA_TOKEN, home.getSa_user_token());
            EventBus.getDefault().post(new HomeSelectedEvent());

            //先显示缓存数据
            queryRooms(CurrentHome.getLocalId());
            handleTipStatus(home.isIs_bind_sa());
            if (home.isIs_bind_sa() && isReconnect) {//需要绑定SA才检查接口
                mPresenter.checkInterfaceEnable(home.getSa_lan_address());
            } else {//如果没有
                handleTipStatus1();
                getRoomList(false);
            }
        });
    }

    // sc绑sa
    private void scBindSa() {
        // 如果本地家庭已绑sa，云端家庭没绑sa，则绑云端sa
        LogUtil.e("开始绑定云sa1");
        if (UserUtils.isLogin() && CurrentHome != null && HomeUtil.isSAEnvironment() && CollectionUtil.isNotEmpty(unBindHomes)) {
            for (HomeCompanyBean unbindHome : unBindHomes) {
                if (CurrentHome.getId() == unbindHome.getId()) {
                    LogUtil.e("开始绑定云sa2");
                    BindCloudRequest request = new BindCloudRequest();
                    request.setCloud_area_id(CurrentHome.getId());
                    request.setCloud_user_id(CurrentHome.getCloud_user_id());
                    mPresenter.scBindSA(request.toString());
                    unbindHome.setIs_bind_sa(true);
                    break;
                }
            }
        }
    }

    /**
     * 查房间
     */
    private void queryRooms(long id) {
        UiUtil.starThread(() -> {
            List<LocationBean> roomList = dbManager.queryLocationList(id);
            LocationBean defaultRoom = new LocationBean(0, UiUtil.getString(R.string.home_all));
            roomList.add(0, defaultRoom);
            UiUtil.runInMainThread(() -> {
                initTabLayout(roomList);
            });
        });
    }

    /**
     * 更新HomeBean
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(HomeEvent event) {
        HomeCompanyBean tempHome = event.getHomeCompanyBean();
        if (event.isAdd()) mHomeList.add(tempHome);
        for (HomeCompanyBean bean : mHomeList) {
            if (bean != null) {
                bean.setSelected(false);
                String localSaToken = bean.getSa_user_token();
                if (localSaToken != null && !TextUtils.isEmpty(localSaToken)) {
                    boolean isSelect = localSaToken.equals(tempHome.getSa_user_token());
                    bean.setSelected(isSelect);
                }
            }
        }
        setCurrentHome(tempHome, true);
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
    public void onMessageEvent(RefreshHome event) {
        if (UserUtils.isLogin()){
            mPresenter.getHomeList();
        }else {
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
        if (!UserUtils.isLogin()){
            queryRooms(CurrentHome.getLocalId());
        }
    }

    /**
     * 从服务器获取数据
     */
    private void getRoomList(boolean showLoading) {
        if (CurrentHome == null) return;
        needLoading = showLoading;
        if (HomeUtil.isBindSA()) {//satoken不为空
            HttpConfig.addHeader(CurrentHome.getSa_user_token());
            mPresenter.getPermissions(CurrentHome.getUser_id());
            mPresenter.getDetail(1, showLoading);
        } else {
            HttpConfig.clearHeader();
            if (UserUtils.isLogin() && HomeUtil.getHomeId() > 0) {//登录了，从接口获取
                mPresenter.getRoomList(false);
            }else if (!UserUtils.isLogin()){
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
            List<LocationBean> roomData = new ArrayList<>();
            roomData.add(new LocationBean(0, getResources().getString(R.string.home_all)));
            for (LocationBean locationBean : roomListBean.getLocations()) {
                locationBean.setLocationId(locationBean.getId());
            }
            roomData.addAll(roomListBean.getLocations());
            initTabLayout(roomData);
            saveRooms(roomListBean.getLocations());
        }
    }

    /**
     * 设备列表
     *
     * @param roomListBean
     */
    @Override
    public void getDeviceListSuccess(RoomDeviceListBean roomListBean) {
        if (roomListBean != null) {
            List<DeviceMultipleBean> mDeviceList = roomListBean.getDevices();
            if (CollectionUtil.isNotEmpty(mDeviceList)) {
                for (DeviceMultipleBean deviceMultipleBean : mDeviceList) {
                    deviceMultipleBean.setItemType(DeviceMultipleBean.DEVICE);
                }
            }
            EventBus.getDefault().post(new DeviceDataEvent(mDeviceList));
            saveDevices(mDeviceList);
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
    }

    /**
     * 获取设备列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDeviceFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        if (errorCode == -1) {
            loadDevice();
        } else if (errorCode == 5012) {
            EventBus.getDefault().post(new DeviceDataEvent(null));
        }
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
        tvMyHome.setText(homeCompanyBean.getName());
        mPresenter.getRoomList(false);
        CurrentHome.setName(homeCompanyBean.getName());
        for (HomeCompanyBean hc : mHomeList) {
            if (hc.getSa_user_token() != null)
                if (hc.getSa_user_token().equals(CurrentHome.getSa_user_token())) {
                    hc.setName(homeCompanyBean.getName());
                    break;
                }
        }
        if (UserUtils.isLogin() && HomeUtil.isBindSA()) {
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
        if (errorCode == 5012) {
            if (showRemoveDialog) {
                RemovedTipsDialog removedTipsDialog = new RemovedTipsDialog(CurrentHome.getName());
                removedTipsDialog.setKnowListener(() -> showRemoveDialog = true);
                removedTipsDialog.show(this);
            }
            showRemoveDialog = false;
            dbManager.removeFamilyByToken(CurrentHome.getSa_user_token());
            loadLocalData();
        }
    }

    /**
     * 当前接口可用
     */
    @Override
    public void checkSuccess() {
        String madAddress = "";
        if (Constant.wifiInfo != null && Constant.wifiInfo.getBSSID() != null)
            madAddress = Constant.wifiInfo.getBSSID();
        handleDisconnect(madAddress);
        scBindSa();
    }

    /**
     * 当前接口不可用
     */
    @Override
    public void checkFail(int errorCode, String msg) {
        handleDisconnect("");
    }

    /**
     * 获取家庭列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getHomeListFail(int errorCode, String msg) {
        ToastUtil.show(msg);
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
            UiUtil.starThread(() -> handleHomeList(areas, true));
        } else {
            loadLocalData();
        }
    }

    /**
     * sc绑sa成功
     */
    @Override
    public void scBindSASuccess() {
        LogUtil.e("scBindSASuccess===========成功");
    }

    /**
     * sc绑sa失败
     */
    @Override
    public void scBindSAFail(int errorCode, String msg) {
        LogUtil.e("scBindSAFail" + msg);
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
    public void saveRooms(List<LocationBean> locationBeans) {
        for (LocationBean locationBean : locationBeans) {
            locationBean.setSa_user_token(CurrentHome.getSa_user_token());
            locationBean.setArea_id(CurrentHome.getLocalId());
        }
        UiUtil.starThread(() -> {
            dbManager.removeLocationBySaToken(CurrentHome.getSa_user_token());
            dbManager.insertLocationList(CurrentHome.getLocalId(), locationBeans);
        });
    }

    /**
     * 保存设备数据
     */
    public void saveDevices(List<DeviceMultipleBean> locationBeans) {
        UiUtil.starThread(() -> {
            dbManager.removeDeviceBySaToken(CurrentHome.getSa_user_token());
            dbManager.insertDeviceList(locationBeans, CurrentHome.getSa_user_token(), CurrentHome.getLocalId());
        });
    }

    @Override
    public void selectTab() {
        if (UserUtils.isLogin()) {  // 登录sc情况
            mPresenter.getHomeList();
        } else {
            getRoomList(true);
        }
    }
}
