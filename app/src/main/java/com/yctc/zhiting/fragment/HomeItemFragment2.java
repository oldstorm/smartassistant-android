package com.yctc.zhiting.fragment;

import static com.yctc.zhiting.config.Constant.CurrentHome;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.event.TempChannelFailEvent;
import com.app.main.framework.fileutil.BaseFileUtil;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.DownloadFailListener;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.king.zxing.util.LogUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.DeviceDetailActivity;
import com.yctc.zhiting.activity.ScanDevice2Activity;
import com.yctc.zhiting.adapter.HomeDeviceAdapter2;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.entity.ws_request.AttrRequestBean;
import com.yctc.zhiting.entity.ws_request.WSConstant;
import com.yctc.zhiting.entity.ws_request.WSDeviceRequest;
import com.yctc.zhiting.entity.ws_request.WSRequest;
import com.yctc.zhiting.entity.ws_response.AttributesBean;
import com.yctc.zhiting.entity.ws_response.EventResponseBean;
import com.yctc.zhiting.entity.ws_response.InstanceBean;
import com.yctc.zhiting.entity.ws_response.ServicesBean;
import com.yctc.zhiting.entity.ws_response.WSBaseResponseBean;
import com.yctc.zhiting.entity.ws_response.WSDeviceResponseBean;
import com.yctc.zhiting.event.ChangeLayoutModeEvent;
import com.yctc.zhiting.event.DeviceDataEvent;
import com.yctc.zhiting.event.HomeSelectedEvent;
import com.yctc.zhiting.event.RefreshHomeEvent;
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
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Response;
import okhttp3.WebSocket;

