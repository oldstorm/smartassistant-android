package com.yctc.zhiting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.imageutil.GlideUtil;
import com.google.gson.reflect.TypeToken;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DeviceSettingContract;
import com.yctc.zhiting.activity.presenter.DeviceSettingPresenter;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.home.DeviceLogoBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.ws_request.WSDeviceRequest;
import com.yctc.zhiting.entity.ws_request.WSRequest;
import com.yctc.zhiting.entity.ws_request.WSConstant;
import com.yctc.zhiting.entity.ws_response.WSBaseResponseBean;
import com.yctc.zhiting.event.DeviceRefreshEvent;
import com.yctc.zhiting.event.DeviceNameEvent;
import com.yctc.zhiting.event.FinishDeviceDetailEvent;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.websocket.IWebSocketListener;
import com.yctc.zhiting.websocket.WSocketManager;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.WebSocket;


/**
 * 设备设置
 */
public class DeviceSettingActivity extends MVPBaseActivity<DeviceSettingContract.View, DeviceSettingPresenter> implements DeviceSettingContract.View {

    private int mDeviceId;//设备id
    private int mLocationId;//房间id
    private String mDeviceName;//设备名称
    private String raName; // 位置
    private boolean updateDeviceP; // 修改设备权限
    private String logoUrl;

    @BindView(R.id.llTop)
    LinearLayout llTop;
    @BindView(R.id.ivCover)
    ImageView ivCover;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvDeviceName)
    TextView tvDeviceName;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvDel)
    TextView tvDel;
    @BindView(R.id.rlName)
    RelativeLayout rlName;
    @BindView(R.id.rlLocation)
    RelativeLayout rlLocation;
    @BindView(R.id.tvIcon)
    TextView tvIcon;
    @BindView(R.id.rlIcon)
    RelativeLayout rlIcon;

    private String updateName;

    private boolean is_sa;

    private CenterAlertDialog centerAlertDialog;

    private IWebSocketListener mIWebSocketListener;
    private String iid; // 删除设备要用
    private String pluginId; // 插件id
    private ConcurrentHashMap<String, WSRequest> requestMap = new ConcurrentHashMap<>();
    private int logoType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter("");
