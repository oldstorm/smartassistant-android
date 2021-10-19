package com.yctc.zhiting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.imageutil.GlideUtil;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DeviceSettingContract;
import com.yctc.zhiting.activity.presenter.DeviceSettingPresenter;
import com.yctc.zhiting.bean.DeviceDetailBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.entity.DeviceDetailEntity;
import com.yctc.zhiting.entity.DeviceDetailResponseEntity;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.event.DeviceRefreshEvent;
import com.yctc.zhiting.event.DeviceNameEvent;
import com.yctc.zhiting.event.FinishDeviceDetailEvent;
import com.yctc.zhiting.event.LocationEvent;
import com.yctc.zhiting.utils.IntentConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 设备设置
 */
public class DeviceSettingActivity extends MVPBaseActivity<DeviceSettingContract.View, DeviceSettingPresenter> implements  DeviceSettingContract.View {

    private int mDeviceId;//设备id
    private int mLocationId;//房间id
    private String mDeviceName;//设备名称
    private String raName; // 位置
    private boolean updateDeviceP; // 修改设备权限
    private String logoUrl;

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

    private String updateName;

    private boolean is_sa;

    private CenterAlertDialog centerAlertDialog;



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
        mDeviceId = intent.getIntExtra(IntentConstant.ID, 0);
        mLocationId = intent.getIntExtra(IntentConstant.RA_ID, 0);
        mDeviceName = intent.getStringExtra(IntentConstant.NAME);
        raName = intent.getStringExtra(IntentConstant.RA_NAME);
        logoUrl = getIntent().getStringExtra(IntentConstant.LOGO_URL);
        is_sa = getIntent().getBooleanExtra(IntentConstant.IS_SA, false);
        tvName.setText(mDeviceName);
        tvDeviceName.setText(mDeviceName);
        tvLocation.setText(raName);
        GlideUtil.load(logoUrl).into(ivCover);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPresenter.getDeviceDetail(mDeviceId);
        mPresenter.getDeviceDetailRestructure(mDeviceId);
    }

    @Override
    public boolean bindEventBus() {
        return true;
    }

    @OnClick({R.id.rlName, R.id.rlLocation, R.id.tvDel})
    void onClick(View view){
        switch (view.getId()) {
            case R.id.rlName:  // 设备名称
                showAUpdateNameDialog();
                break;

            case R.id.rlLocation:  // 设备位置
                Bundle bundle = new Bundle();
                bundle.putInt(IntentConstant.ID, mDeviceId);
                bundle.putInt(IntentConstant.RA_ID, mLocationId);
                bundle.putString(IntentConstant.NAME, mDeviceName);
                switchToActivity(UpdateDeviceLocationActivity.class, bundle);
                break;

            case R.id.tvDel:  // 删除设备
                 centerAlertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.home_device_del_title), null, true);
                centerAlertDialog.setConfirmListener(new CenterAlertDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        mPresenter.delDevice(mDeviceId);

                    }
                });
                centerAlertDialog.show(this);

                break;
        }
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
     * @param permissionBean
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean!=null){
            updateDeviceP = permissionBean.getPermissions().isUpdate_device();
            if (!is_sa) {
                tvDel.setVisibility(permissionBean.getPermissions().isDelete_device() ? View.VISIBLE : View.GONE);
            }
            rlName.setVisibility(updateDeviceP ? View.VISIBLE : View.GONE);
            rlLocation.setVisibility(updateDeviceP ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设备详情
     * @param deviceDetailBean
     */
    @Override
    public void getDeviceDetailSuccess(DeviceDetailBean deviceDetailBean) {
        if (deviceDetailBean!=null){
            DeviceDetailBean.DeviceInfoBean deviceInfoBean = deviceDetailBean.getDevice_info();
            if (deviceDetailBean!=null) {
                DeviceDetailBean.DeviceInfoBean.LocationBean locationBean = deviceInfoBean.getLocation();
                tvName.setText(deviceInfoBean.getName());
                tvDeviceName.setText(deviceInfoBean.getName());
                GlideUtil.load(deviceInfoBean.getLogo_url()).into(ivCover);
                if (locationBean!=null){
                    mLocationId = locationBean.getId();
                    raName = locationBean.getName();
                    tvLocation.setText(raName);
                }
                DeviceDetailBean.DeviceInfoBean.PermissionsBean permissionsBean = deviceInfoBean.getPermissions();
                if (permissionsBean!=null){
                    updateDeviceP = permissionsBean.isUpdate_device();
                    rlName.setVisibility(permissionsBean.isUpdate_device() ? View.VISIBLE : View.GONE);
                    rlLocation.setVisibility(permissionsBean.isUpdate_device() ? View.VISIBLE : View.GONE);
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
        if (centerAlertDialog!=null){
            centerAlertDialog.dismiss();
        }
        finish();
    }

    @Override
    public void getFail(int errorCode, String msg) {
        if (centerAlertDialog!=null){
            centerAlertDialog.dismiss();
        }
        ToastUtil.show(msg);
    }

    /**
     * 设备详情成功 重构
     * @param deviceDetailResponseEntity
     */
    @Override
    public void getDeviceDetailRestructureSuccess(DeviceDetailResponseEntity deviceDetailResponseEntity) {
        if (deviceDetailResponseEntity!=null){
            DeviceDetailEntity deviceDetailEntity = deviceDetailResponseEntity.getDevice_info();
            if (deviceDetailEntity!=null){
                tvName.setText(deviceDetailEntity.getName());
                tvDeviceName.setText(deviceDetailEntity.getName());
                GlideUtil.load(deviceDetailEntity.getLogo_url()).into(ivCover);
                DeviceDetailEntity.AreaAndLocationBean locationBean = deviceDetailEntity.getLocation();

                if (locationBean!=null){
                    mLocationId = locationBean.getId();
                    raName = locationBean.getName();
                    tvLocation.setText(raName);
                }

                DeviceDetailEntity.PermissionsBean permissionsBean = deviceDetailEntity.getPermissions();
                if (permissionsBean!=null){
                    updateDeviceP = permissionsBean.isUpdate_device();
                    rlName.setVisibility(permissionsBean.isUpdate_device() ? View.VISIBLE : View.GONE);
                    rlLocation.setVisibility(permissionsBean.isUpdate_device() ? View.VISIBLE : View.GONE);
                    if (!is_sa) {
                        tvDel.setVisibility(permissionsBean.isDelete_device() ? View.VISIBLE : View.GONE);
                    }
                }
            }
        }
    }
}