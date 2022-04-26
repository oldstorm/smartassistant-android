package com.yctc.zhiting.activity;

import static com.yctc.zhiting.config.Constant.CurrentHome;
import static com.yctc.zhiting.config.Constant.wifiInfo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
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
import com.yctc.zhiting.dialog.FindCertificateDialog;
import com.yctc.zhiting.entity.FindCertificateBean;
import com.yctc.zhiting.entity.ws_request.WSAuthParamsBean;
import com.yctc.zhiting.entity.ws_request.WSConstant;
import com.yctc.zhiting.entity.ws_response.WSDeviceResponseBean;
import com.yctc.zhiting.entity.ws_response.WSBaseResponseBean;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.AreaMoveUrlBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.entity.ws_request.WSDeviceRequest;
import com.yctc.zhiting.entity.ws_request.WSRequest;
import com.yctc.zhiting.entity.ws_response.WSDeviceBean;
import com.yctc.zhiting.event.FinishSetHomekit;
import com.yctc.zhiting.event.HomeEvent;
import com.yctc.zhiting.request.AreaMoveRequest;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;
import com.yctc.zhiting.widget.ConnectView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.WebSocket;

/**
 * 设备连接
 */
public class DeviceConnectActivity extends MVPBaseActivity<DeviceConnectContract.View, DeviceConnectPresenter> implements DeviceConnectContract.View {

