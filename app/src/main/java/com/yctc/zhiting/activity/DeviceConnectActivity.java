package com.yctc.zhiting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DeviceConnectContract;
import com.yctc.zhiting.activity.presenter.DeviceConnectPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.entity.WSBaseResponseBean;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.SocketDeviceInfoBean;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.event.AddSAEvent;
import com.yctc.zhiting.event.FinishSetHomekit;
import com.yctc.zhiting.event.HomeEvent;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;
import com.yctc.zhiting.widget.ConnectView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Response;
import okhttp3.WebSocket;

import static com.yctc.zhiting.config.Constant.wifiInfo;

/**
 * 设备连接
 */
public class DeviceConnectActivity extends MVPBaseActivity<DeviceConnectContract.View, DeviceConnectPresenter> implements DeviceConnectContract.View {

    @BindView(R.id.connectView)
    ConnectView connectView;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.tvAgain)
    TextView tvAgain;
    @BindView(R.id.ivStatus)
    ImageView ivStatus;

    private long homeId;
    private final int mSendId = 100;
    private int progress = 0;
    private String nickname;
    private String areaName;
    private String sa_lan_address;

    private Bundle bundle;
    private DBManager dbManager;
    private DeviceBean mDeviceBean;//设备对象
    private HomeCompanyBean homeCompanyBean = new HomeCompanyBean();
    private CountDownTimer mCountDownTimer;//进度计时
    private IWebSocketListener mIWebSocketListener;

    private String homekitCode;

    private boolean addFail;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_connect;
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_home_device_connect));

        initWebSocket();
    }

    @Override
    public void onBackPressed() {

        if (!TextUtils.isEmpty(homekitCode) && addFail){
            Intent intent = new Intent();
            intent.putExtra(IntentConstant.HOMEKIT_CODE_ERROR, getResources().getString(R.string.home_wrong_code));
            setResult(RESULT_OK, intent);
            finish();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        WSocketManager.getInstance().addWebSocketListener(mIWebSocketListener);
    }

    @Override
    protected void initData() {
        super.initData();
        WeakReference<Context> context = new WeakReference<>(this);
        dbManager = DBManager.getInstance(context.get());
        getUserInfo();
        startAddDevice();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        homeId = intent.getLongExtra(IntentConstant.ID, -1);
        homekitCode = intent.getStringExtra(IntentConstant.HOMEKIT_CODE);
        mDeviceBean = (DeviceBean) intent.getSerializableExtra(IntentConstant.BEAN);
        if (mDeviceBean != null) {
            String addr = mDeviceBean.getAddress();
            String port = mDeviceBean.getPort();
            if (!TextUtils.isEmpty(addr) && !TextUtils.isEmpty(port)) {
                sa_lan_address = Constant.HTTP_HEAD + addr + ":" + port;
            }
        }
    }

    /**
     * WebSocket初始化、添加监听
     */
    private void initWebSocket() {
//        mIWebSocketListener = new IWebSocketListener() {
//            @Override
//            public void onMessage(WebSocket webSocket, String text) {
//                if (!TextUtils.isEmpty(text)) {
//                    SocketDeviceInfoBean data = GsonConverter.getGson().fromJson(text, SocketDeviceInfoBean.class);
//                    SocketDeviceInfoBean.ResultBean resultBean = null;
//                    SocketDeviceInfoBean.ResultBean.DeviceBean deviceBean = null;
//                    List<SocketDeviceInfoBean.ResultBean.DeviceBean.InstancesBean> instances = null;
//                    List<SocketDeviceInfoBean.ResultBean.DeviceBean.InstancesBean.AttributesBean> attributes = null;
//                    if (data != null)
//                        resultBean = data.getResult();
//                    if (resultBean != null)
//                        deviceBean = resultBean.getDevice();
//                    if (deviceBean != null)
//                        instances = deviceBean.getInstances();
//                    if (CollectionUtil.isNotEmpty(instances)) {
//                        for (int i = 0; i < instances.size(); i++) {
//                            int instance_id = instances.get(i).getInstance_id();
//                            attributes = instances.get(i).getAttributes();
//                            if (CollectionUtil.isNotEmpty(attributes)) {
//                                for (int j = 0; j < attributes.size(); j++) {
//                                    SocketDeviceInfoBean.ResultBean.DeviceBean.InstancesBean.AttributesBean attributesBean = attributes.get(j);
//                                    if (attributesBean != null) {
//                                        String attrName = attributesBean.getAttribute();
//                                        if (attrName != null && attrName.equals(Constant.PIN)) {
//                                            if (TextUtils.isEmpty(attributesBean.getVal())) {
//                                                bundle.putSerializable(IntentConstant.BEAN, mDeviceBean);
//                                                bundle.putInt(IntentConstant.INSTANCE_ID, instance_id);
//                                                switchToActivity(SetHomeKitActivity.class, bundle);
//                                                finish();
//                                                return;
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                toSetDevicePositionActivity();
//            }
//        };
        mIWebSocketListener = new IWebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                WSBaseResponseBean<Object> data = GsonConverter.getGson().fromJson(text, new TypeToken<WSBaseResponseBean<Object>>(){}.getType());
                if (data.isSuccess()){
                    mPresenter.addDevice(mDeviceBean);
                }else {
                    addFail = true;
                    switchConnectStatus(-1);
                    cancelTimer();
                }
            }
        };
        WSocketManager.getInstance().addWebSocketListener(mIWebSocketListener);
    }

    /**
     * 添加设备
     */
    private void startAddDevice() {
        initCountDownTimer();
        switchConnectStatus(Constant.ConnectType.TYPE_CONNECTING);
        if (mDeviceBean != null) {
            UiUtil.postDelayed(() -> {
                if (TextUtils.isEmpty(homekitCode)) { // 不是homekit设备
                    mPresenter.addDevice(mDeviceBean);
                }else { // homekit设备
                    String formatStr = UiUtil.getString(R.string.device_set_attr);
                    String deviceJson = String.format(formatStr, homekitCode, 1, Constant.PIN, 1, mDeviceBean.getIdentity(), mDeviceBean.getPluginId());
                    WSocketManager.getInstance().sendMessage(deviceJson);
                }
            }, 2000);
        }
    }

    private void initCountDownTimer() {
        progress = 0;
        int maxSecond = (int) (Math.random() * 19_000 + 80_000);
        mCountDownTimer = new CountDownTimer(maxSecond, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
//                if (progress > 95) {
//                    onFinish();
//                    cancel();
//                }
                if (progress<95) {
                    progress++;
                    connectView.setProgress(progress);
                }
            }

            @Override
            public void onFinish() {

                connectView.setProgress(progress);
            }
        }.start();
    }

    @OnClick(R.id.tvAgain)
    void onClickAgain() {
        startAddDevice();
    }

    /**
     * 同步成功
     */
    @Override
    public void syncSuccess(InvitationCheckBean invitationCheckBean) {
        updateHc(false);
        delRoom();
    }

    /**
     * 同步失败
     */
    @Override
    public void syncFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 取消进度
     */
    private void cancelTimer() {
        mCountDownTimer.onFinish();
        mCountDownTimer.cancel();
    }

    /**
     * 添加设备成功
     *
     * @param data
     */
    @Override
    public void addDeviceSuccess(AddDeviceResponseBean data) {
        progress = 100;
        cancelTimer();
        switchConnectStatus(Constant.ConnectType.TYPE_SUCCESS);
        //添加成功SA
        if (mDeviceBean.getType() != null && mDeviceBean.getType().equalsIgnoreCase(Constant.DeviceType.TYPE_SA)) {
            String token = data.getUser_info().getToken();
            if (homeCompanyBean == null)
                homeCompanyBean = new HomeCompanyBean();
            homeCompanyBean.setIs_bind_sa(true);
            if (!TextUtils.isEmpty(sa_lan_address))
                homeCompanyBean.setSa_lan_address(sa_lan_address);
            homeCompanyBean.setSa_user_token(token);
            homeCompanyBean.setUser_id(data.getUser_info().getUser_id());
            IdBean idBean = data.getArea_info();
            if (idBean != null) {
                homeCompanyBean.setArea_id(idBean.getId());
            }
            if (wifiInfo != null) {
                homeCompanyBean.setSs_id(wifiInfo.getSSID());
                homeCompanyBean.setMac_address(wifiInfo.getBSSID());
            }
            homeCompanyBean.setSa_id(mDeviceBean.getSa_id());
            HttpConfig.addHeader(token);
            updateHc(true);

//            AllRequestUtil.createHomeBindSC(homeCompanyBean, null);
            AllRequestUtil.bindCloudWithoutCreateHome(homeCompanyBean, null);
            EventBus.getDefault().post(new AddSAEvent());
        } else {//添加插座、开关
            String pluginId = mDeviceBean.getPluginId();
            bundle = new Bundle();
            bundle.putInt(IntentConstant.ID, data.getDevice_id());
            bundle.putString(IntentConstant.NAME, mDeviceBean.getName());
//            if (pluginId != null && pluginId.equals(Constant.HOMEKIT)) {  // 是homekit设备
//                String formatStr = UiUtil.getString(R.string.device_info);
//                String deviceJson = String.format(formatStr, pluginId, mDeviceBean.getIdentity(), mSendId);
//                WSocketManager.getInstance().sendMessage(deviceJson);
//            } else {
//                toSetDevicePositionActivity();
//            }
            toSetDevicePositionActivity();
        }
    }

    /**
     * 设置设备位置界面
     */
    private void toSetDevicePositionActivity() {
        EventBus.getDefault().post(new FinishSetHomekit());
        switchToActivity(SetDevicePositionActivity.class, bundle);
        finish();
    }

    /**
     * 添加设备失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void addDeviceFail(int errorCode, String msg) {
        addFail = true;
        cancelTimer();
        ToastUtil.show(msg);
        switchConnectStatus(Constant.ConnectType.TYPE_FAILED);
    }

    @Override
    public void bindCloudSuccess() {

    }

    @Override
    public void bindCloudFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 连接状态改变
     * d
     *
     * @param type -1失败 0连接中 1连接成功
     */
    private void switchConnectStatus(int type) {
        if (type == 0) {
            tvAgain.setVisibility(View.GONE);
            ivStatus.setVisibility(View.GONE);
            tvStatus.setTextColor(UiUtil.getColor(R.color.color_94A5BE));
            tvStatus.setText(UiUtil.getString(R.string.mine_home_device_connecting));
        } else if (type == 1) {
            tvAgain.setVisibility(View.GONE);
            ivStatus.setVisibility(View.VISIBLE);
            tvStatus.setTextColor(UiUtil.getColor(R.color.color_3f4663));
            tvStatus.setText(UiUtil.getString(R.string.mine_home_device_connect_successful));
        } else {
            ivStatus.setVisibility(View.GONE);
            String pluginId = mDeviceBean.getPluginId();
            if (TextUtils.isEmpty(pluginId) || !pluginId.equals(Constant.HOMEKIT))
            tvAgain.setVisibility(View.VISIBLE);
            tvStatus.setTextColor(UiUtil.getColor(R.color.color_FE0000));
            tvStatus.setText(UiUtil.getString(R.string.mine_home_device_connect_failed));
        }
    }

    /**
     * 加载家庭信息
     */
    private void loadHC() {
        UiUtil.starThread(() -> {
            homeCompanyBean = dbManager.queryHomeCompanyById(homeId);
            if (homeCompanyBean != null) {
                areaName = homeCompanyBean.getName();
            }
        });
    }

    /**
     * 修改家庭
     */
    private void updateHc(boolean loadRoom) {
        UiUtil.starThread(() -> {
            int count = dbManager.updateHomeCompany(homeCompanyBean);
            if (count > 0) {
                UiUtil.runInMainThread(() -> {
                    if (loadRoom) {
                        loadRoomData();
                    } else {
                        HttpUrlConfig.baseSAUrl = homeCompanyBean.getSa_lan_address();
                        HttpUrlConfig.apiSAUrl = HttpUrlConfig.baseSAUrl + HttpUrlConfig.API;
                        EventBus.getDefault().post(new HomeEvent(true, homeCompanyBean));
                        switchToActivity(MainActivity.class);
                    }
                });
            }
        });
    }

    /**
     * 删除房间
     */
    private void delRoom() {
        UiUtil.starThread(() -> {
            dbManager.removeLocationByHId(homeId, -1);
            UiUtil.runInMainThread(() -> DeviceConnectActivity.this.finish());
        });
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        UiUtil.starThread(() -> {
            UserInfoBean userInfoBean = dbManager.getUser();
            if (userInfoBean != null) {
                UiUtil.runInMainThread(() -> {
                    nickname = userInfoBean.getNickname();
                    loadHC();
                });
            }
        });
    }

    /**
     * 从数据库查询数据
     */
    private void loadRoomData() {
        UiUtil.starThread(() -> {
            List<LocationBean> list = dbManager.queryLocationList(homeId);
            UiUtil.runInMainThread(() -> {
                SynPost.AreaBean areaBean = new SynPost.AreaBean(areaName, list);
                SynPost synPost = new SynPost(nickname, areaBean);
                String body = new Gson().toJson(synPost);
                mPresenter.sync(body, sa_lan_address);
            });
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTimer();
        WSocketManager.getInstance().removeWebSocketListener(mIWebSocketListener);
    }
}