package com.yctc.zhiting.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.gsonutils.GsonConverter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.DeviceDetailActivity;
import com.yctc.zhiting.activity.ScanDeviceActivity;
import com.yctc.zhiting.adapter.HomeDeviceAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.home.SocketDeviceInfoBean;
import com.yctc.zhiting.entity.home.SocketSwitchBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.event.DeviceDataEvent;
import com.yctc.zhiting.event.HomeSelectedEvent;
import com.yctc.zhiting.fragment.contract.HomeItemFragmentContract;
import com.yctc.zhiting.fragment.presenter.HomeItemFragmentPresenter;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.SpacesItemDecoration;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Response;
import okhttp3.WebSocket;

import static com.yctc.zhiting.config.Constant.CurrentHome;

public class HomeItemFragment extends MVPBaseFragment<HomeItemFragmentContract.View, HomeItemFragmentPresenter> implements
        HomeItemFragmentContract.View {
    private static String KEY_TITLE = "key_title";
    private static String KEY_POS = "key_pos";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.llEmpty)
    LinearLayout llEmpty;
    @BindView(R.id.ivTips)
    ImageView ivTips;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.tvAdd)
    TextView tvAdd;
    @BindView(R.id.tvRetry)
    TextView tvRetry;
    @BindView(R.id.llParent)
    LinearLayout llParent;

    private int pos;
    private int mSendId;//发送消息的id,自动自增
    private LocationBean locationBean;
    private HomeDeviceAdapter homeDeviceAdapter;
    private List<DeviceMultipleBean> data = new ArrayList<>();

    public static HomeItemFragment getInstance(LocationBean LocationBean, int pos) {
        HomeItemFragment fragment = new HomeItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_TITLE, LocationBean);
        args.putInt(KEY_POS, pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragmemt_home_item;
    }

    @Override
    protected void initUI() {
        initRv();
        initWebSocket();
        pos = getArguments().getInt(KEY_POS);
        locationBean = (LocationBean) getArguments().getSerializable(KEY_TITLE);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (CurrentHome != null) {
                    EventBus.getDefault().post(new HomeSelectedEvent(true));
                } else {
                    refreshLayout.finishRefresh();
                }
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }
        });
    }

    private void initWebSocket() {
        WSocketManager.getInstance().addWebSocketListener(new IWebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                mSendId = 0;
                initDeviceStatus(data);
                LogUtil.e("HomeItemFragment=123=open");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                handleMessage(text);
            }
        });
    }

    /**
     * 处理消息
     *
     * @param message
     */
    private void handleMessage(String message) {
        Log.e("handleMessage123=", message);
        if (!TextUtils.isEmpty(message)) {
            if (message.contains("attribute_change")) {//开关信息
                SocketSwitchBean data = GsonConverter.getGson().fromJson(message, SocketSwitchBean.class);
                if (data != null) {
                    SocketSwitchBean.DataBean dataBean = data.getData();
                    SocketSwitchBean.DataBean.AttrBean attrBean = null;
                    if (dataBean != null)
                        attrBean = dataBean.getAttr();
                    if (attrBean != null) {
                        String onOff = attrBean.getVal();
                        refreshDeviceStatus(1, dataBean.getIdentity(), onOff, false, 0);
                    }
                }
            } else {//初始化设备信息
                SocketDeviceInfoBean data = GsonConverter.getGson().fromJson(message, SocketDeviceInfoBean.class);
                SocketDeviceInfoBean.ResultBean resultBean = null;
                SocketDeviceInfoBean.ResultBean.DeviceBean deviceBean = null;
                List<SocketDeviceInfoBean.ResultBean.DeviceBean.InstancesBean> instances = null;
                List<SocketDeviceInfoBean.ResultBean.DeviceBean.InstancesBean.AttributesBean> attributes = null;
                if (data != null)
                    resultBean = data.getResult();
                if (resultBean != null)
                    deviceBean = resultBean.getDevice();
                if (deviceBean != null)
                    instances = deviceBean.getInstances();
                if (CollectionUtil.isNotEmpty(instances)) {
                    for (int i = 0; i < instances.size(); i++) {
                        if (instances.get(i).getType().equalsIgnoreCase("light_bulb")) {
                            attributes = instances.get(i).getAttributes();
                            if (CollectionUtil.isNotEmpty(attributes)) {
                                for (int j = 0; j < attributes.size(); j++) {
                                    if (attributes.get(j).getAttribute().equalsIgnoreCase("power")) {
                                        refreshDeviceStatus(2, deviceBean.getIdentity(),
                                                attributes.get(j).getVal(),
                                                attributes.get(j).isCan_control(),
                                                instances.get(i).getInstance_id());
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @param type         1开关 2权限/开关 3是否在线
     * @param identity     设备id
     * @param onOff        on/off
     * @param isPermission 是否有权限控制设备
     */
    private void refreshDeviceStatus(int type, String identity, String onOff, boolean isPermission, int instance_id) {
        if (CollectionUtil.isEmpty(data)) return;
        for (int position = 0; position < data.size(); position++) {
            DeviceMultipleBean device = data.get(position);
            if (device.getIdentity() != null && device.getIdentity().equalsIgnoreCase(identity)) {
                device.setOnline(true);//收到消息就是在线了
                if (instance_id > 0)
                    device.setInstance_id(instance_id);
                if (type == 1 && !TextUtils.isEmpty(onOff)) {//开关
                    device.setPower(onOff);
                } else if (type == 2) {//初始状态
                    device.setPower(onOff);
                    device.setIs_permit(isPermission);
                }
                homeDeviceAdapter.notifyItemChanged(position);
                break;
            }
        }
    }

    /**
     * 添加设备
     */
    @OnClick(R.id.tvAdd)
    void onClickAdd() {
        Bundle bundle = new Bundle();
        bundle.putLong(IntentConstant.ID, CurrentHome.getLocalId());
        switchToActivity(ScanDeviceActivity.class, bundle);
    }

    /**
     * 重试
     */
    @OnClick(R.id.tvRetry)
    void onClickRetry() {
        ToastUtil.show("重试");
    }

    /**
     * 设备列表
     */
    private void initRv() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        recyclerView.addItemDecoration(spacesItemDecoration);

        homeDeviceAdapter = new HomeDeviceAdapter(data);
        recyclerView.setAdapter(homeDeviceAdapter);
        homeDeviceAdapter.setOnItemClickListener((adapter, view, position) -> {
            DeviceMultipleBean deviceMultipleBean = homeDeviceAdapter.getItem(position);
            switch (deviceMultipleBean.getItemType()) {
                case DeviceMultipleBean.DEVICE:
                    bundle.putInt(IntentConstant.ID, homeDeviceAdapter.getItem(position).getId());
                    bundle.putBoolean(IntentConstant.IS_SA, homeDeviceAdapter.getItem(position).isIs_sa());
                    bundle.putString(IntentConstant.NAME, homeDeviceAdapter.getItem(position).getName());
                    bundle.putString(IntentConstant.LOGO_URL, homeDeviceAdapter.getItem(position).getLogo_url());
                    bundle.putInt(IntentConstant.RA_ID, homeDeviceAdapter.getItem(position).getLocation_id());
                    bundle.putString(IntentConstant.PLUGIN_URL, homeDeviceAdapter.getItem(position).getPlugin_url());
                    bundle.putString(IntentConstant.RA_NAME, homeDeviceAdapter.getItem(position).getLocation_name());
                    switchToActivity(DeviceDetailActivity.class, bundle);
                    break;

                case DeviceMultipleBean.ADD:
                    Bundle bundle = new Bundle();
                    bundle.putLong(IntentConstant.ID, CurrentHome.getLocalId());
                    switchToActivity(ScanDeviceActivity.class);
                    break;
            }
        });

        homeDeviceAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.ivSwitch) { // 开关
                switchButton(homeDeviceAdapter.getItem(position));
            }
        });
    }

    /**
     * 开关控制
     *
     * @param bean
     */
    private void switchButton(DeviceMultipleBean bean) {
        mSendId++;
        String power = Constant.PowerType.TYPE_ON;
        if (!TextUtils.isEmpty(bean.getPower()) && bean.getPower().equalsIgnoreCase(Constant.PowerType.TYPE_ON))
            power = Constant.PowerType.TYPE_OFF;
        String formatStr = UiUtil.getString(R.string.device_switch);
        String deviceJson = String.format(formatStr, power, bean.getInstance_id(), mSendId, bean.getIdentity(), bean.getPlugin_id());
        UiUtil.starThread(() -> WSocketManager.getInstance().sendMessage(deviceJson));
    }

    /**
     * 设置空视图
     *
     * @param visible
     */
    private void setNullView(boolean visible) {
        llEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(visible ? View.GONE : View.VISIBLE);
        tvTips.setPadding(0, pos == 1 ? UiUtil.dip2px(11) : UiUtil.dip2px(5), 0, pos == 1 ? UiUtil.dip2px(37) : UiUtil.dip2px(49));
        if (visible) {
            if (!TextUtils.isEmpty(CurrentHome.getSa_user_token())) {
                tvAdd.setVisibility(HomeFragment.addDeviceP ? View.VISIBLE : View.GONE);
            } else {
                tvAdd.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DeviceDataEvent event) {
        data.clear();
        refreshLayout.finishRefresh();
        if (CollectionUtil.isNotEmpty(event.getDevices())) {
            if (pos > 0) {
                for (DeviceMultipleBean deviceMultipleBean : event.getDevices()) {
                    if (deviceMultipleBean.getLocation_id() == locationBean.getLocationId()) {
                        data.add(deviceMultipleBean);
                    }
                }
            } else {
                data.addAll(event.getDevices());
            }

            if (CollectionUtil.isNotEmpty(data)) {
                setNullView(false);
                if (HomeFragment.addDeviceP) {
                    data.add(new DeviceMultipleBean(DeviceMultipleBean.ADD));
                }
                homeDeviceAdapter.setNewData(data);
                initDeviceStatus(data);
            } else {
                setNullView(true);
            }
        } else {
            setNullView(true);
        }
    }

    /**
     * WebSocket 获取设备是否有权限、开关状态
     * 默认接受离线状态，接受到消息后显示在线状态
     *
     * @param list
     */
    private void initDeviceStatus(List<DeviceMultipleBean> list) {
        List<DeviceMultipleBean> data = new ArrayList<>();
        data.addAll(list);
        if (CollectionUtil.isEmpty(data)) return;
        UiUtil.starThread(() -> {
            for (DeviceMultipleBean device : data) {
                if (!TextUtils.isEmpty(device.getPlugin_id()) && !TextUtils.isEmpty(device.getIdentity())) {
                    mSendId++;
                    String formatStr = UiUtil.getString(R.string.device_info);
                    String deviceJson = String.format(formatStr, device.getPlugin_id(), device.getIdentity(), mSendId);
                    WSocketManager.getInstance().sendMessage(deviceJson);
                }
            }
        });
    }
}
