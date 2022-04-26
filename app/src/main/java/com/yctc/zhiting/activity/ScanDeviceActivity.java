package com.yctc.zhiting.activity;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.yctc.zhiting.entity.home.DeviceTypeListBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AddDeviceContract;
import com.yctc.zhiting.activity.presenter.AddDevicePresenter;
import com.yctc.zhiting.adapter.AddDeviceAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.entity.home.DeviceBean;
import com.yctc.zhiting.entity.home.FindDeviceBean;
import com.yctc.zhiting.entity.home.ScanResultBean;
import com.yctc.zhiting.entity.mine.CheckBindSaBean;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.widget.RadarScanView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * 扫描设备
 */
public class ScanDeviceActivity extends MVPBaseActivity<AddDeviceContract.View, AddDevicePresenter> implements AddDeviceContract.View {

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
    @BindView(R.id.viewLine)
    View viewLine;
    @BindView(R.id.rvDevice)
    RecyclerView rvDevice;

    private boolean again;
    private AddDeviceAdapter addDeviceAdapter;
    private List<DeviceBean> mScanLists = new ArrayList<>();
    private CountDownTimer mCountDownTimer;

    private long homeId;
    private long mFindDeviceId;//发现设备id
    private IWebSocketListener mIWebSocketListener;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_device;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.mine_home_add_device));
        initRv();
        initRadarScanView();
        initCountDownTimer();
        if (Constant.CurrentHome != null && Constant.CurrentHome.isIs_bind_sa()) {//绑定有SA
            initWebSocket();
        } else {
            mPresenter.checkBindSa();
        }
    }

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
                    tvStatus.setText(UiUtil.getString(R.string.mine_home_not_find_device));
                    ivReport.setVisibility(View.VISIBLE);
                    radarScanView.stop();
                    tvAgain.setVisibility(View.VISIBLE);
                    rvDevice.setVisibility(View.GONE);
                    viewLine.setVisibility(View.GONE);
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
                        viewLine.setVisibility(View.VISIBLE);
                        rvDevice.setVisibility(View.VISIBLE);
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
     * 开始发现设备
     */
    private void startScanDevice() {
        tvStatus.setText(UiUtil.getString(R.string.mine_home_scanning));
        FindDeviceBean findDeviceBean = new FindDeviceBean();
        findDeviceBean.setDomain("yeelight");
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
                setDeviceRecyclerViewVisible(again);
            }

            @Override
            public void onStop() {
            }
        });
    }

    /**
     * 设备列表可见
     *
     * @param again
     */
    private void setDeviceRecyclerViewVisible(boolean again) {
        if (again) {
            viewLine.setVisibility(View.VISIBLE);
            rvDevice.setVisibility(View.VISIBLE);
        }
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
    private void initRv() {
        addDeviceAdapter = new AddDeviceAdapter(mScanLists);
        rvDevice.setLayoutManager(new LinearLayoutManager(this));
        rvDevice.setAdapter(addDeviceAdapter);
        addDeviceAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            DeviceBean bean = mScanLists.get(position);
            if (bean.getType()!=null && bean.getType().equalsIgnoreCase(Constant.DeviceType.TYPE_SA)) {
                if (bean.isBind()) {//已经被绑定过，扫码加入家庭(SA)
                    //switchToActivity(ScanActivity.class);
                    switchToActivity(CaptureNewActivity.class);
                } else {//添加SA
                    bean.setArea_type(Constant.CurrentHome.getArea_type());
                    addDevice(bean);
                }
            } else {//添加设备(灯和开关)
                addDevice(bean);
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
        switchToActivity(DeviceConnectActivity.class, bundle);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (radarScanView != null)
            radarScanView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        radarScanView.stop();
        if (mCountDownTimer != null) {
            mCountDownTimer.onFinish();
            mCountDownTimer.cancel();
        }
        LogUtil.e("mCountDownTimer=onPause=");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        radarScanView.stop();
        WSocketManager.getInstance().removeWebSocketListener(mIWebSocketListener);
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

    @Override
    public void getDeviceTypeSuccess(DeviceTypeListBean deviceTypeListBean) {

    }

    @Override
    public void getDeviceTypeFail(int errorCode, String msg) {

    }

    @Override
    public void getPluginDetailSuccess(PluginDetailBean pluginDetailBean) {

    }

    @Override
    public void getPluginDetailFail(int errorCode, String msg) {

    }

    @Override
    public void getDeviceFirstTypeSuccess(DeviceTypeListBean deviceTypeListBean) {

    }

    @Override
    public void getDeviceFirstTypFail(int errorCode, String msg) {

    }

    @Override
    public void getDeviceSecondTypeSuccess(DeviceTypeListBean deviceTypeListBean) {

    }

    @Override
    public void getDeviceSecondTypeFail(int errorCode, String msg) {

    }

    private void initDefaultSA(boolean isBind) {
        String json1 = "{\"address\":\"http://192.168.0.188:8088\",\"identity\":\"0x00000000157b4d9c\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"WeiJie'SA\",\"plugin_id\":\"light_001\",\"sw_version\":\"17\",\"type\":\"sa\"}";
        String json2 = "{\"address\":\"http://192.168.0.112:8088\",\"identity\":\"0x00000000157b4d9d\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"MaJian'SA\",\"plugin_id\":\"light_002\",\"sw_version\":\"18\",\"type\":\"sa\"}";
        String json3 = "{\"address\":\"http://192.168.0.165:8089\",\"identity\":\"0x00000000157b4d9e\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"LiHong'SA\",\"plugin_id\":\"light_003\",\"sw_version\":\"19\",\"type\":\"sa\"}";
        String json4 = "{\"address\":\"http://192.168.22.123:9020\",\"identity\":\"0x00000000157b4d9f\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"CeShi'SA\",\"plugin_id\":\"light_004\",\"sw_version\":\"120\",\"type\":\"sa\"}";
        String json5 = "{\"address\":\"http://192.168.0.84:8088\",\"identity\":\"0x00000000157b4d9f\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"CeShi1'SA\",\"plugin_id\":\"light_004\",\"sw_version\":\"120\",\"type\":\"sa\"}";
        String json6 = "{\"address\":\"http://192.168.0.82:8088\",\"identity\":\"0x00000000157b4d9f\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"CeShi2'SA\",\"plugin_id\":\"light_004\",\"sw_version\":\"120\",\"type\":\"sa\"}";
        String json7 = "{\"address\":\"http://192.168.0.182:8088\",\"identity\":\"0x00000000157b4d9e\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"LiHong'SA\",\"plugin_id\":\"light_003\",\"sw_version\":\"19\",\"type\":\"sa\"}";
        String json8 = "{\"address\":\"http://192.168.22.76:8088\",\"identity\":\"0x00000000157b4d9e\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"LiHong'SA\",\"plugin_id\":\"light_003\",\"sw_version\":\"19\",\"type\":\"sa\"}";
        String json9 = "{\"address\":\"http://192.168.22.106:37965\",\"identity\":\"0x00000000157b4d9e\",\"manufacturer\":\"yeelight\",\"model\":\"smart_assistant\",\"name\":\"LiHong'SA\",\"plugin_id\":\"light_003\",\"sw_version\":\"19\",\"type\":\"sa\"}";
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
        }else if (HttpUrlConfig.apiSAUrl.contains(bean8.getAddress())) {
            mScanLists.add(bean8);
            bean8.setBind(isBind);
        }else if (HttpUrlConfig.apiSAUrl.contains(bean9.getAddress())) {
            mScanLists.add(bean9);
            bean9.setBind(isBind);
        }
        addDeviceAdapter.notifyDataSetChanged();
        setDeviceRecyclerViewVisible(mScanLists.size() > 0);
    }
}