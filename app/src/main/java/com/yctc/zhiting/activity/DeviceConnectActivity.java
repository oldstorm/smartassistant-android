package com.yctc.zhiting.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
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
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DeviceConnectContract;
import com.yctc.zhiting.activity.presenter.DeviceConnectPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.db.DBThread;
import com.yctc.zhiting.entity.home.AddDeviceResponseBean;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.UserInfoBean;
import com.yctc.zhiting.event.HomeEvent;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;
import com.yctc.zhiting.widget.ConnectView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    private int progress = 0;
    private String nickname;
    private String areaName;
    private DBManager dbManager;
    private Handler mainThreadHandler;
    private DeviceBean mDeviceBean;//设备对象
    private HomeCompanyBean homeCompanyBean = new HomeCompanyBean();
    private WeakReference<Context> mContext;
    private CountDownTimer mCountDownTimer;//进度计时

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_connect;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_home_device_connect));
    }

    @Override
    protected void initData() {
        super.initData();
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());
        mainThreadHandler = new Handler(Looper.getMainLooper());
        getUserInfo();
        startAddDevice();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        homeId = intent.getLongExtra(IntentConstant.ID, -1);
        mDeviceBean = (DeviceBean) intent.getSerializableExtra(IntentConstant.BEAN);
    }

    /**
     * 添加设备
     */
    private void startAddDevice() {
        initCountDownTimer();
        switchConnectStatus(Constant.ConnectType.TYPE_CONNECTING);
        if (mDeviceBean != null) {
            UiUtil.postDelayed(() -> {
                mPresenter.addDevice(mDeviceBean);
            }, 2000);
        }
    }

    private void initCountDownTimer() {
        progress = 0;
        int maxSecond = (int) (Math.random() * 19_000 + 80_000);
        mCountDownTimer = new CountDownTimer(maxSecond, 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (progress > 95) {
                    onFinish();
                    cancel();
                }
                progress++;
                connectView.setProgress(progress);
            }

            @Override
            public void onFinish() {
                progress = 100;
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
        ToastUtil.show("成功");
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
        cancelTimer();
        switchConnectStatus(Constant.ConnectType.TYPE_SUCCESS);
        //添加成功SA
        if (mDeviceBean.getType()!=null && mDeviceBean.getType().equalsIgnoreCase(Constant.DeviceType.TYPE_SA)) {
            LogUtil.e("DeviceConnectActivity2==" + GsonConverter.getGson().toJson(homeCompanyBean));
            String token = data.getUser_info().getToken();
            if(homeCompanyBean==null)
                homeCompanyBean = new HomeCompanyBean();
            homeCompanyBean.setIs_bind_sa(true);
            homeCompanyBean.setSa_user_token(token);
            homeCompanyBean.setSa_lan_address(mDeviceBean.getAddress());
            homeCompanyBean.setUser_id(data.getUser_info().getUser_id());
            if (wifiInfo != null) {
                homeCompanyBean.setSs_id(wifiInfo.getSSID());
                homeCompanyBean.setMac_address(wifiInfo.getBSSID());
            }
            LogUtil.e("updateHc==" + GsonConverter.getGson().toJson(homeCompanyBean));
            LogUtil.e("wifiInfo==" + GsonConverter.getGson().toJson(wifiInfo));

            HttpConfig.addHeader(token);
            updateHc(true);

            AllRequestUtil.createHomeBindSC(homeCompanyBean);
        } else {//添加插座、开关
            Bundle bundle = new Bundle();
            bundle.putInt(IntentConstant.ID, data.getDevice_id());
            bundle.putString(IntentConstant.NAME, mDeviceBean.getName());
            switchToActivity(SetDevicePositionActivity.class, bundle);
        }
    }

    /**
     * 添加设备失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void addDeviceFail(int errorCode, String msg) {
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
            tvAgain.setVisibility(View.VISIBLE);
            tvStatus.setTextColor(UiUtil.getColor(R.color.color_FE0000));
            tvStatus.setText(UiUtil.getString(R.string.mine_home_device_connect_failed));
        }
    }

    /**
     * 加载家庭信息
     */
    private void loadHC() {
        new DBThread(() -> {
            homeCompanyBean = dbManager.queryHomeCompanyById(homeId);
            mainThreadHandler.post(() -> {
                if (homeCompanyBean != null) {
                    areaName = homeCompanyBean.getName();
                }
            });
        }).start();
    }

    /**
     * 修改家庭
     */
    private void updateHc(boolean loadRoom) {
        new DBThread(() -> {
            int count = dbManager.updateHomeCompany(homeCompanyBean);
            if (count > 0) {
                mainThreadHandler.post(() -> {
                    if (loadRoom) {
                        loadRoomData();
                    } else {
                        EventBus.getDefault().post(new HomeEvent(true,homeCompanyBean));
                        switchToActivity(MainActivity.class);
                    }
                });
            }
        }).start();
    }

    /**
     * 删除房间
     */
    private void delRoom() {
        new DBThread(() -> {
            dbManager.removeLocationByHId(homeId, -1);
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    DeviceConnectActivity.this.finish();
                }
            });
        });
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        new DBThread(() -> {
            UserInfoBean userInfoBean = dbManager.getUser();
            if (userInfoBean != null) {
                mainThreadHandler.post(() -> {
                    nickname = userInfoBean.getNickname();
                    loadHC();
                });
            }
        }).start();
    }

    /**
     * 从数据库查询数据
     */
    private void loadRoomData() {
        new DBThread(() -> {
            List<LocationBean> list = dbManager.queryLocationList(homeId);
            mainThreadHandler.post(() -> {
                SynPost.AreaBean areaBean = new SynPost.AreaBean(areaName, list);
                SynPost synPost = new SynPost(nickname, areaBean);
                String body = new Gson().toJson(synPost);
                mPresenter.sync(body);
            });
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}