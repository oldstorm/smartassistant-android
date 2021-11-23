package com.yctc.zhiting.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.event.TempChannelFailEvent;
import com.app.main.framework.fileutil.BaseFileUtil;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.DeviceDetailActivity;
import com.yctc.zhiting.activity.ScanDevice2Activity;
import com.yctc.zhiting.adapter.HomeDeviceAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.home.SocketDeviceInfoBean;
import com.yctc.zhiting.entity.home.SocketSwitchBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.event.DeviceDataEvent;
import com.yctc.zhiting.event.HomeSelectedEvent;
import com.yctc.zhiting.fragment.contract.HomeItemFragmentContract;
import com.yctc.zhiting.fragment.presenter.HomeItemFragmentPresenter;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.SpacesItemDecoration;
import com.yctc.zhiting.utils.ZipUtils;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Response;
import okhttp3.WebSocket;

import static com.yctc.zhiting.config.Constant.CurrentHome;
import static com.yctc.zhiting.config.Constant.USER;
import static com.yctc.zhiting.config.Constant.wifiInfo;

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
    private DeviceMultipleBean mDeviceMultipleBean;
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
                setDeviceStatus();
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
//                        if (instances.get(i).getType().equalsIgnoreCase("light_bulb")) {
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
//                            }
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
        switchToActivity(ScanDevice2Activity.class, bundle);
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
            if (HomeUtil.notLoginAndSAEnvironment()){
                return;
            }
            switch (deviceMultipleBean.getItemType()) {
                case DeviceMultipleBean.DEVICE:
                    mDeviceMultipleBean = deviceMultipleBean;
//                    mPresenter.getPluginDetail(mDeviceMultipleBean.getPlugin_id());
                    toDeviceDetail("");
                    break;

                case DeviceMultipleBean.ADD:
                    Bundle bundle = new Bundle();
                    bundle.putLong(IntentConstant.ID, CurrentHome.getLocalId());
                    switchToActivity(ScanDevice2Activity.class);
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
        refreshLayout.setEnableRefresh(!HomeUtil.tokenIsInvalid);
        if (HomeUtil.tokenIsInvalid){  // satoken失效
            recyclerView.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
            tvAdd.setVisibility(View.GONE);
            ivTips.setImageResource(R.drawable.icon_invalid_token);
            tvTips.setText(getResources().getString(R.string.home_invalid_token));
        }else {
            llEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(visible ? View.GONE : View.VISIBLE);
            tvTips.setPadding(0, pos == 1 ? UiUtil.dip2px(11) : UiUtil.dip2px(5), 0, pos == 1 ? UiUtil.dip2px(37) : UiUtil.dip2px(49));
            tvTips.setText(getResources().getString(R.string.home_no_device));
            ivTips.setImageResource(R.drawable.icon_device_empty);
            if (visible) {
                if (!TextUtils.isEmpty(CurrentHome.getSa_user_token())) {
                    tvAdd.setVisibility(HomeFragment.addDeviceP ? View.VISIBLE : View.GONE);
                } else {
                    tvAdd.setVisibility(View.VISIBLE);
                }
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
                    if (deviceMultipleBean.getLocation_id() == locationBean.getId()) {
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

//                initDeviceStatus(data);
                setDeviceStatus();
            } else {
                setNullView(true);
            }
        } else {
            setNullView(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TempChannelFailEvent event) {
        refreshLayout.finishRefresh();
    }

    /**
     * 设置设备状态
     */
    private void setDeviceStatus(){
//        if (CurrentHome.getMac_address() != null && wifiInfo !=null && CurrentHome.getMac_address().equals(wifiInfo.getBSSID())) {
        if (CurrentHome.getArea_id() > 0 || CurrentHome.getId()>0) {
            initDeviceStatus(data);
        }else {
            if (homeDeviceAdapter!=null) {
                if (CollectionUtil.isNotEmpty(homeDeviceAdapter.getData())){
                    for (DeviceMultipleBean deviceMultipleBean : homeDeviceAdapter.getData()){
                        deviceMultipleBean.setOnline(false);
                    }
                    homeDeviceAdapter.notifyDataSetChanged();
                }
            }
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

    /**
     * 插件详情成功
     *
     * @param pluginDetailBean
     */
    @Override
    public void getPluginDetailSuccess(PluginDetailBean pluginDetailBean) {
        if (pluginDetailBean != null) {
            PluginsBean pluginsBean = pluginDetailBean.getPlugin();
            String pluginId = pluginsBean.getId();
            String pluginFilePath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                pluginFilePath = getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/plugins/" + pluginId;
            }else {
                pluginFilePath = Constant.PLUGIN_PATH + pluginId;
            }
            if (pluginsBean != null) {
                String downloadUrl = pluginsBean.getDownload_url();
                String cachePluginJson = SpUtil.get(pluginId);
                PluginsBean cachePlugin = GsonConverter.getGson().fromJson(cachePluginJson, PluginsBean.class);
                String cacheVersion = "";
                if (cachePlugin!=null){
                    cacheVersion = cachePlugin.getVersion();
                }
                String version = pluginsBean.getVersion();
                if (mDeviceMultipleBean!=null && BaseFileUtil.isFileExist(pluginFilePath) &&
                        !TextUtils.isEmpty(cacheVersion) && !TextUtils.isEmpty(version) && cacheVersion.equals(version)) {  // 如果缓存存在且版本相同
                    String urlPath = "file://"+pluginFilePath+"/"+mDeviceMultipleBean.getControl();
                    toDeviceDetail(urlPath);
                } else {
                    if (!TextUtils.isEmpty(downloadUrl)) {
                        String suffix = downloadUrl.substring(downloadUrl.lastIndexOf(".") + 1);
                        BaseFileUtil.deleteFolderFile(pluginFilePath, true);
                        String fileZipPath = pluginFilePath+"."+suffix;
                        File file = new File(fileZipPath);
                        BaseFileUtil.deleteFile(file);
                        List<Header> headers = new ArrayList<>();
                        headers.add(new Header("Accept-Encoding", "identity"));
                        headers.add(new Header(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken()));
                        String path = "";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                            path = getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/plugins/";
                        }else {
                            path = Constant.PLUGIN_PATH;
                        }
                        String finalPath = path;
                        String finalPluginFilePath = pluginFilePath;
                        HTTPCaller.getInstance().downloadFile(downloadUrl, path, pluginId, headers.toArray(new Header[headers.size()]), UiUtil.getString(R.string.home_download_plugin_package_fail), new ProgressUIListener() {
                            @Override
                            public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                                LogUtil.e("进度：" + percent);
                                if (percent == 1) {
                                    LogUtil.e("下载完成");
                                    try {
                                        ZipUtils.decompressFile(new File(fileZipPath), finalPath, true);
                                        String pluginsBeanToJson = GsonConverter.getGson().toJson(pluginsBean);
                                        SpUtil.put(pluginId, pluginsBeanToJson);
                                        String urlPath = "file://"+ finalPluginFilePath +"/"+mDeviceMultipleBean.getControl();
                                        toDeviceDetail(urlPath);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        ToastUtil.show(UiUtil.getString(R.string.unzip_file_exception));
                                    }

                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * 去设备详情界面
     * @param urlPath
     */
    private void toDeviceDetail(String urlPath){
        Bundle bundle = new Bundle();
        bundle.putString(IntentConstant.PLUGIN_PATH, urlPath);
        bundle.putSerializable(IntentConstant.BEAN, mDeviceMultipleBean);
        switchToActivity(DeviceDetailActivity.class, bundle);
    }

    /**
     * 插件详情失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getPluginDetailFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}
