
package com.yctc.zhiting.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.NetworkUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.BaseFragment;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.event.TempChannelFailEvent;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.httputil.cookie.PersistentCookieStore;
import com.app.main.framework.widget.StatusBarView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.CaptureNewActivity;
import com.yctc.zhiting.activity.FindSAGuideActivity;
import com.yctc.zhiting.activity.MainActivity;
import com.yctc.zhiting.activity.ScanDevice2Activity;
import com.yctc.zhiting.adapter.HomeFragmentPagerAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.FindCertificateDialog;
import com.yctc.zhiting.dialog.HomeSelectDialog;
import com.yctc.zhiting.dialog.RemovedTipsDialog;
import com.yctc.zhiting.entity.AreaIdBean;
import com.yctc.zhiting.entity.FindCertificateBean;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RoomListBean;
import com.yctc.zhiting.event.AddSAEvent;
import com.yctc.zhiting.event.DeviceDataEvent;
import com.yctc.zhiting.event.DeviceRefreshEvent;
import com.yctc.zhiting.event.HomeEvent;
import com.yctc.zhiting.event.HomeSelectedEvent;
import com.yctc.zhiting.event.MineUserInfoEvent;
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
    public boolean isRetryBindSC = true;//绑定sc如果失败是否重新再绑定一次
    private boolean hasLoadHomeList = false; // 家庭列表是否加载过
    private boolean needLoadIfWifiEnabled = true;  // 接收wifi广播连接是否需要加载数据set
    private boolean needLoadIfWifiDisabled = true;  // 接收wifi广播断开是否需要加载数据
    private int currentItem;
    private boolean bindSc = false; // 是否在绑定云端

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
                bindSaHomeHideTips();
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
        setTipsRefreshVisible(true);
        if (!UserUtils.isLogin()) {
            llTips.setVisibility(showTip ? View.VISIBLE : View.GONE);
            EventBus.getDefault().post(new SocketStatusEvent(showTip));
        } else {
            llTips.setVisibility(View.GONE);
            EventBus.getDefault().post(new SocketStatusEvent(false));
        }
    }

    /**
     * 绑定SA&&不在SA环境&&没有登陆 显示
     */
    private void handleTipStatus1() {
        setTipsRefreshVisible(true);
        LogUtil.e("handleTipStatus1=" + CurrentHome);
        if (!UserUtils.isLogin()) {
            boolean isShowStatus = (HomeUtil.isHomeIdThanZero() && !HomeUtil.isSAEnvironment());
            llTips.setVisibility(isShowStatus ? View.VISIBLE : View.GONE);
            EventBus.getDefault().post(new SocketStatusEvent(isShowStatus));
        } else {
            llTips.setVisibility(View.GONE);
            EventBus.getDefault().post(new SocketStatusEvent(false));
        }
        if (CurrentHome.isIs_bind_sa())
            startConnectSocket();
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
                    if (CurrentHome != null)
                        handleTipStatus(CurrentHome.isIs_bind_sa());
//                    if (needLoadIfWifiDisabled) {
//                        needLoadIfWifiDisabled = false;
//                        handleDisconnect("");
//                    }
//                    needLoadIfWifiEnabled = true;
                    handleDisconnect("", false);
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        wifiInfo = wifiManager.getConnectionInfo();
//                        if (needLoadIfWifiEnabled) {
//                            needLoadIfWifiEnabled = false;
//                            if (CurrentHome != null && !TextUtils.isEmpty(CurrentHome.getSa_lan_address())) {
//                                if (!TextUtils.isEmpty(CurrentHome.getMac_address())) {
//                                    bindSaHomeHideTips();
//                                }
//                                if (hasLoadHomeList)
//                                    mPresenter.checkInterfaceEnable(CurrentHome.getSa_lan_address());
//                            }
//                        }
                        if (CurrentHome != null && !TextUtils.isEmpty(CurrentHome.getSa_lan_address())) {
                            if (!TextUtils.isEmpty(CurrentHome.getMac_address())) {
                                bindSaHomeHideTips();
                            }
                            if (hasLoadHomeList) {
//                                mPresenter.checkInterfaceEnable(CurrentHome.getSa_lan_address());

                                checkInterfaceEnabled();
                            }
                        }
                        //获取当前wifi名称
                        LogUtil.e(TAG, "网络=连接到网络1 " + wifiInfo.getSSID());
                        LogUtil.e(TAG, "网络=连接到网络2 " + GsonConverter.getGson().toJson(wifiInfo));
                        LogUtil.e(TAG, "网络=连接到网络3 " + wifiInfo.getBSSID());
                        LogUtil.e(TAG, "网络=连接到网络4 " + wifiInfo.getMacAddress());
                    }
                    needLoadIfWifiDisabled = true;
                }
                if (isFirstInit) {
                    isFirstInit = false;
                    loadData();
                }
            }
        }
    };

    /**
     * 隐藏显示连接sa
     */
    private void bindSaHomeHideTips() {
        if (CurrentHome != null && wifiInfo != null && !TextUtils.isEmpty(CurrentHome.getMac_address()) && !TextUtils.isEmpty(wifiInfo.getBSSID())) {
            boolean showTips = CurrentHome.isIs_bind_sa() ? !CurrentHome.getMac_address().equals(wifiInfo.getBSSID()) : false;
            handleTipStatus(showTips);
        }
    }

    /**
     * wifi断开就不是SA环境了
     */
    private void handleDisconnect(String address, boolean bindScToSa) {
        if (CurrentHome != null) {
            String macAddress = CurrentHome.getMac_address();
            if (wifiInfo != null) {
                if (TextUtils.isEmpty(macAddress)) {
                    if (!TextUtils.isEmpty(address)) {
                        CurrentHome.setMac_address(address);
                        dbManager.updateHomeMacAddress(CurrentHome.getLocalId(), address);
                    }
                } else {
                    if (!macAddress.equals(wifiInfo.getBSSID()))
                        CurrentHome.setMac_address("");
                }
            }
            // 需要绑sa且云id和said不同
//            if (bindScToSa && CurrentHome.getId()>0 &&CurrentHome.getArea_id()>0 && CurrentHome.getId() != CurrentHome.getArea_id())
//            scBindSa();
            getRoomList(false);
            if (!TextUtils.isEmpty(address)) {
                handleTipStatus1();
            } else {
                handleTipStatus(CurrentHome.isIs_bind_sa());
            }
            EventBus.getDefault().post(new UpdateProfessionStatusEvent());
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

    @OnClick({R.id.ivScan, R.id.ivAddDevice, R.id.tvMyHome, R.id.ivRefresh, R.id.tvRefresh, R.id.llTips})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivScan:  // 扫码
                switchToActivity(CaptureNewActivity.class);
                break;
            case R.id.ivAddDevice:  // 添加设备
                bundle.putLong(IntentConstant.ID, CurrentHome.getLocalId());
                switchToActivity(ScanDevice2Activity.class, bundle);
                break;
            case R.id.tvMyHome:  // 我的家
                showSelectHomeDialog();
                break;
            case R.id.ivRefresh:
            case R.id.tvRefresh:  // 刷新
                refreshSocketConnect();
                break;
            case R.id.llTips:
                if (HomeUtil.tokenIsInvalid) {
                    switchToActivity(FindSAGuideActivity.class);
                }
                break;
        }
    }

    /**
     * 显示家庭列表对话框
     */
    private void showSelectHomeDialog() {
        addDeviceP = false;
        if (CollectionUtil.isNotEmpty(mHomeList)) {
            for (HomeCompanyBean home : mHomeList) {
                home.setSelected(home.getLocalId() == CurrentHome.getLocalId());
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
        CurrentHome = null;
        hasLoadHomeList = false;
        HomeUtil.tokenIsInvalid = false;
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
                List<Long> cloudIdList = new ArrayList<>();
                for (HomeCompanyBean hcb : userHomeCompanyList) {
                    cloudIdList.add(hcb.getId());
                }

                // 用于存储从服务获取的数据
                List<Long> serverIdList = new ArrayList<>();
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
                        if (area.isIs_bind_sa()) {
                            area.setArea_id(area.getId());
                        }
                        dbManager.insertCloudHomeCompany(area);
                    }
                }

                // 移除sc已删除的数据
                for (Long id : cloudIdList) {
                    if (serverIdList.contains(id)) {  // 如果云端数据还在，继续
                        continue;
                    } else { // 如果云端数据不在，则删除本地数据
                        if (id>0)
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
            setCurrentHome(tempHome, true, isFailed);
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
    public void setCurrentHome(HomeCompanyBean home, boolean isReconnect, boolean isFailed) {
        hasLoadHomeList = true;
        if (home == null) return;
        HomeUtil.tokenIsInvalid = false;
        currentItem = 0;
        UiUtil.runInMainThread(() -> {
            CurrentHome = home;
            if (!TextUtils.isEmpty(home.getSa_lan_address())) {
                System.out.println("sa的地址：" + home.getSa_lan_address());
                HttpUrlConfig.baseSAUrl = home.getSa_lan_address();
                HttpUrlConfig.apiSAUrl = HttpUrlConfig.baseSAUrl + HttpUrlConfig.API;
            }
            homeLocalId = CurrentHome.getLocalId();

            if (UserUtils.isLogin() && CurrentHome.getArea_id()>0 && CurrentHome.getId() == 0){
                System.out.println("绑定云：第一次");
                AllRequestUtil.bindCloudWithoutCreateHome(CurrentHome, null);
            }

            HttpConfig.clearHeader();
            HttpConfig.addHeader(CurrentHome.getSa_user_token());
            SpUtil.put(SpConstant.SA_TOKEN, home.getSa_user_token());
            SpUtil.put(SpConstant.AREA_ID, String.valueOf(home.getId()));
            SpUtil.put(SpConstant.IS_BIND_SA, home.isIs_bind_sa());

            EventBus.getDefault().postSticky(new HomeSelectedEvent());
            ChannelUtil.reSaveChannel();//重新获取临时通道

            //先显示缓存数据
            queryRooms(CurrentHome.getLocalId());
            if (isFailed) return;
            String homeMacAddress = CurrentHome.getMac_address();
            if (wifiInfo != null) {
                handleTipStatus(home.isIs_bind_sa() && (TextUtils.isEmpty(homeMacAddress) ? true : !homeMacAddress.equals(wifiInfo.getBSSID())));
            } else {
                handleTipStatus(home.isIs_bind_sa());
            }

            if (home.isIs_bind_sa() && CurrentHome.getArea_id() > 0 && CurrentHome.getId() == CurrentHome.getArea_id() && TextUtils.isEmpty(CurrentHome.getMac_address()) && isReconnect) {//需要绑定SA才检查接口
//                handleDisconnect("", false);
//                mPresenter.checkInterfaceEnable(home.getSa_lan_address());
                LogUtil.e("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                checkInterfaceEnabled();

//                AllRequestUtil.bindCloudWithoutCreateHome(home, null);
            } else {//如果没有
                handleTipStatus1();
                LogUtil.e("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                getRoomList(false);
            }
        });
    }

    private void checkInterfaceEnabled(){
        if (CurrentHome!=null && !TextUtils.isEmpty(CurrentHome.getSa_lan_address()))
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AllRequestUtil.checkUrl500(CurrentHome.getSa_lan_address(), new AllRequestUtil.onCheckUrlListener() {// 检查地址是否可以连接
                            @Override
                            public void onSuccess() {  // 可以连接上
                                LogUtil.e("checkUrl===onSuccess");
                                String madAddress = "";
                                if (Constant.wifiInfo != null && Constant.wifiInfo.getBSSID() != null)
                                    madAddress = Constant.wifiInfo.getBSSID();
                                handleDisconnect(madAddress, true);
                            }

                            @Override
                            public void onError() {  // 连接失败
                                LogUtil.e("checkUrl===onError");
                                hideLoadingView();
                                bindSc = false;
                                if (UserUtils.isLogin()){
                                    getRoomList(false);
                                }
                            }
                        });
                    }
                }, 500);


    }

    // 如果本地家庭已绑sa，云端家庭没绑sa，则绑云端sa
    private synchronized void scBindSa() {
//        if (CurrentHome != null && wifiInfo != null && CurrentHome.getMac_address() != null && wifiInfo.getBSSID() != null && !CurrentHome.getMac_address().equals(wifiInfo.getBSSID()))
//            return;
                if (bindSc)
                UiUtil.postDelayed(() -> {
                    LogUtil.e("开始绑定云sa1");
//                    if (UserUtils.isLogin() && CurrentHome != null && HomeUtil.isSAEnvironment() && CollectionUtil.isNotEmpty(unBindHomes)) {
//                        for (HomeCompanyBean unbindHome : unBindHomes) {
//                            if (CurrentHome.getId() == unbindHome.getId()) {
//                                LogUtil.e("开始绑定云sa2");
//                                BindCloudRequest request = new BindCloudRequest();
//                                request.setCloud_area_id(String.valueOf(CurrentHome.getId()));
//                                request.setCloud_user_id(CurrentHome.getCloud_user_id());
//                                HttpConfig.clearHear(HttpConfig.AREA_ID);
//                                mPresenter.scBindSA(request.toString());
//                                unbindHome.setIs_bind_sa(true);
//                                break;
//                            }
//                        }

//                    }
                    if (CurrentHome.isIs_bind_sa()){
                        CurrentHome.setCloud_user_id(UserUtils.getCloudUserId());
                        System.out.println("绑定云：第二次");
                        AllRequestUtil.bindCloudWithoutCreateHome(CurrentHome, null);
                        bindSc = false;
                    }
                }, 500);


    }

    /**
     * 查房间
     */
    private void queryRooms(long id) {
        if (dbManager.isOpen()) {
            UiUtil.starThread(() -> {
                List<LocationBean> roomList = dbManager.queryLocationList(id);
                LocationBean defaultRoom = new LocationBean(0, UiUtil.getString(R.string.home_all));
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
        setCurrentHome(tempHome, true, false);
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
     * 成功添加SA
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddSAEvent event) {
        FindCertificateDialog dialog = FindCertificateDialog.newInstance();
        dialog.setDialogListener((isAllow) -> {
            FindCertificateBean.FindCertificateItemBean bean =
                    new FindCertificateBean.FindCertificateItemBean();
            bean.setUser_credential_found(isAllow);
            mPresenter.putFindCertificate(bean);
        });
        dialog.show(this);
    }

    /**
     * 从服务器获取数据
     */
    private void getRoomList(boolean showLoading) {
        if (CurrentHome == null) return;
        needLoading = showLoading;
        if (HomeUtil.isHomeIdThanZero()) {//satoken不为空
            HttpConfig.addHeader(CurrentHome.getSa_user_token());
            long areaId = CurrentHome.getArea_id();
            if (NetworkUtil.isNetworkAvailable()) {
                mPresenter.getDetail(HomeUtil.isSAEnvironment() ? areaId : CurrentHome.getId(), showLoading);
            }
        } else {
            HttpConfig.clearHeader();
            if (UserUtils.isLogin() && HomeUtil.getHomeId() > 0) {//登录了，从接口获取
                mPresenter.getRoomList(false);
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
        if (errorCode != 5012)
            ToastUtil.show(msg);
        if (errorCode == -1) {
            loadDevice();
        } else if (errorCode == 5012) {
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
        tvMyHome.setText(homeCompanyBean.getName());
        if (HomeUtil.isHomeIdThanZero()) {
            mPresenter.getPermissions(CurrentHome.getUser_id());
        }
        mPresenter.getRoomList(false);
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
        if (errorCode == 5012) {
            if (UserUtils.isLogin()) { // 用户登录SC情况
                NameValuePair nameValuePair = new NameValuePair("area_id", String.valueOf(HomeUtil.isSAEnvironment() ? CurrentHome.getArea_id() : CurrentHome.getId()));
                List<NameValuePair> requestData = new ArrayList<>();
                requestData.add(nameValuePair);
                mPresenter.getSAToken(CurrentHome.getCloud_user_id(), requestData);  // sc的用户id, sc上的家庭id
            } else {
                removeLocalFamily();
            }
        }else if (errorCode == 5003){  // 被移除家庭
            removeLocalFamily();
        }else {
//            loadLocalData(true);
        }
        EventBus.getDefault().post(new TempChannelFailEvent());
    }

    /**
     * 移除本地家庭
     */
    private void removeLocalFamily() {
        if (showRemoveDialog) {
            RemovedTipsDialog removedTipsDialog = new RemovedTipsDialog(CurrentHome.getName());
            removedTipsDialog.setKnowListener(() -> showRemoveDialog = true);
            removedTipsDialog.show(this);
        }
        showRemoveDialog = false;
        dbManager.removeFamily(CurrentHome.getLocalId());
        loadLocalData();
    }

    /**
     * 当前接口可用
     */
    @Override
    public void checkSuccess() {
        String madAddress = "";
        if (Constant.wifiInfo != null && Constant.wifiInfo.getBSSID() != null)
            madAddress = Constant.wifiInfo.getBSSID();
        handleDisconnect(madAddress, true);

    }

    /**
     * 当前接口不可用
     */
    @Override
    public void checkFail(int errorCode, String msg) {
        handleDisconnect("", false);
    }

    /**
     * 获取家庭列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getHomeListFail(int errorCode, String msg) {
        if (errorCode == 2008) {  // 用户未登录/登录失效
            UserUtils.saveUser(null);
            PersistentCookieStore.getInstance().removeAll();
            EventBus.getDefault().post(new MineUserInfoEvent(false));
        } else {
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
     * sc绑sa成功
     */
    @Override
    public void scBindSASuccess(AreaIdBean areaIdBean) {
        LogUtil.e("scBindSASuccess===========成功");
        if (areaIdBean != null) {
            UiUtil.starThread(() -> dbManager.updateHCAreaId(CurrentHome.getLocalId(), areaIdBean.getArea_id(), true));
        }
    }

    /**
     * sc绑sa失败
     */
    @Override
    public void scBindSAFail(int errorCode, String msg) {
        LogUtil.e("scBindSAFail" + msg);
        bindSc = false;
        CurrentHome.setMac_address("");
        if (isRetryBindSC) {
            isRetryBindSC = false;
//            scBindSa();
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
        if (errorCode == 2011 || errorCode == 2010) {    //凭证获取失败，状态码2011，无权限
            HomeUtil.tokenIsInvalid = true;
            setTipsRefreshVisible(false);
            llTips.setVisibility(View.VISIBLE);
            tvTips.setText(getResources().getString(R.string.home_invalid_token));
            EventBus.getDefault().post(new DeviceDataEvent(null));
        } else if (errorCode == 3002) {  //状态码3002，提示被管理员移除家庭
            removeLocalFamily();
        } else {
            ToastUtil.show(msg);
        }
    }

    @Override
    public void onCertificateSuccess() {

    }

    @Override
    public void onCertificateFail(int errorCode, String msg) {

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