    @BindView(R.id.connectView)
    ConnectView connectView;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.tvAddFailDesc)
    TextView tvAddFailDesc;
    @BindView(R.id.tvAgain)
    TextView tvAgain;
    @BindView(R.id.ivStatus)
    ImageView ivStatus;

    private long homeId;
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
    private String cloudToken;
    private boolean isHomeKitFailed;//是否是HomeKit连接失败
    private String mHomeKitFailedTitle;//HomeKit连接失败title
    private ConcurrentHashMap<String, WSRequest> requestHashMap = new ConcurrentHashMap<>();

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
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(homekitCode) && addFail) {
            Intent intent = new Intent();
            intent.putExtra(IntentConstant.HOMEKIT_CODE_ERROR, mHomeKitFailedTitle);
            setResult(RESULT_OK, intent);
            finish();
        } else {
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
        initWebSocket();
        mDeviceBean = (DeviceBean) intent.getSerializableExtra(IntentConstant.BEAN);
        cloudToken = CurrentHome.getSa_user_token();
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
        mIWebSocketListener = new IWebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                LogUtil.e("initWebSocket添加设备结果："+text);
                WSBaseResponseBean<WSDeviceResponseBean> responseBean = GsonConverter.getGson().fromJson(text, new TypeToken<WSBaseResponseBean<WSDeviceResponseBean>>() {
                }.getType());
                if (responseBean != null) {

                    if (requestHashMap.containsKey(String.valueOf(responseBean.getId()))) {
                        if (responseBean.isSuccess()) {
                            WSDeviceResponseBean addDeviceResponseBean = responseBean.getData();
                            if (addDeviceResponseBean != null) {
                                WSDeviceBean deviceBean = addDeviceResponseBean.getDevice();
                                if (deviceBean != null) {
                                    switchConnectStatus(Constant.ConnectType.TYPE_SUCCESS);
                                    progress = 100;
                                    cancelTimer();
                                    bundle = new Bundle();
                                    bundle.putInt(IntentConstant.ID, deviceBean.getId());
                                    bundle.putString(IntentConstant.PLUGIN_ID, deviceBean.getPlugin_id());
                                    bundle.putString(IntentConstant.NAME, mDeviceBean.getName());
                                    bundle.putString(IntentConstant.CONTROL, deviceBean.getControl());
                                    toSetDevicePositionActivity();
                                }
                            }
                        } else {
                            WSBaseResponseBean.ErrorBean errorBean = responseBean.getError();
                            if (errorBean != null) {
                                mHomeKitFailedTitle = errorBean.getMessage();
                                if (errorBean.getCode() == 10006) {
                                    Intent intent = new Intent();
                                    intent.putExtra(IntentConstant.HOMEKIT_CODE_ERROR, mHomeKitFailedTitle);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    addFail = true;
                                    isHomeKitFailed = true;
                                    switchConnectStatus(-1);
                                    cancelTimer();
                                }
                            }
                        }
                    }
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
                if (TextUtils.isEmpty(mDeviceBean.getSa_id())) {  // 如果非SA设备
                    Constant.mSendId=Constant.mSendId+1;
                    WSRequest<WSDeviceRequest> wsRequest = new WSRequest<>();
                    wsRequest.setId(Constant.mSendId);
                    wsRequest.setDomain(mDeviceBean.getPluginId());
                    wsRequest.setService(WSConstant.SERVICE_CONNECT);
                    WSDeviceRequest wsAddDeviceRequest = new WSDeviceRequest(mDeviceBean.getIid());
                    if (!TextUtils.isEmpty(homekitCode)) { // 如果是homekit设备，需要传pin码
                        WSAuthParamsBean wsAuthParamsBean = new WSAuthParamsBean(homekitCode);
                        wsAddDeviceRequest.setAuth_params(wsAuthParamsBean);
                    }
                    wsRequest.setData(wsAddDeviceRequest);
                    requestHashMap.put(String.valueOf(Constant.mSendId), wsRequest);
                    String deviceJson = GsonConverter.getGson().toJson(wsRequest);
                    LogUtil.e("添加设备发送的数据：" + deviceJson);
                    WSocketManager.getInstance().sendMessage(deviceJson);
                } else {  // SA设备通过接口添加
                    mPresenter.addDevice(mDeviceBean);
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
                if (progress < 95) {
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
        if (UserUtils.isLogin()) { // 登录要做家庭迁移
            HttpConfig.addHeader(cloudToken);
            CurrentHome.setSa_user_token(cloudToken);
            mPresenter.getAreaMoveUrl(String.valueOf(CurrentHome.getId()));
        } else {
            updateHc(false);
        }
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
        //添加成功SA
        if (mDeviceBean.getType() != null && mDeviceBean.getType().equalsIgnoreCase(Constant.DeviceType.TYPE_SA)) {
            if (!UserUtils.isLogin())
                switchConnectStatus(Constant.ConnectType.TYPE_SUCCESS);
            String token = data.getUser_info().getToken();
            if (homeCompanyBean == null)
                homeCompanyBean = new HomeCompanyBean();
            homeCompanyBean.setIs_bind_sa(true);
            if (!TextUtils.isEmpty(sa_lan_address)) {
                LogUtil.e("sa地址：" + sa_lan_address);
                homeCompanyBean.setSa_lan_address(sa_lan_address);
            }
            homeCompanyBean.setSa_user_token(token);
            if (UserUtils.isLogin()) {
                homeCompanyBean.setUser_id(CurrentHome.getUser_id());
            } else {
                homeCompanyBean.setUser_id(data.getUser_info().getUser_id());
            }

            IdBean idBean = data.getArea_info();
            if (idBean != null) {
                homeCompanyBean.setArea_id(idBean.getId());
            }
            if (wifiInfo != null) {
                homeCompanyBean.setSs_id(wifiInfo.getSSID());
                homeCompanyBean.setBSSID(wifiInfo.getBSSID());
            }
            homeCompanyBean.setSa_id(mDeviceBean.getSa_id());
            HttpConfig.addHeader(token);
            if (!UserUtils.isLogin()) { // 登录要做家庭迁移
                progress = 100;
                cancelTimer();
            }
            updateHc(true);

//            AllRequestUtil.createHomeBindSC(homeCompanyBean, null);
//            AllRequestUtil.bindCloudWithoutCreateHome(homeCompanyBean, null, sa_lan_address);

        } else {//添加插座、开关
            switchConnectStatus(Constant.ConnectType.TYPE_SUCCESS);
            progress = 100;
            cancelTimer();
            String pluginId = mDeviceBean.getPluginId();
            bundle = new Bundle();
            bundle.putInt(IntentConstant.ID, data.getDevice_id());
            bundle.putString(IntentConstant.NAME, mDeviceBean.getName());
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
     * 获取家庭迁移地址成功
     *
     * @param areaMoveUrlBean
     */
    @Override
    public void getAreaMoveUrlSuccess(AreaMoveUrlBean areaMoveUrlBean) {
        HttpConfig.addHeader(homeCompanyBean.getSa_user_token());
        if (areaMoveUrlBean != null) {
            AreaMoveRequest areaMoveRequest = new AreaMoveRequest(areaMoveUrlBean.getUrl(), areaMoveUrlBean.getBackup_file(), areaMoveUrlBean.getSum());
            String body = areaMoveRequest.toString();
            mPresenter.areaMove(body, sa_lan_address);
        } else {
            AreaMoveFail(true);
        }
    }

    /**
     * 获取家庭迁移地址失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getAreaMoveUrlFail(int errorCode, String msg) {
        AreaMoveFail(true);
    }

    /**
     * 迁移云端家庭到本地成功
     */
    @Override
    public void areaMoveSuccess() {
        progress = 100;
        cancelTimer();
        homeCompanyBean.setArea_id(homeCompanyBean.getId());
        homeCompanyBean.setSa_user_token(cloudToken);
        HttpConfig.addHeader(cloudToken);
        switchConnectStatus(Constant.ConnectType.TYPE_SUCCESS);
        updateHc(false);
    }

    /**
     * 迁移云端家庭到本地失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void areaMoveFail(int errorCode, String msg) {
        AreaMoveFail(false);
    }

    /**
     * 设置是否允许找回凭证成功
     */
    @Override
    public void onCertificateSuccess() {

    }

    /**
     * 设置是否允许找回凭证失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void onCertificateFail(int errorCode, String msg) {

    }

    /**
     * 迁移失败
     *
     * @param isUrlFail
     */
    private void AreaMoveFail(boolean isUrlFail) {
        tvStatus.setTextColor(UiUtil.getColor(R.color.color_FE0000));
        tvStatus.setText(isUrlFail ? UiUtil.getString(R.string.home_area_move_address_fail) : UiUtil.getString(R.string.home_area_move_fail));
        ivStatus.setVisibility(View.GONE);
        progress = 95;
        cancelTimer();
    }

    /**
     * 连接状态改变
     * d
     *
     * @param type -1失败 0连接中 1连接成功
     */
    private void switchConnectStatus(int type) {
        tvAddFailDesc.setVisibility(View.GONE);
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
            tvAddFailDesc.setVisibility(View.VISIBLE);

            String failTitle = UiUtil.getString(R.string.mine_home_add_device_connect_failed);
            String failContent = UiUtil.getString(R.string.home_add_device_no_homekit_failed);
            if (isHomeKitFailed) {
                failTitle = mHomeKitFailedTitle;
                failContent = UiUtil.getString(R.string.home_add_device_homekit_failed);
            }

            tvStatus.setText(failTitle);
            tvAddFailDesc.setText(failContent);
        }
        isHomeKitFailed = false;
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
                        delRoom();
                        HttpUrlConfig.baseSAUrl = homeCompanyBean.getSa_lan_address();
                        HttpUrlConfig.apiSAUrl = HttpUrlConfig.baseSAUrl + HttpUrlConfig.API;
                        showCertificateDialog();
                    }
                });
            }
        });
    }

    /**
     * 显示是否允许找回凭证
     */
    private void showCertificateDialog() {
        FindCertificateDialog dialog = FindCertificateDialog.newInstance();
        dialog.setDialogListener((isAllow) -> {
            FindCertificateBean.FindCertificateItemBean bean =
                    new FindCertificateBean.FindCertificateItemBean();
            bean.setUser_credential_found(isAllow);
            mPresenter.putFindCertificate(bean);
            switchToActivity(MainActivity.class);
            EventBus.getDefault().post(new HomeEvent(false, UserUtils.isLogin(), homeCompanyBean));
            finish();
        });
        dialog.show(DeviceConnectActivity.this);
    }

    /**
     * 删除房间
     */
    private void delRoom() {
        UiUtil.starThread(() -> {
            dbManager.removeLocationByHId(homeId, -1);
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
                SynPost.AreaBean areaBean = new SynPost.AreaBean(areaName);
                if (Constant.CurrentHome.getArea_type() == 2) {
                    areaBean.setDepartments(list);
                } else {
                    areaBean.setLocations(list);
                }
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