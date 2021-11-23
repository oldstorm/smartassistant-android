package com.yctc.zhiting.activity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.fileutil.BaseFileUtil;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AddDeviceContract;
import com.yctc.zhiting.activity.presenter.AddDevicePresenter;
import com.yctc.zhiting.adapter.AddDeviceAdapter;
import com.yctc.zhiting.adapter.AddDeviceInCategoryAdapter;
import com.yctc.zhiting.adapter.DeviceCategoryAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.config.HttpUrlParams;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.entity.GetDeviceInfoBean;
import com.yctc.zhiting.entity.ScanDeviceByUDPBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.DeviceTypeBean;
import com.yctc.zhiting.entity.home.DeviceTypeDeviceBean;
import com.yctc.zhiting.entity.home.DeviceTypeListBean;
import com.yctc.zhiting.entity.home.FindDeviceBean;
import com.yctc.zhiting.entity.home.ScanResultBean;
import com.yctc.zhiting.entity.mine.CheckBindSaBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.utils.AESUtil;
import com.yctc.zhiting.utils.BluetoothUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.Md5Util;
import com.yctc.zhiting.utils.SpacesItemDecoration;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.utils.ZipUtils;
import com.yctc.zhiting.utils.udp.ByteUtil;
import com.yctc.zhiting.utils.udp.UDPSocket;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;
import com.yctc.zhiting.widget.RadarScanView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * 发现设备
 */
public class ScanDevice2Activity extends MVPBaseActivity<AddDeviceContract.View, AddDevicePresenter> implements AddDeviceContract.View {

