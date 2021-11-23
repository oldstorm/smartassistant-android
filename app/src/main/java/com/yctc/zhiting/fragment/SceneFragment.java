package com.yctc.zhiting.fragment;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.FindSAGuideActivity;
import com.yctc.zhiting.activity.HowCreateSceneActivity;
import com.yctc.zhiting.activity.LogActivity;
import com.yctc.zhiting.activity.SceneDetailActivity;
import com.yctc.zhiting.adapter.SceneAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.HomeSelectDialog;
import com.yctc.zhiting.dialog.RemovedTipsDialog;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.scene.SceneBean;
import com.yctc.zhiting.entity.scene.SceneListBean;
import com.yctc.zhiting.event.HomeSelectedEvent;
import com.yctc.zhiting.event.PermissionEvent;
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

/**
 * 首页-场景
 */
public class SceneFragment extends MVPBaseFragment<SceneFragmentContract.View, SceneFragmentPresenter> implements
        SceneFragmentContract.View, ISceneView {

    @BindView(R.id.ivRefresh)
    ImageView ivRefresh;
    @BindView(R.id.ivReconnect)
    ImageView ivReconnect;
    @BindView(R.id.rvManual)
    RecyclerView rvManual;
    @BindView(R.id.rvAutomatic)
    RecyclerView rvAutomatic;
    @BindView(R.id.viewData)
    View viewData;
    @BindView(R.id.viewEmpty)
    View viewEmpty;
    @BindView(R.id.tvManual)
    TextView tvManual;
    @BindView(R.id.tvAutomatic)
    TextView tvAutomatic;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
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

    private SceneAdapter manualAdapter;  // 手动
    private SceneAdapter automaticAdapter; // 自动

    private int type; //区分手动和自动提示文案
    private boolean checked; //区分自动开关提示文案
    private boolean hasAddP; //添加场景权限
    private boolean hasUpdateP; // 修改场景的权限
    private boolean hasDelP; // 删除场景的权限

    private WeakReference<Context> mContext;
    private DBManager dbManager;
    private Handler mainThreadHandler;
    private boolean notFirst;

    private boolean hasLocal;
    private boolean showLoading;

    private IWebSocketListener mWebSocketListener;

    private boolean needLoadData = true; // 是否加载数据


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
        mainThreadHandler = new Handler(Looper.getMainLooper());
        initRvManual();
        initRvAutomatic();
        initWebSocket();
        showSaStatus();
        refreshLayout.setOnRefreshListener(refreshLayout -> getData(false));
        setRefreshAndLoadMore();
    }

    /**
     * 设置刷新/加载更多
     */
    private void setRefreshAndLoadMore() {
        refreshLayout.setEnableRefresh(!HomeUtil.tokenIsInvalid);
    }

    /**
     * sa状态
     */
    private void showSaStatus() {
        String homeMacAddress = "";
        if (CurrentHome != null && !TextUtils.isEmpty(CurrentHome.getMac_address()))
            homeMacAddress = CurrentHome.getMac_address();
        if (wifiInfo != null) {
            handleTipStatus(CurrentHome.isIs_bind_sa() && (TextUtils.isEmpty(homeMacAddress) ? true : !homeMacAddress.equals(wifiInfo.getBSSID())));
        } else {
            handleTipStatus(CurrentHome.isIs_bind_sa());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (notFirst) {
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
        setRefreshAndLoadMore();
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
        viewData.setVisibility(View.GONE);
        viewEmpty.setVisibility(View.GONE);
        rlInvalid.setVisibility(View.VISIBLE);
    }

    /**
     * 手动
     */
    private void initRvManual() {
        rvManual.setLayoutManager(new LinearLayoutManager(getContext()));
        manualAdapter = new SceneAdapter(0);
        rvManual.setAdapter(manualAdapter);

        manualAdapter.setOnItemClickListener((adapter, view, position) -> {
            SceneBean sceneBean = manualAdapter.getItem(position);
            if ((WSocketManager.isConnecting && HomeUtil.isSAEnvironment()) || UserUtils.isLogin()) {
                if (hasUpdateP) { // 有权限才进
                    if (HomeUtil.notLoginAndSAEnvironment()) {
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt(IntentConstant.ID, sceneBean.getId());
                    bundle.putBoolean(IntentConstant.REMOVE_SCENE, hasDelP);
                    switchToActivity(SceneDetailActivity.class, bundle);
                } else {
                    ToastUtil.show(getResources().getString(R.string.scene_no_modify_permission));
                }
            }
        });
        manualAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.tvPerform) { // 执行
                if (manualAdapter.getItem(position).isControl_permission()) {
                    type = 0;
                    manualAdapter.getItem(position).setPerforming(true);
                    manualAdapter.notifyItemChanged(position);
                    mPresenter.perform(manualAdapter.getItem(position).getId(), true);
                } else {
                    ToastUtil.show(getResources().getString(R.string.scene_no_control_permission));
                }
            }
        });
    }

    /**
     * 自动
     */
    private void initRvAutomatic() {
        rvAutomatic.setLayoutManager(new LinearLayoutManager(getContext()));
        automaticAdapter = new SceneAdapter(1);
        rvAutomatic.setAdapter(automaticAdapter);
        automaticAdapter.setOnItemClickListener((adapter, view, position) -> {
            SceneBean sceneBean = automaticAdapter.getItem(position);
            if (WSocketManager.isConnecting) {
                if (hasUpdateP) { // 有权限才进
                    if (HomeUtil.notLoginAndSAEnvironment()) {
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt(IntentConstant.ID, sceneBean.getId());
                    bundle.putBoolean(IntentConstant.REMOVE_SCENE, hasDelP);
                    switchToActivity(SceneDetailActivity.class, bundle);
                } else {
                    ToastUtil.show(getResources().getString(R.string.scene_no_modify_permission));
                }
            }
        });
        automaticAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.ivSwitch) { // 无权限开关
                ToastUtil.show(getResources().getString(R.string.scene_no_control_permission));
            } else if (view.getId() == R.id.llSwitch) { // 开关
                SceneBean sceneBean = automaticAdapter.getItem(position);
                sceneBean.setIs_on(!sceneBean.isIs_on());
                sceneBean.setPerforming(true);
                automaticAdapter.notifyItemChanged(position);
                type = 1;
                checked = sceneBean.isIs_on();
                mPresenter.perform(sceneBean.getId(), sceneBean.isIs_on());
            }
        });
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
        boolean status = UserUtils.isLogin() ? true : HomeUtil.isSAEnvironment();
        if (manualAdapter != null) {
            manualAdapter.setStatus(status);
        }
        if (automaticAdapter != null) {
            automaticAdapter.setStatus(status);
        }
        if (HomeUtil.tokenIsInvalid) {
            rlInvalid.setVisibility(View.VISIBLE);
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

    @OnClick({R.id.llRefresh, R.id.llReconnect, R.id.tvMyHome, R.id.ivLog, R.id.ivAddScene, R.id.llHow, R.id.viewTips})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.llRefresh:  // 刷新
                AnimationUtil.rotationAnim(ivRefresh, 500, R.drawable.icon_scene_refreshing, R.drawable.icon_scene_refresh);
                if (WSocketManager.isConnecting) {
                    getData(true);
                } else {
                    WSocketManager.getInstance().start();
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

            case R.id.viewTips:
                if (HomeUtil.tokenIsInvalid) {
                    switchToActivity(FindSAGuideActivity.class);
                }
                break;
        }
    }

    private void showHomeDialog() {
        HomeSelectDialog homeSelectDialog = new HomeSelectDialog(HomeFragment.mHomeList);
        homeSelectDialog.setClickItemListener(homeCompanyBean -> {
            HomeFragment.homeLocalId = homeCompanyBean.getLocalId();
            CurrentHome = homeCompanyBean;
            HomeUtil.tokenIsInvalid = false;
            needLoadData = false;
            EventBus.getDefault().post(new HomeSelectedEvent());
            SpUtil.put(SpConstant.SA_TOKEN, homeCompanyBean.getSa_user_token());
//            homeChange();
            homeSelectDialog.dismiss();
            HttpUrlConfig.baseSAUrl = homeCompanyBean.getSa_lan_address();
            HttpUrlConfig.apiSAUrl = HttpUrlConfig.baseSAUrl + HttpUrlConfig.API;
            //检测接口是否有用
            if (CurrentHome.isIs_bind_sa() && TextUtils.isEmpty(CurrentHome.getMac_address()) && !TextUtils.isEmpty(CurrentHome.getSa_lan_address())) { // 绑定了sa，mac地址为空，sa_lan_address不为空
                checkUrl();
            } else {
                WSocketManager.getInstance().start();
            }
            showSaStatus();
        });
        homeSelectDialog.show(this);
    }

    private void checkUrl() {
        AllRequestUtil.checkUrl(CurrentHome.getSa_lan_address(), new AllRequestUtil.onCheckUrlListener() {
            @Override
            public void onSuccess() {
                WifiInfo wifiInfo = Constant.wifiInfo;
                if (wifiInfo != null && wifiInfo.getBSSID() != null)
                    CurrentHome.setMac_address(wifiInfo.getBSSID());
                dbManager.updateHomeMacAddress(CurrentHome.getLocalId(), wifiInfo.getBSSID());
                WSocketManager.getInstance().start();
            }

            @Override
            public void onError() {
                CurrentHome.setMac_address("");
                WSocketManager.getInstance().start();
            }
        });
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
            if (CurrentHome.isIs_bind_sa() && (HomeUtil.isSAEnvironment() || UserUtils.isLogin())) {
                this.showLoading = showLoading;
                HttpConfig.addHeader(CurrentHome.getSa_user_token());
                if (mPresenter != null) {
                    mPresenter.getPermissions(CurrentHome.getUser_id());
                }
            } else {
                loadLocalScene();
            }
        } else {
            refreshLayout.finishRefresh();
        }
    }

    /**
     * 场景列表成功
     *
     * @param data
     */
    @Override
    public void getSceneListSuccess(SceneListBean data) {
        refreshLayout.finishRefresh();
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
        if (CollectionUtil.isNotEmpty(data.getManual()) || CollectionUtil.isNotEmpty(data.getAuto_run())) {
            setNullView(false);
            if (CollectionUtil.isNotEmpty(data.getManual())) {
                tvManual.setVisibility(View.VISIBLE);
                rvManual.setVisibility(View.VISIBLE);
                manualAdapter.setNewData(data.getManual());
            } else {
                tvManual.setVisibility(View.GONE);
                rvManual.setVisibility(View.GONE);
            }
            if (CollectionUtil.isNotEmpty(data.getAuto_run())) {
                tvAutomatic.setVisibility(View.VISIBLE);
                rvAutomatic.setVisibility(View.VISIBLE);
                automaticAdapter.setNewData(data.getAuto_run());
            } else {
                tvAutomatic.setVisibility(View.GONE);
                rvAutomatic.setVisibility(View.GONE);
            }
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
        refreshLayout.finishRefresh();
        setNullView(true);
    }

    /**
     * 执行成功
     */
    @Override
    public void performSuccess() {
        String tip = UiUtil.getString(R.string.scene_perform_success);
        if (type == 1) {
            tip = checked ? UiUtil.getString(R.string.scene_perform_has_been_opened) : UiUtil.getString(R.string.scene_perform_has_been_closed);
        }
        getData(true);
        ToastUtil.show(tip);
    }

    /**
     * 执行失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void performFail(int errorCode, String msg) {
        if (errorCode == 5012) {
//            showRemovedTipsDialog();
            findSAToken();
        } else {
            ToastUtil.show(msg);
        }
    }

    @Override
    public void onPermissionsFail(int errorCode, String msg) {
        if (errorCode == 5012) {
//            showRemovedTipsDialog();
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
        if (notFirst) {
            RemovedTipsDialog removedTipsDialog = new RemovedTipsDialog(CurrentHome.getName());
            removedTipsDialog.show(this);
            dbManager.removeFamilyByToken(CurrentHome.getSa_user_token());
            if (CollectionUtil.isNotEmpty(HomeFragment.mHomeList)) {
                for (HomeCompanyBean homeCompanyBean : HomeFragment.mHomeList) {
                    if (!TextUtils.isEmpty(homeCompanyBean.getSa_user_token()) && homeCompanyBean.getSa_user_token().equalsIgnoreCase(CurrentHome.getSa_user_token())) {
                        HomeFragment.mHomeList.remove(homeCompanyBean);
                        break;
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(HomeFragment.mHomeList)) {  // 如果还有家庭
                CurrentHome = HomeFragment.mHomeList.get(0);
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
        UiUtil.starThread(new Runnable() {
            @Override
            public void run() {
                HomeCompanyBean homeCompanyBean = new HomeCompanyBean(1, getResources().getString(R.string.main_my_home));
                homeCompanyBean.setIs_bind_sa(false);
                homeCompanyBean.setSa_user_token(null);
                homeCompanyBean.setSa_lan_address(null);
                homeCompanyBean.setUser_id(1);
                homeCompanyBean.setSelected(true);
                long count = dbManager.insertHomeCompany(homeCompanyBean, null, false);
                UiUtil.runInMainThread(() -> {
                    CurrentHome = homeCompanyBean;
                    HomeFragment.mHomeList.add(homeCompanyBean);
                    refreshHome();
                });
            }
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
            UiUtil.starThread(new Runnable() {
                @Override
                public void run() {
                    dbManager.updateSATokenByLocalId(CurrentHome.getLocalId(), saToken);
                }
            });
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
        rlInvalid.setVisibility(View.GONE);
        viewData.setVisibility(visible ? View.GONE : View.VISIBLE);
        viewEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
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
        refreshLayout.finishRefresh();
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
}