//        mPresenter.getPermissions(Constant.CurrentHome.getUser_id());
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        initWebSocket();
        mDeviceId = intent.getIntExtra(IntentConstant.ID, 0);
        mLocationId = intent.getIntExtra(IntentConstant.RA_ID, 0);
        mDeviceName = intent.getStringExtra(IntentConstant.NAME);
        iid = intent.getStringExtra(IntentConstant.IID);
        pluginId = intent.getStringExtra(IntentConstant.PLUGIN_ID);
        raName = intent.getStringExtra(IntentConstant.RA_NAME);
        logoUrl = getIntent().getStringExtra(IntentConstant.LOGO_URL);
        is_sa = getIntent().getBooleanExtra(IntentConstant.IS_SA, false);
        if (mDeviceName != null) {
            tvName.setText(mDeviceName);
            tvDeviceName.setText(mDeviceName);
        }
        if (raName != null)
            tvLocation.setText(raName);
        GlideUtil.load(logoUrl).into(ivCover);
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        mPresenter.getDeviceDetailRestructure(mDeviceId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                getData();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestMap.clear();
        WSocketManager.getInstance().removeWebSocketListener(mIWebSocketListener);
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @OnClick({R.id.rlName, R.id.rlLocation, R.id.tvDel, R.id.rlIcon})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlName:  // 设备名称
                showAUpdateNameDialog();
                break;

            case R.id.rlLocation:  // 设备位置
                Bundle bundle = new Bundle();
                bundle.putInt(IntentConstant.ID, mDeviceId);
                bundle.putInt(IntentConstant.RA_ID, mLocationId);
                bundle.putString(IntentConstant.NAME, mDeviceName);
                switchToActivityForResult(UpdateDeviceLocationActivity.class, bundle, 100);
                break;

            case R.id.tvDel:  // 删除设备
                centerAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.home_device_del_title), null, true);
                centerAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm(boolean del) {
                        delDevice();
//                        mPresenter.delDevice(mDeviceId);

                    }
                });
                centerAlertDialog.show(this);

                break;

            case R.id.rlIcon:
                Bundle iconBundle = new Bundle();
                iconBundle.putInt(IntentConstant.ID, mDeviceId);
                iconBundle.putInt(IntentConstant.TYPE, logoType);
                switchToActivityForResult(ChangeIconActivity.class, iconBundle, 100);
                break;
        }
    }

    /**
     * 删除设备
     */
    private void delDevice() {
        Constant.mSendId=Constant.mSendId+1;
        WSRequest<WSDeviceRequest> wsRequest = new WSRequest<>();
        wsRequest.setId(Constant.mSendId);
        wsRequest.setDomain(pluginId);
        wsRequest.setService(WSConstant.SERVICE_DISCONNECT);
        WSDeviceRequest wsAddDeviceRequest = new WSDeviceRequest(iid);
        wsRequest.setData(wsAddDeviceRequest);
        String deviceJson = GsonConverter.getGson().toJson(wsRequest);
        LogUtil.e("删除设备发送的数据：" + deviceJson);
        requestMap.put(String.valueOf(Constant.mSendId), wsRequest);
        WSocketManager.getInstance().sendMessage(deviceJson);
    }

    /**
     * WebSocket初始化、添加监听
     */
    private void initWebSocket() {
        mIWebSocketListener = new IWebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {

                WSBaseResponseBean responseBean = GsonConverter.getGson().fromJson(text, new TypeToken<WSBaseResponseBean>() {
                }.getType());
                if (responseBean != null) {
                    long respId = responseBean.getId();
                    LogUtil.e("删除设备：" + text);
                    LogUtil.e("删除消息id：" + respId);
                    if (requestMap.containsKey(String.valueOf(respId))) {
                        if (responseBean.isSuccess()) {
                            delDeviceSuccess();
                        } else {
                            getFail(-1, UiUtil.getString(R.string.mine_remove_fail));
                        }
                    }
                }
            }
        };
        WSocketManager.getInstance().addWebSocketListener(mIWebSocketListener);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(LocationEvent event) {
//        mLocationId = event.getId();
//        raName = event.getName();
//        tvLocation.setText(event.getName());
//    }

    /**
     * 修改设备名称
     */
    private void showAUpdateNameDialog() {
        String title = UiUtil.getString(R.string.home_device_update_name);
        String tip = UiUtil.getString(R.string.home_device_update_name_input);

        EditBottomDialog editBottomDialog = EditBottomDialog.newInstance(title, tip, mDeviceName, 1);
        editBottomDialog.setClickSaveListener(content -> {
            updateName = content;
            AndroidUtil.hideSoftInput(LibLoader.getCurrentActivity());
            mPresenter.updateName(mDeviceId, content, mLocationId);
        }).show(this);
    }

    /**
     * 获取权限成功
     *
     * @param permissionBean
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean != null) {
            updateDeviceP = permissionBean.getPermissions().isUpdate_device();
            if (!is_sa) {
                tvDel.setVisibility(permissionBean.getPermissions().isDelete_device() ? View.VISIBLE : View.GONE);
            }
            rlName.setVisibility(updateDeviceP ? View.VISIBLE : View.GONE);
            rlLocation.setVisibility(updateDeviceP ? View.VISIBLE : View.GONE);
            rlIcon.setVisibility(updateDeviceP ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设备详情
     *
     * @param deviceDetailBean
     */
    @Override
    public void getDeviceDetailSuccess(DeviceDetailBean deviceDetailBean) {
        if (deviceDetailBean != null) {
            DeviceDetailBean.DeviceInfoBean deviceInfoBean = deviceDetailBean.getDevice_info();
            if (deviceDetailBean != null) {
                DeviceDetailBean.DeviceInfoBean.LocationBean locationBean = Constant.AREA_TYPE == Constant.COMPANY_MODE ? deviceInfoBean.getDepartment() : deviceInfoBean.getLocation();
                tvName.setText(deviceInfoBean.getName());
                tvDeviceName.setText(deviceInfoBean.getName());
                GlideUtil.load(deviceInfoBean.getLogo_url()).into(ivCover);
                if (locationBean != null) {
                    mLocationId = locationBean.getId();
                    raName = locationBean.getName();
                    tvLocation.setText(raName);
                }
                DeviceDetailBean.DeviceInfoBean.PermissionsBean permissionsBean = deviceInfoBean.getPermissions();
                if (permissionsBean != null) {
                    updateDeviceP = permissionsBean.isUpdate_device();
                    rlName.setVisibility(permissionsBean.isUpdate_device() ? View.VISIBLE : View.GONE);
                    rlLocation.setVisibility(permissionsBean.isUpdate_device() ? View.VISIBLE : View.GONE);
                    rlIcon.setVisibility(permissionsBean.isUpdate_device() ? View.VISIBLE : View.GONE);
                    if (!is_sa) {
                        tvDel.setVisibility(permissionsBean.isDelete_device() ? View.VISIBLE : View.GONE);
                    }
                }
            }
        }
    }

    /**
     * 修改设备名称
     */
    @Override
    public void updateNameSuccess() {
        tvName.setText(updateName);
        tvDeviceName.setText(updateName);
        mDeviceName = updateName;
        EventBus.getDefault().post(new DeviceNameEvent(updateName));
        ToastUtil.show(getResources().getString(R.string.mine_save_success));
    }

    /**
     * 删除设备
     */
    @Override
    public void delDeviceSuccess() {
        EventBus.getDefault().post(new FinishDeviceDetailEvent());
        EventBus.getDefault().post(new DeviceRefreshEvent());
        ToastUtil.show(getResources().getString(R.string.mine_remove_success));
        if (centerAlertDialog != null) {
            centerAlertDialog.dismiss();
        }
        finish();
    }

    @Override
    public void getFail(int errorCode, String msg) {
        if (centerAlertDialog != null) {
            centerAlertDialog.dismiss();
        }
        ToastUtil.show(msg);
    }

    /**
     * 设备详情成功 重构
     *
     * @param deviceDetailResponseEntity
     */
    @Override
    public void getDeviceDetailRestructureSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity) {
        if (deviceDetailResponseEntity != null) {
            DeviceDetailEntity deviceDetailEntity = deviceDetailResponseEntity.getDevice_info();
            if (deviceDetailEntity != null) {
                tvName.setText(deviceDetailEntity.getName());
                tvDeviceName.setText(deviceDetailEntity.getName());
                GlideUtil.load(deviceDetailEntity.getLogo_url()).into(ivCover);
                llTop.setVisibility(View.VISIBLE);
                iid = deviceDetailEntity.getIid();
                DeviceDetailEntity.AreaAndLocationBean locationBean = Constant.AREA_TYPE == Constant.COMPANY_MODE ? deviceDetailEntity.getDepartment() : deviceDetailEntity.getLocation();

                if (locationBean != null) {
                    mLocationId = locationBean.getId();
                    raName = locationBean.getName();
                    tvLocation.setText(raName);
                }

                DeviceLogoBean deviceLogoBean = deviceDetailEntity.getLogo();
                if (deviceLogoBean != null) {
                    tvIcon.setText(deviceLogoBean.getName());
                    logoType = deviceLogoBean.getType();
                }

                DeviceDetailEntity.PermissionsBean permissionsBean = deviceDetailEntity.getPermissions();
                if (permissionsBean != null) {
                    updateDeviceP = permissionsBean.isUpdate_device();
                    rlName.setVisibility(permissionsBean.isUpdate_device() ? View.VISIBLE : View.GONE);
                    rlLocation.setVisibility(permissionsBean.isUpdate_device() ? View.VISIBLE : View.GONE);
                    rlIcon.setVisibility(permissionsBean.isUpdate_device() ? View.VISIBLE : View.GONE);
                    if (!is_sa) {
                        tvDel.setVisibility(permissionsBean.isDelete_device() ? View.VISIBLE : View.GONE);
                    }
                }
            }
        }
    }
}