    @BindView(R.id.radarScanView)
    RadarScanView radarScanView;
    @BindView(R.id.ivReport)
    ImageView ivReport;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.tvAgain)
    TextView tvAgain;
    @BindView(R.id.rvDevice)
    RecyclerView rvDevice;
    @BindView(R.id.rvDeviceCategory)
    RecyclerView rvDeviceCategory;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.rvDeviceInCategory)
    RecyclerView rvDeviceInCategory;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @BindView(R.id.radarScanViewSmall)
    RadarScanView radarScanViewSmall;
    @BindView(R.id.line1)
    View viewLine1;
    @BindView(R.id.line2)
    View viewLine2;

    private static final int REQUEST_PERMISSION = 0x01;
    private boolean again;
    private AddDeviceAdapter addDeviceAdapter;
    private DeviceCategoryAdapter mDeviceCategoryAdapter;
    private AddDeviceInCategoryAdapter mAddDeviceInCategoryAdapter;
    private List<DeviceBean> mScanLists = new ArrayList<>();
    private CountDownTimer mCountDownTimer;

    private long homeId;
    private long mFindDeviceId;//发现设备id
    private IWebSocketListener mIWebSocketListener;

    private BluetoothScanCallback mBluetoothScanCallback;  // 扫描蓝牙回调
    private boolean needLoadBluetooth = false; // 是否需要加载蓝牙
    private Set<String> blueDeviceAdded; // 存储已存储的蓝牙设备
    private Set<String> updAddressSet; // 存储已获取upd设备
    private HashMap<String, String> passwordMap; // 保存的密码
    private ConcurrentHashMap<String, ScanDeviceByUDPBean> scanMap = new ConcurrentHashMap<>();  // 存储udp扫描的设备信息
    private UDPSocket udpSocket; // 发现sa udpsocket
    private DeviceTypeDeviceBean mDeviceTypeDeviceBean;  // 点击的那个设备
    private final String BLUETOOTH_PLUGIN_ID = "zhiting";  // 蓝牙设备固定的pluginId

    private CenterAlertDialog alertDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_device2;
    }

    @Override
    protected void initUI() {
        super.initUI();
        initDeviceRv();
        initRvDeviceCategory();
        initRvDeviceInCategory();
        initRadarScanView();
        initCountDownTimer();
        initAlertDialog();
        blueDeviceAdded = new HashSet<>();
        updAddressSet = new HashSet<>();
        passwordMap = new HashMap<>();
        mBluetoothScanCallback = new BluetoothScanCallback();
        if (Constant.CurrentHome != null && Constant.CurrentHome.isIs_bind_sa()) {//绑定有SA
            initWebSocket();
        } else {
//            mPresenter.checkBindSa();
        }
        mPresenter.getDeviceType();
        initUDPScan();
        initBlueToothScan();
    }

    /**
     * 不在局域网提示弹窗
     */
    private void initAlertDialog() {
        alertDialog = CenterAlertDialog.newInstance(UiUtil.getString(R.string.please_add_intelligent_center_or_login_before_adding_device), getResources().getString(R.string.home_cancel), getResources().getString(R.string.home_go_to_login), false);
        alertDialog.setConfirmListener(del -> {
            alertDialog.dismiss();
            switchToActivity(LoginActivity.class);
            finish();
        });
    }

    /**
     * upd发现设备
     */
    private void initUDPScan() {
        udpSocket = new UDPSocket(Constant.FIND_DEVICE_URL, Constant.FIND_DEVICE_PORT,
                new UDPSocket.OnReceiveCallback() {
                    @Override
                    public void onReceiveByteData(String address, int port, byte[] data, int length) {
                        scanDeviceSuccessByUDP(address, port, data, length);

                    }

                    @Override
                    public void onReceive(String msg) {

                    }
                });
        udpSocket.startUDPSocket();
        // 发送hello包数据，固定
        byte[] sendHelloData = {(byte) 0x21, (byte) 0x31, (byte) 0x00, (byte) 0x20, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        System.out.println("最后发送hello包数据：" + Arrays.toString(sendHelloData));
        udpSocket.sendMessage(sendHelloData, Constant.FIND_DEVICE_URL);
    }

    /**
     * 处理udp发现的设备
     *
     * @param address
     * @param port
     * @param data
     * @param length
     */
    private void scanDeviceSuccessByUDP(String address, int port, byte[] data, int length) {
        byte[] deviceIdData = Arrays.copyOfRange(data, 6, 12);  // 设备id
        try {
            String deviceId = ByteUtil.bytesToHex(deviceIdData);
            if (scanMap.containsKey(deviceId)) {  // 已经获取到hello数据包信息
                ScanDeviceByUDPBean sdb = scanMap.get(deviceId);
                String token = sdb.getToken();
                byte[] dealData = Arrays.copyOfRange(data, 32, length);
                if (TextUtils.isEmpty(token)) { // 没有获取过token信息
                    String key = sdb.getPassword();
                    String decryptKeyMD5 = Md5Util.getMD5(key);
                    byte[] decryptKeyDta = ByteUtil.md5Str2Byte(decryptKeyMD5);
                    byte[] ivData = ByteUtil.byteMergerAll(decryptKeyDta, key.getBytes());
                    byte[] ivEncryptedData = Md5Util.getMD5(ivData);
                    String tokenFromServer = AESUtil.decryptAES(dealData, decryptKeyDta, ivEncryptedData, AESUtil.PKCS7, true);
                    if (!TextUtils.isEmpty(tokenFromServer)) {
                        sdb.setToken(tokenFromServer);
                        long id = System.currentTimeMillis();
                        sdb.setId(id);
                        String deviceStr = "{\"method\":\"get_prop.info\",\"params\":[],\"id\":" + id + "}";  // 获取设备信息体
                        byte[] bodyData = AESUtil.encryptAES(deviceStr.getBytes(), tokenFromServer, AESUtil.PKCS7); // 获取设备信息体转字节加密
                        int len = bodyData.length + 32;  // 包长
                        byte[] lenData = ByteUtil.intToByte2(len);  // 包长用占两位字节
                        byte[] headData = {(byte) 0x21, (byte) 0x31}; // 包头固定
                        byte[] preData = {(byte) 0xFF, (byte) 0xFF}; // 预留固定
                        byte[] serData = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; // 序列号固定
                        byte[] tokenData = sdb.getPassword().getBytes();  // 之前获取设备信息时生成的16位随机密码
                        byte[] getDeviceInfoData = ByteUtil.byteMergerAll(headData, lenData, preData, deviceIdData, serData, tokenData, bodyData); //  拼接获取设备信息包
                        System.out.println(address + "获取设备信息发送数据：" + Arrays.toString(getDeviceInfoData));
                        udpSocket.sendMessage(getDeviceInfoData, address);
                    }
                } else { // 获取过token信息
                    GetDeviceInfoBean deviceInfoBean = sdb.getDeviceInfoBean();
                    byte[] decryptDeviceData = Md5Util.getMD5(ByteUtil.md5Str2Byte(token));
                    byte[] ivDeviceData = ByteUtil.byteMergerAll(decryptDeviceData, ByteUtil.md5Str2Byte(token));
                    byte[] ivEncryptedDeviceData = Md5Util.getMD5(ivDeviceData);
                    System.out.println("设备信息字节：" + Arrays.toString(dealData));
                    System.out.println("=========处理数据长度：" + dealData.length + "      " + length);
                    String infoJson = AESUtil.decryptAES(dealData, decryptDeviceData, ivEncryptedDeviceData, AESUtil.PKCS7, false);
                    System.out.println("设备信息：" + infoJson);
                    GetDeviceInfoBean getDeviceInfoBean = new Gson().fromJson(infoJson, GetDeviceInfoBean.class);

                    if (deviceInfoBean == null && getDeviceInfoBean != null && sdb.getId() == getDeviceInfoBean.getId()) {
                        GetDeviceInfoBean.ResultBean gdifb = getDeviceInfoBean.getResult();
                        sdb.setDeviceInfoBean(getDeviceInfoBean);
                        DeviceBean deviceBean = new DeviceBean();
                        deviceBean.setAddress(sdb.getHost());
                        if (gdifb != null) {
                            deviceBean.setPort(gdifb.getPort());
                            String model = gdifb.getModel();
                            if (!TextUtils.isEmpty(model) && model.equals(Constant.SMART_ASSISTANT)) { // 如果时sa设备
                                deviceBean.setType(Constant.DeviceType.TYPE_SA);
                                deviceBean.setModel(gdifb.getModel());
                                deviceBean.setName(gdifb.getSa_id());
                                deviceBean.setSa_id(gdifb.getSa_id());
                                deviceBean.setSwVersion(gdifb.getSw_ver());
                                checkIsBind(deviceBean);
                            }
                        }
                    }
                }
            } else {  // 获取到hello数据包信息
                ScanDeviceByUDPBean scanDeviceByUDPBean = new ScanDeviceByUDPBean(address, port, deviceId);
                String password = StringUtil.getUUid().substring(0,16);
                scanDeviceByUDPBean.setPassword(password);
                scanMap.put(deviceId, scanDeviceByUDPBean);
                getDeviceToken(address, data, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查通过udp发现的sa是否已被绑定
     */
    private void checkIsBind(DeviceBean deviceBean) {
        HttpConfig.clearHear(HttpConfig.AREA_ID);
        String url = Constant.HTTP_HEAD + deviceBean.getAddress() + ":" + deviceBean.getPort() + HttpUrlConfig.API + HttpUrlParams.checkBindSa;
        HTTPCaller.getInstance().get(CheckBindSaBean.class, url,
                new RequestDataCallback<CheckBindSaBean>() {
                    @Override
                    public void onSuccess(CheckBindSaBean obj) {
                        super.onSuccess(obj);
                        if (obj != null) {
                            deviceBean.setBind(obj.isIs_bind());
                            mScanLists.add(deviceBean);
                            addDeviceAdapter.notifyDataSetChanged();
                            setHasDeviceStatus();
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        LogUtil.e(errorMessage);
                    }
                });
    }

    /**
     * 获取设备token
     *
     * @param address
     * @param data
     */
    private void getDeviceToken(String address, byte[] data, String password) {
        if (!updAddressSet.contains(address)) {
            updAddressSet.add(address);
            byte[] tokenHeadData = {(byte) 0x21, (byte) 0x31, (byte) 0x00, (byte) 0x20, (byte) 0xFF, (byte) 0xFE}; // 包头，包长，预留字节固定
            byte[] deviceIdData = Arrays.copyOfRange(data, 6, 12);  // 设备id
            byte[] passwordData = password.getBytes();  // 密码
            byte[] serData = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; // 序列号 固定
            byte[] tokenData = ByteUtil.byteMergerAll(tokenHeadData, deviceIdData, serData, passwordData);  // 拼接获取token数据包
            udpSocket.sendMessage(tokenData, address);
        }
    }

    /**
     * 检查蓝牙
     */
    private void initBlueToothScan() {
        if (BluetoothUtil.hasBlueTooth()) {
            if (BluetoothUtil.isEnabled()) {
                checkLocationPermission();
            } else {
                CenterAlertDialog alertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.home_blue_tooth_disabled), getResources().getString(R.string.home_guide_user_to_open_bluetooth),
                        getResources().getString(R.string.home_cancel), getResources().getString(R.string.home_setting), false);
                alertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean del) {
//                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        needLoadBluetooth = true;
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show(this);
            }
        }
    }

    /**
     * 权限结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int size = permissions.length;
        for (int i = 0; i < size; ++i) {
            String permission = permissions[i];
            int grant = grantResults[i];

            if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grant == PackageManager.PERMISSION_GRANTED) {
                    scanBluetoothDevice();
                }
            }
        }
    }

    /**
     * 定位权限
     */
    private void checkLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
    }

    /**
     * 计时
     */
    private void initCountDownTimer() {
        mCountDownTimer = new CountDownTimer(30_000, 800) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.e("initCountDownTimer=" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                LogUtil.e("initCountDownTimer=onFinish=");
                if (mScanLists.size() == 0) {
                    tvAgain.setVisibility(View.VISIBLE);
                    tvStatus.setText(UiUtil.getString(R.string.mine_home_not_find_device));
                    ivReport.setVisibility(View.VISIBLE);
                    radarScanView.stop();
                    radarScanViewSmall.stop();
                    tvAgain.setVisibility(View.VISIBLE);
                    rvDevice.setVisibility(View.GONE);
                }
                BluetoothUtil.stopScanBluetooth(mBluetoothScanCallback);
                if (udpSocket != null) {
                    udpSocket.stopUDPSocket();
                }
            }
        }.start();
    }

    /**
     * WebSocket初始化、添加监听
     */
    private void initWebSocket() {
        mIWebSocketListener = new IWebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if (!TextUtils.isEmpty(text)) {
                    ScanResultBean scanBean = GsonConverter.getGson().fromJson(text, ScanResultBean.class);
                    if (scanBean != null && scanBean.getResult() != null && scanBean.getResult().getDevice() != null) {
                        DeviceBean bean = scanBean.getResult().getDevice();
                        mScanLists.add(bean);
                        addDeviceAdapter.notifyDataSetChanged();
                        setHasDeviceStatus();
                    }
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            }
        };
        WSocketManager.getInstance().addWebSocketListener(mIWebSocketListener);
        startScanDevice();
    }

    /**
     * 扫描到有设备
     */
    private void setHasDeviceStatus() {
        if (radarScanViewSmall.getVisibility() == View.GONE) {
            radarScanView.stop();
            radarScanViewSmall.setVisibility(View.VISIBLE);
            radarScanViewSmall.start();
            nestedScrollView.setVisibility(View.GONE);
            tvAgain.setTextColor(UiUtil.getColor(R.color.color_94A5BE));
            tvAgain.setText(getResources().getString(R.string.mine_home_scanning));
            tvAgain.setVisibility(View.VISIBLE);
            rvDevice.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 开始发现设备
     */
    private void startScanDevice() {
        tvStatus.setText(UiUtil.getString(R.string.mine_home_scanning));
        FindDeviceBean findDeviceBean = new FindDeviceBean();
        findDeviceBean.setDomain("plugin");
        findDeviceBean.setService("discover");
        findDeviceBean.setId(mFindDeviceId);
        String findDeviceJson = GsonConverter.getGson().toJson(findDeviceBean);
        UiUtil.postDelayed(() -> WSocketManager.getInstance().sendMessage(findDeviceJson), 1000);
        mFindDeviceId++;
    }

    /**
     * 初始化扫描动画监听
     */
    private void initRadarScanView() {
        radarScanView.setScanListener(new RadarScanView.OnScanListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onStop() {
            }
        });
    }


    @Override
    protected void initData() {
        super.initData();
        homeId = getIntent().getLongExtra(IntentConstant.ID, -1);
        LogUtil.e("homeId1=" + homeId);
    }

    /**
     * 设置列表
     */
    private void initDeviceRv() {
        addDeviceAdapter = new AddDeviceAdapter(mScanLists);
        rvDevice.setLayoutManager(new LinearLayoutManager(this));
        rvDevice.setAdapter(addDeviceAdapter);
        addDeviceAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            DeviceBean bean = mScanLists.get(position);
            if (bean.getType() != null && bean.getType().equalsIgnoreCase(Constant.DeviceType.TYPE_SA)) {
                if (bean.isBind()) {//已经被绑定过，扫码加入家庭(SA)
                    //switchToActivity(ScanActivity.class);
                    switchToActivity(CaptureNewActivity.class);
                } else {//添加SA
                    addDevice(bean);
                }
            } else {//添加设备(灯和开关)
                if (HomeUtil.isSAEnvironment() || UserUtils.isLogin()) {
                    if (bean.getBluetoothDevice() == null) { // 非蓝牙设备
                        addDevice(bean);
                    } else {  // 蓝牙设备
                        String name = bean.getName();
                        String provisioning = "html/index.html#/h5?mode=bluetooth_softap&bluetooth_name=" + name + "&hotspot_name=" + name;
                        mDeviceTypeDeviceBean = new DeviceTypeDeviceBean(name, name, "", "", provisioning, BLUETOOTH_PLUGIN_ID);
                        mPresenter.getPluginDetail(BLUETOOTH_PLUGIN_ID);
                    }
                }else {
                    if (alertDialog!=null && !alertDialog.isShowing()){
                        alertDialog.show(this);
                    }
                }
            }
        });
    }

    /**
     * 设备分类
     */
    private void initRvDeviceCategory() {
        mDeviceCategoryAdapter = new DeviceCategoryAdapter();
        rvDeviceCategory.setLayoutManager(new LinearLayoutManager(this));
        rvDeviceCategory.setAdapter(mDeviceCategoryAdapter);

        mDeviceCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DeviceTypeBean deviceTypeBean = mDeviceCategoryAdapter.getItem(position);
                for (int i = 0; i < mDeviceCategoryAdapter.getData().size(); i++) {
                    mDeviceCategoryAdapter.getData().get(i).setSelected(false);
                }
                deviceTypeBean.setSelected(true);
                setSelData(deviceTypeBean);
                mDeviceCategoryAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 分类中的设备
     */
    private void initRvDeviceInCategory() {
        mAddDeviceInCategoryAdapter = new AddDeviceInCategoryAdapter();
        rvDeviceInCategory.setLayoutManager(new GridLayoutManager(this, 3));
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, 5);
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, 5);
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, 5);
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, 5);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        rvDeviceInCategory.addItemDecoration(spacesItemDecoration);
        rvDeviceInCategory.setAdapter(mAddDeviceInCategoryAdapter);
        mAddDeviceInCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (HomeUtil.isSAEnvironment() || UserUtils.isLogin()) {
                    DeviceTypeDeviceBean deviceTypeDeviceBean = mAddDeviceInCategoryAdapter.getItem(position);
                    mDeviceTypeDeviceBean = deviceTypeDeviceBean;
                    if (!TextUtils.isEmpty(deviceTypeDeviceBean.getPlugin_id())) {
                        mPresenter.getPluginDetail(deviceTypeDeviceBean.getPlugin_id());
                    }
                }else {
                    if (alertDialog!=null && !alertDialog.isShowing()){
                        alertDialog.show(ScanDevice2Activity.this);
                    }
                }
            }
        });
    }

    /**
     * 添加开关
     */
    private void addDevice(DeviceBean bean) {
        Bundle bundle = new Bundle();
        bundle.putLong(IntentConstant.ID, homeId);
        bundle.putSerializable(IntentConstant.BEAN, bean);
        String pluginId = bean.getPluginId();
        switchToActivity(pluginId != null && pluginId.equals(Constant.HOMEKIT) ? SetHomeKitActivity.class : DeviceConnectActivity.class, bundle);
    }

    /**
     * 重新扫描
     */
    @OnClick(R.id.tvAgain)
    void onClickAgain() {
        again = true;
        tvAgain.setVisibility(View.GONE);
        ivReport.setVisibility(View.GONE);
        mCountDownTimer.start();
        radarScanView.start();
        startScanDevice();
        scanBluetoothDevice();
    }

    @OnClick(R.id.ivBack)
    void onClickBack() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (radarScanView != null && radarScanView.getVisibility() == View.VISIBLE)
            radarScanView.start();
        if (radarScanViewSmall != null && radarScanViewSmall.getVisibility() == View.VISIBLE)
            radarScanViewSmall.start();
        if (needLoadBluetooth) {
            checkLocationPermission();
            needLoadBluetooth = false;
        }

    }

    /**
     * 扫描蓝牙设备
     */
    private void scanBluetoothDevice() {
        BluetoothUtil.startScanBluetooth(mBluetoothScanCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        radarScanView.stop();
        radarScanViewSmall.stop();
        if (!needLoadBluetooth) {
            if (mCountDownTimer != null) {
                mCountDownTimer.onFinish();
                mCountDownTimer.cancel();
            }
            WSocketManager.getInstance().removeWebSocketListener(mIWebSocketListener);
            if (udpSocket != null) {
                udpSocket.stopUDPSocket();
            }
            BluetoothUtil.stopScanBluetooth(mBluetoothScanCallback);
        }
        LogUtil.e("mCountDownTimer=onPause=");
    }


    @Override
    public void checkBindSaSuccess(CheckBindSaBean data) {
        LogUtil.e("checkBindSaSuccess==" + data.isIs_bind());
        initDefaultSA(data.isIs_bind());
    }

    @Override
    public void checkBindSaFail(int errorCode, String msg) {
        LogUtil.e("checkBindSaFail==errorCode=" + errorCode + ",msg=" + msg);
    }

    /**
     * 设备分类列表成功
     *
     * @param deviceTypeListBean
     */
    @Override
    public void getDeviceTypeSuccess(DeviceTypeListBean deviceTypeListBean) {
        if (deviceTypeListBean != null) {
            List<DeviceTypeBean> types = deviceTypeListBean.getTypes();
            if (CollectionUtil.isNotEmpty(types)) {
                mDeviceCategoryAdapter.setNewData(types);
                DeviceTypeBean deviceTypeBean = types.get(0);
                deviceTypeBean.setSelected(true);
                setSelData(deviceTypeBean);
                viewLine1.setVisibility(View.VISIBLE);
                viewLine2.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置选择的数据
     *
     * @param deviceTypeBean
     */
    private void setSelData(DeviceTypeBean deviceTypeBean) {
        List<DeviceTypeDeviceBean> devices = deviceTypeBean.getDevices();
        tvName.setText(deviceTypeBean.getName());
        mAddDeviceInCategoryAdapter.setNewData(devices);
    }

    /**
     * 设备分类列表失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDeviceTypeFail(int errorCode, String msg) {
        ToastUtil.show(msg);
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
                pluginFilePath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/plugins/" + pluginId;
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
                if (mDeviceTypeDeviceBean!=null && BaseFileUtil.isFileExist(pluginFilePath) &&
                        !TextUtils.isEmpty(cacheVersion) && !TextUtils.isEmpty(version) && cacheVersion.equals(version)) {  // 如果缓存存在且版本相同
                    String urlPath = "file://"+pluginFilePath+"/"+mDeviceTypeDeviceBean.getProvisioning();
                    toConfigNetwork(urlPath);
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
                            path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/plugins/";
                        }else {
                            path = Constant.PLUGIN_PATH;
                        }
                        String finalPath = path;
                        String finalPluginFilePath = pluginFilePath;
                        HTTPCaller.getInstance().downloadFile(downloadUrl, path, pluginId, headers.toArray(new Header[headers.size()]), UiUtil.getString(R.string.home_download_plugin_package_fail),
                                new ProgressUIListener() {
                                @Override
                                public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                                    LogUtil.e("进度：" + percent);
                                    if (percent == 1) {
                                        LogUtil.e("下载完成");
                                        try {
                                            ZipUtils.decompressFile(new File(fileZipPath), finalPath, true);
                                            String pluginsBeanToJson = GsonConverter.getGson().toJson(pluginsBean);
                                            SpUtil.put(pluginId, pluginsBeanToJson);
                                            String urlPath = "file://"+ finalPluginFilePath +"/"+mDeviceTypeDeviceBean.getProvisioning();
                                            toConfigNetwork(urlPath);
                                        } catch (IOException e) {
                                            e.printStackTrace();
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
     * 去配网界面
     * @param urlPath
     */
    private void toConfigNetwork(String urlPath){
        Bundle bundle = new Bundle();
        bundle.putString(IntentConstant.PLUGIN_PATH, urlPath);
        bundle.putSerializable(IntentConstant.BEAN, mDeviceTypeDeviceBean);
        switchToActivity(ConfigNetworkWebActivity.class, bundle);
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

    private void initDefaultSA(boolean isBind) {
        String json1 = "{\"address\":\"192.168.0.188\", \"port\":\"8088\", \"identity\":\"0x00000000157b4d9c\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"WeiJie'SA\",\"plugin_id\":\"light_001\",\"sw_version\":\"17\",\"type\":\"sa\"}";
        String json2 = "{\"address\":\"192.168.0.112\", \"port\":\"8088\",\"identity\":\"0x00000000157b4d9d\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"MaJian'SA\",\"plugin_id\":\"light_002\",\"sw_version\":\"18\",\"type\":\"sa\"}";
        String json3 = "{\"address\":\"192.168.0.165\", \"port\":\"8089\",\"identity\":\"0x00000000157b4d9e\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"LiHong'SA\",\"plugin_id\":\"light_003\",\"sw_version\":\"19\",\"type\":\"sa\"}";
        String json4 = "{\"address\":\"192.168.22.123\", \"port\":\"9020\",\"identity\":\"0x00000000157b4d9f\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"CeShi'SA\",\"plugin_id\":\"light_004\",\"sw_version\":\"120\",\"type\":\"sa\"}";
        String json5 = "{\"address\":\"192.168.0.84\", \"port\":\"8088\",\"identity\":\"0x00000000157b4d9f\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"CeShi1'SA\",\"plugin_id\":\"light_004\",\"sw_version\":\"120\",\"type\":\"sa\"}";
        String json6 = "{\"address\":\"192.168.0.82\", \"port\":\"8088\",\"identity\":\"0x00000000157b4d9f\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"CeShi2'SA\",\"plugin_id\":\"light_004\",\"sw_version\":\"120\",\"type\":\"sa\"}";
        String json7 = "{\"address\":\"192.168.0.182\", \"port\":\"8088\",\"identity\":\"0x00000000157b4d9e\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"LiHong'SA\",\"plugin_id\":\"light_003\",\"sw_version\":\"19\",\"type\":\"sa\"}";
        String json8 = "{\"address\":\"192.168.22.76\", \"port\":\"8088\",\"identity\":\"0x00000000157b4d9e\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"LiHong'SA\",\"plugin_id\":\"light_003\",\"sw_version\":\"19\",\"type\":\"sa\"}";
        String json9 = "{\"address\":\"192.168.22.86\", \"port\":\"37965\",\"identity\":\"0x00000000157b4d9e\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"LiHong'SA\",\"plugin_id\":\"light_003\",\"sw_version\":\"19\",\"type\":\"sa\"}";
        DeviceBean bean1 = GsonConverter.getGson().fromJson(json1, DeviceBean.class);
        DeviceBean bean2 = GsonConverter.getGson().fromJson(json2, DeviceBean.class);
        DeviceBean bean3 = GsonConverter.getGson().fromJson(json3, DeviceBean.class);
        DeviceBean bean4 = GsonConverter.getGson().fromJson(json4, DeviceBean.class);
        DeviceBean bean5 = GsonConverter.getGson().fromJson(json5, DeviceBean.class);
        DeviceBean bean6 = GsonConverter.getGson().fromJson(json6, DeviceBean.class);
        DeviceBean bean7 = GsonConverter.getGson().fromJson(json7, DeviceBean.class);
        DeviceBean bean8 = GsonConverter.getGson().fromJson(json8, DeviceBean.class);
        DeviceBean bean9 = GsonConverter.getGson().fromJson(json9, DeviceBean.class);

        if (HttpUrlConfig.apiSAUrl.contains(bean1.getAddress())) {
            mScanLists.add(bean1);
            bean1.setBind(isBind);
        } else if (HttpUrlConfig.apiSAUrl.contains(bean2.getAddress())) {
            mScanLists.add(bean2);
            bean2.setBind(isBind);
        } else if (HttpUrlConfig.apiSAUrl.contains(bean3.getAddress())) {
            mScanLists.add(bean3);
            bean3.setBind(isBind);
        } else if (HttpUrlConfig.apiSAUrl.contains(bean4.getAddress())) {
            mScanLists.add(bean4);
            bean4.setBind(isBind);
        } else if (HttpUrlConfig.apiSAUrl.contains(bean5.getAddress())) {
            mScanLists.add(bean5);
            bean5.setBind(isBind);
        } else if (HttpUrlConfig.apiSAUrl.contains(bean6.getAddress())) {
            mScanLists.add(bean6);
            bean6.setBind(isBind);
        } else if (HttpUrlConfig.apiSAUrl.contains(bean7.getAddress())) {
            mScanLists.add(bean7);
            bean7.setBind(isBind);
        } else if (HttpUrlConfig.apiSAUrl.contains(bean8.getAddress())) {
            mScanLists.add(bean8);
            bean8.setBind(isBind);
        } else if (HttpUrlConfig.apiSAUrl.contains(bean9.getAddress())) {
            mScanLists.add(bean9);
            bean9.setBind(isBind);
        }
        addDeviceAdapter.notifyDataSetChanged();
        setHasDeviceStatus();
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    private class BluetoothScanCallback extends ScanCallback {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                onLeScan(result);
            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            onLeScan(result);
        }

        private void onLeScan(ScanResult scanResult) {
            if (scanResult != null) {
                BluetoothDevice bluetoothDevice = scanResult.getDevice();
                if (bluetoothDevice != null) {
                    String name = bluetoothDevice.getName();
                    if (TextUtils.isEmpty(name)) {
                        return;
                    }
                    if (!TextUtils.isEmpty(BluetoothUtil.BLUFI_PREFIX)) {
                        if (!name.startsWith(BluetoothUtil.BLUFI_PREFIX) || blueDeviceAdded.contains(name)) {
                            return;
                        }
                    }
                    blueDeviceAdded.add(bluetoothDevice.getName());
                    DeviceBean deviceBean = new DeviceBean();
                    deviceBean.setName(scanResult.getDevice().getName());
                    deviceBean.setBluetoothDevice(bluetoothDevice);
                    mScanLists.add(deviceBean);
                    addDeviceAdapter.notifyDataSetChanged();
                    setHasDeviceStatus();
                }
            }
        }
    }

}