public class HomeItemFragment2 extends MVPBaseFragment<HomeItemFragmentContract.View, HomeItemFragmentPresenter> implements
        HomeItemFragmentContract.View {

    private static String KEY_TITLE = "key_title";
    private static String KEY_POS = "key_pos";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
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

    private int mPosition;
    private LocationBean locationBean;
    private HomeDeviceAdapter2 homeDeviceAdapter;
    private HomeDeviceAdapter2 homeDeviceLinerAdapter;
    private DeviceMultipleBean mDeviceMultipleBean;
    private List<DeviceMultipleBean> mDeviceList = new ArrayList<>();

    private ConcurrentHashMap<String, WSRequest> requestMap = new ConcurrentHashMap<>(); // 用于缓存ws发送请求
    private boolean isLinearMode = false;
    private SpacesItemDecoration spacesItemDecoration;
    private IWebSocketListener mWebSocketListener;
    public static HomeItemFragment2 getInstance(LocationBean LocationBean, int pos) {
        HomeItemFragment2 fragment = new HomeItemFragment2();
        Bundle args = new Bundle();
        args.putSerializable(KEY_TITLE, LocationBean);
        args.putInt(KEY_POS, pos);
        fragment.setArguments(args);
        return fragment;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public void setLocationBean(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragmemt_home_item2;
    }

    @Override
    protected void initUI() {
//        LogUtil.d("mPosition : " + mPosition + " --- initUI");
        isLinearMode = SpUtil.getBoolean(SpConstant.KEY_HOME_RV_MODE);
        initRv();
        initWebSocket();
        mPosition = getArguments().getInt(KEY_POS);
        locationBean = (LocationBean) getArguments().getSerializable(KEY_TITLE);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (CurrentHome != null) {
                    EventBus.getDefault().post(new RefreshHomeEvent());
                }
                refreshLayout.finishRefresh();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }
        });
    }

    private void initWebSocket() {
        mWebSocketListener = new IWebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                requestMap.clear();
                setDeviceStatus();
                LogUtil.e("HomeItemFragment=123=open");
                LogUtil.e("HomeItemFragment --- " + "onOpen --- mPosition : " + mPosition);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                handleMessage(text);
//                LogUtil.e("HomeItemFragment --- " + "onMessage --- mPosition : " + mPosition);
            }
        };

        WSocketManager.getInstance().addWebSocketListener(mWebSocketListener);
    }

    /**
     * 处理消息
     *
     * @param message
     */
    private void handleMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            WSBaseResponseBean data = GsonConverter.getGson().fromJson(message, new TypeToken<WSBaseResponseBean>() {
            }.getType());
            if (data == null) return;
            String type = data.getType();
            String eventType = data.getEvent_type();
            String respId = String.valueOf(data.getId());
            if (type == null) return;
            //LogUtil.e(TAG + "WebSocket结果:" + message);
            if (type.equals(WSConstant.RESPONSE)) {  // 客户发送之后响应的消息
                if (data.isSuccess() && requestMap.containsKey(respId)) {  // 如果成功
                    WSBaseResponseBean<WSDeviceResponseBean> wsBaseResponseBean = GsonConverter.getGson().fromJson(message, new TypeToken<WSBaseResponseBean<WSDeviceResponseBean>>() {
                    }.getType());
                    WSDeviceResponseBean deviceResponseBean = wsBaseResponseBean.getData();
                    if (deviceResponseBean != null) {
                        WSRequest<WSDeviceRequest> deviceRequestWSRequest = requestMap.get(respId);
                        if (deviceRequestWSRequest != null && CollectionUtil.isNotEmpty(mDeviceList)) {
                            for (int i = 0; i < mDeviceList.size(); i++) {
                                DeviceMultipleBean deviceMultipleBean = mDeviceList.get(i);
//                                LogUtil.e(TAG + "handleMessage2221===" + message);
                                String wsIid = "";
                                String devIId = deviceMultipleBean.getIid();
                                if (deviceRequestWSRequest.getData() != null)
                                    wsIid = deviceRequestWSRequest.getData().getIid();
                                if (!TextUtils.isEmpty(devIId) && !TextUtils.isEmpty(wsIid) && devIId.equals(wsIid)) {
                                    deviceMultipleBean.setOnline(true);
                                    WSDeviceResponseBean device_instances = deviceMultipleBean.getDevice_instances();
                                    device_instances.setInstances(deviceResponseBean.getInstances());
                                    int finalI = i;
                                    UiUtil.runInMainThread(() ->{
                                        if (isLinearMode) {
                                            homeDeviceLinerAdapter.notifyItemChanged(finalI);
                                        } else {
                                            homeDeviceAdapter.notifyItemChanged(finalI);
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (type.equalsIgnoreCase(WSConstant.EVENT)) {  // 服务器自动发的设备属性信息
                if (!TextUtils.isEmpty(eventType) && eventType.equalsIgnoreCase(WSConstant.ATTRIBUTE_CHANGE)) {
                    WSBaseResponseBean<EventResponseBean> wsBaseResponseBean = GsonConverter.getGson().fromJson(message, new TypeToken<WSBaseResponseBean<EventResponseBean>>() {
                    }.getType());
                    EventResponseBean eventResponseBean = wsBaseResponseBean.getData();
                    dealAttrInfo(eventResponseBean);
                }
            }
        }
    }

    /**
     * 处理event信息
     *
     * @param eventResponseBean
     */
    private void dealAttrInfo(EventResponseBean eventResponseBean) {
        if (CollectionUtil.isNotEmpty(mDeviceList)) {
            boolean hasFound = false;
            for (int i = 0; i < mDeviceList.size(); i++) {  // 遍历设备列表
                DeviceMultipleBean deviceMultipleBean = mDeviceList.get(i);
                AttributesBean attributesBean = eventResponseBean.getAttr();
                if (attributesBean != null) {
                    String attrIid = attributesBean.getIid();
                    String devIid = deviceMultipleBean.getIid();
                    if (!deviceMultipleBean.isIs_sa() && !TextUtils.isEmpty(devIid) && !TextUtils.isEmpty(attrIid) && devIid.equals(attrIid)) {
                        deviceMultipleBean.setOnline(true);
                        LogUtil.e(TAG + "handleMessage2222");

                        List<InstanceBean> instances = deviceMultipleBean.getDevice_instances().getInstances();
                        if (CollectionUtil.isNotEmpty(instances))
                            for (InstanceBean instanceBean : instances) {  // 遍历instance
                                List<ServicesBean> services = instanceBean.getServices();
                                if (CollectionUtil.isNotEmpty(services))
                                    for (ServicesBean servicesBean : services) {  // 遍历service
                                        List<AttributesBean> attributes = servicesBean.getAttributes();
                                        if (CollectionUtil.isNotEmpty(attributes))
                                            for (AttributesBean attr : attributes) {  // 遍历attribute
                                                if (attr.getAid() == attributesBean.getAid()) { // 如果属性相等
                                                    attr.setVal(attributesBean.getVal());
                                                    if(!isLinearMode){
                                                        if (homeDeviceAdapter != null) {
                                                            int finalI = i;
                                                            UiUtil.runInMainThread(() -> homeDeviceAdapter.notifyItemChanged(finalI));
                                                        }
                                                    }else {
                                                        if (homeDeviceLinerAdapter != null) {
                                                            int finalI = i;
                                                            UiUtil.runInMainThread(() -> homeDeviceLinerAdapter.notifyItemChanged(finalI));
                                                        }
                                                    }
                                                    hasFound = true;
                                                    break;
                                                }
                                            }
                                        if (hasFound) {
                                            break;
                                        }
                                    }
                                if (hasFound) {
                                    break;
                                }
                            }
                    }
                }
                if (hasFound) {
                    break;
                }
            }
        }
    }

    /**
     * 添加设备
     */
    @OnClick(R.id.tvAdd)
    void onClickAdd() {
        checkFineLocationTask(() -> {
            Bundle bundle = new Bundle();
            bundle.putLong(IntentConstant.ID, CurrentHome.getLocalId());
            switchToActivity(ScanDevice2Activity.class, bundle);
        });
    }

    /**
     * 设备列表
     */
    private void initRv() {
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spacesItemDecoration = new SpacesItemDecoration(spaceValue);

        homeDeviceAdapter = new HomeDeviceAdapter2(mDeviceList,false);
        homeDeviceLinerAdapter = new HomeDeviceAdapter2(mDeviceList,true);

        MyOnItemClickListener itemClickListener = new MyOnItemClickListener();
        MyOnItemChildClickListener itemChildClickListener = new MyOnItemChildClickListener();

        homeDeviceAdapter.setOnItemClickListener(itemClickListener);
        homeDeviceLinerAdapter.setOnItemClickListener(itemClickListener);

        homeDeviceAdapter.setOnItemChildClickListener(itemChildClickListener);
        homeDeviceLinerAdapter.setOnItemChildClickListener(itemChildClickListener);

        setRvMode(isLinearMode);
    }

    private class MyOnItemClickListener implements BaseQuickAdapter.OnItemClickListener {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            DeviceMultipleBean deviceMultipleBean = isLinearMode ? homeDeviceLinerAdapter.getItem(position) : homeDeviceAdapter.getItem(position);
            if (HomeUtil.notLoginAndSAEnvironment()) {
                return;
            }
            switch (deviceMultipleBean.getItemType()) {
                case DeviceMultipleBean.DEVICE:
                    mDeviceMultipleBean = deviceMultipleBean;
                    if (deviceMultipleBean.isIs_sa()) {
                        toDeviceDetail("");
                    } else {
                        showLoadingDialogInAct();
                        mPresenter.getPluginDetail(mDeviceMultipleBean.getPlugin_id());
                    }
                    break;

                case DeviceMultipleBean.ADD:
                    Bundle bundle = new Bundle();
                    bundle.putLong(IntentConstant.ID, CurrentHome.getLocalId());
                    switchToActivity(ScanDevice2Activity.class);
                    break;
            }
        }
    }

    private class MyOnItemChildClickListener implements BaseQuickAdapter.OnItemChildClickListener{

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            if (view.getId() == R.id.ivSwitch) { // 开关
                switchButton(isLinearMode ? homeDeviceLinerAdapter.getItem(position) : homeDeviceAdapter.getItem(position));
            }
        }
    }

    /**
     * 开关控制
     *
     * @param bean
     */
    private void switchButton(DeviceMultipleBean bean) {
        Constant.mSendId=Constant.mSendId+1;
        WSRequest<AttrRequestBean> switchRequest = new WSRequest<>();
        switchRequest.setId(Constant.mSendId);
        switchRequest.setDomain(bean.getPlugin_id());
        switchRequest.setService(WSConstant.SERVICE_SET_ATTRIBUTES);
        AttributesBean switchAttr = new AttributesBean();
        AttributesBean beanAttr = bean.getAttributes();
        if (beanAttr != null) {
            switchAttr.setIid(bean.getIid());
            switchAttr.setAid(beanAttr.getAid());
            Object val = beanAttr.getVal();
            // 因为val可能是字符串或数值，所以需要两个变量接收
            String valStr = "";
            double valDou = 0;
            if (val instanceof String) {
                valStr = beanAttr.getVal().toString();
            } else if (val instanceof Double){
                valDou = (double) val;
            }
            // val是“on”或是1
            if (valStr.equals(Constant.ON) || valDou == Constant.DOU_ON) {
                switchAttr.setVal(Constant.OFF);
            } else {
                switchAttr.setVal(Constant.ON);
            }
        }
        List<AttributesBean> attributes = new ArrayList<>();
        attributes.add(switchAttr);
        AttrRequestBean attrRequestBean = new AttrRequestBean(attributes);
        switchRequest.setData(attrRequestBean);
        String deviceJson = new Gson().toJson(switchRequest);
        LogUtil.e("控制开关：" + deviceJson);
        UiUtil.starThread(() -> WSocketManager.getInstance().sendMessage(deviceJson));
    }

    /**
     * 设置空视图
     *
     * @param visible
     */
    private void setNullView(boolean visible) {
        refreshLayout.setEnableRefresh(!HomeUtil.tokenIsInvalid);
        if (HomeUtil.tokenIsInvalid) {// satoken失效
            recyclerView.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            tvAdd.setVisibility(View.GONE);
            ivTips.setImageResource(R.drawable.icon_invalid_token);
            tvTips.setText(UiUtil.getString(R.string.home_invalid_token));
            LogUtil.e(TAG + "setNullView1");
        } else {
            scrollView.setVisibility(visible ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(visible ? View.GONE : View.VISIBLE);
            //tvTips.setPadding(0, mPosition == 1 ? UiUtil.dip2px(11) : UiUtil.dip2px(5), 0, mPosition == 1 ? UiUtil.dip2px(37) : UiUtil.dip2px(49));
            tvTips.setText(UiUtil.getString(R.string.home_no_device));
            ivTips.setImageResource(R.drawable.icon_device_empty);
            LogUtil.e(TAG + "setNullView2=" + visible);
            LogUtil.e(TAG + "setNullView3=" + HomeFragment2.addDeviceP);
            if (visible) {
                tvAdd.setVisibility(HomeFragment2.addDeviceP ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DeviceDataEvent event) {
        mDeviceList.clear();
        refreshLayout.finishRefresh();
        if (CollectionUtil.isNotEmpty(event.getDevices())) {
            if (mPosition > 0) {
                for (DeviceMultipleBean deviceMultipleBean : event.getDevices()) {
                    int locationId = Constant.AREA_TYPE == 2 ? deviceMultipleBean.getDepartment_id() : deviceMultipleBean.getLocation_id();
                    if (locationId == locationBean.getId()) {
                        mDeviceList.add(deviceMultipleBean);
                    }
                }
            } else {
                mDeviceList.addAll(event.getDevices());
            }

            if (CollectionUtil.isNotEmpty(mDeviceList)) {
                setNullView(false);
                if (HomeFragment2.addDeviceP) {
                    mDeviceList.add(new DeviceMultipleBean(DeviceMultipleBean.ADD));
                }
                for (int position = 0; position < mDeviceList.size(); position++) {
                    mDeviceList.get(position).setTag(position);
                }
                homeDeviceAdapter.setNewData(mDeviceList);
                homeDeviceLinerAdapter.setNewData(mDeviceList);
                setDeviceStatus();
            } else {
                setNullView(true);
            }
        } else {
            setNullView(true);
        }
        LogUtil.e("onMessageEvent=" + GsonConverter.getGson().toJson(mDeviceList));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TempChannelFailEvent event) {
        refreshLayout.finishRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChangeLayoutModeEvent event) {
        isLinearMode = event.isLinear();
        setRvMode(isLinearMode);
    }

    private void setRvMode(boolean isLinearMode) {
        recyclerView.setLayoutManager(isLinearMode ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(isLinearMode ? homeDeviceLinerAdapter : homeDeviceAdapter);
        if(isLinearMode){
            recyclerView.removeItemDecoration(spacesItemDecoration);
            homeDeviceLinerAdapter.notifyDataSetChanged();
        }else {
            recyclerView.addItemDecoration(spacesItemDecoration);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            homeDeviceAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置设备状态
     */
    private void setDeviceStatus() {
        if (CurrentHome!=null && (CurrentHome.getArea_id() > 0 || CurrentHome.getId() > 0)) {
            initDeviceStatus();
        } else if ((homeDeviceAdapter != null && homeDeviceLinerAdapter != null) && CollectionUtil.isNotEmpty(mDeviceList)) {
            for (DeviceMultipleBean deviceMultipleBean : mDeviceList) {
                deviceMultipleBean.setOnline(false);
            }
            if(isLinearMode){
                homeDeviceLinerAdapter.notifyDataSetChanged();
            }else {
                homeDeviceAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * WebSocket 获取设备是否有权限、开关状态
     * 默认接受离线状态，接受到消息后显示在线状态
     */
    private synchronized void initDeviceStatus() {
        if (CollectionUtil.isEmpty(mDeviceList)) return;
        List<DeviceMultipleBean> mTempDeviceList = new ArrayList<>();
        mTempDeviceList.addAll(mDeviceList);
        UiUtil.starThread(() -> {
            for (DeviceMultipleBean device : mTempDeviceList) {
                if (!device.isIs_sa() && !TextUtils.isEmpty(device.getIid())) {
                    Constant.mSendId=Constant.mSendId+1;
                    WSRequest<WSDeviceRequest> wsRequest = new WSRequest<>();
                    wsRequest.setId(Constant.mSendId);
                    wsRequest.setDomain(device.getPlugin_id());
                    wsRequest.setService(WSConstant.SERVICE_GET_INSTANCE);
                    WSDeviceRequest wsAddDeviceRequest = new WSDeviceRequest(device.getIid());
                    wsRequest.setData(wsAddDeviceRequest);
                    String deviceJson = GsonConverter.getGson().toJson(wsRequest);
                    //LogUtil.e("设备发送的数据：" + deviceJson);
                    requestMap.put(String.valueOf(Constant.mSendId), wsRequest);
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
            if (pluginsBean != null) {
                String pluginId = pluginsBean.getId();
                String pluginFilePath = "";
                if (getContext() != null)
                    pluginFilePath = getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + Constant.PLUGINS_PATH + pluginId;
                String downloadUrl = pluginsBean.getDownload_url();
                String cachePluginJson = SpUtil.get(pluginId);
                PluginsBean cachePlugin = GsonConverter.getGson().fromJson(cachePluginJson, PluginsBean.class);
                String cacheVersion = "";
                if (cachePlugin != null) {
                    cacheVersion = cachePlugin.getVersion();
                }
                LogUtil.e("缓存版本："+cacheVersion);
                String version = pluginsBean.getVersion();
                if (mDeviceMultipleBean != null && BaseFileUtil.isFileExist(pluginFilePath) &&
                        !TextUtils.isEmpty(cacheVersion) && !TextUtils.isEmpty(version) && cacheVersion.equals(version)) {  // 如果缓存存在且版本相同
                    String urlPath = Constant.FILE_HEAD + pluginFilePath + Constant.SEPARATOR + mDeviceMultipleBean.getControl();
                    toDeviceDetail(urlPath);
                } else {
                    if (!TextUtils.isEmpty(downloadUrl)) {
                        String suffix = downloadUrl.substring(downloadUrl.lastIndexOf(".") + 1);
                        BaseFileUtil.deleteFolderFile(pluginFilePath, true);
                        String fileZipPath = pluginFilePath + "." + suffix;
                        File file = new File(fileZipPath);
                        BaseFileUtil.deleteFile(file);
                        List<Header> headers = new ArrayList<>();
                        headers.add(new Header("Accept-Encoding", "identity"));
                        headers.add(new Header(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken()));
                        String path = getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + Constant.PLUGINS_PATH ;
                        String finalPath = path;
                        String finalPluginFilePath = pluginFilePath;
                        String tip = UiUtil.getString(R.string.home_download_plugin_package_fail);
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
                                        String urlPath = Constant.FILE_HEAD + finalPluginFilePath + Constant.SEPARATOR + mDeviceMultipleBean.getControl();
                                        toDeviceDetail(urlPath);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        dismissLoadingDialogInAct();
                                        ToastUtil.show(UiUtil.getString(R.string.unzip_file_exception));
                                    }
                                }
                            }
                        }, new DownloadFailListener() {
                            @Override
                            public void downloadFailed() {
                                dismissLoadingDialogInAct();
                                UiUtil.runInMainThread(() -> ToastUtil.show(tip));
                            }
                        });
                    } else {
                        dismissLoadingDialogInAct();
                        ToastUtil.show(UiUtil.getString(R.string.home_download_plugin_package_fail));
                    }
                }
            }
        }
    }

    /**
     * 去设备详情界面
     *
     * @param urlPath
     */
    private void toDeviceDetail(String urlPath) {
        LogUtil.e("================路径："+urlPath);
        UiUtil.runInMainThread(() -> {
            dismissLoadingDialogInAct();
            Bundle bundle = new Bundle();
            bundle.putString(IntentConstant.PLUGIN_PATH, urlPath);
            bundle.putSerializable(IntentConstant.BEAN, mDeviceMultipleBean);
            //switchToActivity(DeviceDetailActivity.class, bundle);
            Intent intent = new Intent(mActivity, DeviceDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    /**
     * 插件详情失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getPluginDetailFail(int errorCode, String msg) {
        dismissLoadingDialogInAct();
        ToastUtil.show(msg);
    }

    @Override
    public void onDestroy() {
        if (mWebSocketListener != null) {
            WSocketManager.getInstance().removeWebSocketListener(mWebSocketListener);
        }
        super.onDestroy();
    }
}
