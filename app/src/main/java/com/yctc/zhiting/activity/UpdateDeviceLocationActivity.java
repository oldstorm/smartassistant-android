package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SetDevicePositionContract;
import com.yctc.zhiting.activity.contract.UpdateDeviceLocationContract;
import com.yctc.zhiting.activity.presenter.SetDevicePositionPresenter;
import com.yctc.zhiting.activity.presenter.UpdateDeviceLocationPresenter;
import com.yctc.zhiting.adapter.PositionAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.entity.mine.AreasBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.event.DeviceRefreshEvent;
import com.yctc.zhiting.event.LocationEvent;
import com.yctc.zhiting.request.AddRoomRequest;
import com.yctc.zhiting.request.ModifyDeviceRequest;
import com.yctc.zhiting.utils.IntentConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 修改设备位置
 */
public class UpdateDeviceLocationActivity extends MVPBaseActivity<UpdateDeviceLocationContract.View, UpdateDeviceLocationPresenter> implements UpdateDeviceLocationContract.View {

    @BindView(R.id.tvAdd)
    TextView tvAdd;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private PositionAdapter mPositionAdapter;
    private List<LocationBean> mLocationList = new ArrayList<>();
    private int mDeviceId;//设备id
    private int mLocationId;//房间id
    private String mDeviceName;//设备名称

    private int lId;
    private String lName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_device_location;
    }

    @Override
    protected void initData() {
        super.initData();
        initRecyclerView();
        mPresenter.getPermissions(Constant.CurrentHome.getUser_id());
        mPresenter.getAreaList();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mDeviceId = intent.getIntExtra(IntentConstant.ID, 0);
        mLocationId = intent.getIntExtra(IntentConstant.RA_ID, 0);
        mDeviceName = intent.getStringExtra(IntentConstant.NAME);
    }

    private void initRecyclerView() {
        mPositionAdapter = new PositionAdapter(mLocationList);
        recyclerView.setAdapter(mPositionAdapter);

        mPositionAdapter.setOnItemClickListener((adapter, view, position) -> {
            for (int i = 0; i < mLocationList.size(); i++)
                if (i != position) mLocationList.get(i).setCheck(false);
            LocationBean bean = mPositionAdapter.getItem(position);
            lId = bean.getId();
            lName = bean.getName();
            bean.setCheck(!bean.isCheck());
            adapter.notifyDataSetChanged();
            mLocationId = bean.isCheck() ? bean.getId() : 0;
        });
    }


    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    /**
     * 返回
     */
    @OnClick(R.id.ivBack)
    void onClickBack(){
        onBackPressed();
    }

    /**
     * 添加房间
     */
    @OnClick(R.id.tvAdd)
    void onClickAdd(){
        showAddAreaDialog();
    }

    /**
     * 完成
     */
    @OnClick(R.id.tvComplete)
    void onClickComplete(){
        complete();
    }

    /**
     * 添加房间、区域对话框
     */
    private void showAddAreaDialog() {
        String title = UiUtil.getString(R.string.mine_room_name);
        String tip = UiUtil.getString(R.string.mine_input_room_name);

        EditBottomDialog editBottomDialog = EditBottomDialog.newInstance(title, tip, null, 1);
        editBottomDialog.setClickSaveListener(content -> {
            AndroidUtil.hideSoftInput(LibLoader.getCurrentActivity());
            mPresenter.addAreaRoom(new AddRoomRequest(content));
        }).show(this);
    }

    /**
     * 完成
     */
    private void complete() {
        Request request = new ModifyDeviceRequest(mDeviceName, mLocationId);
        mPresenter.updateDeviceName(String.valueOf(mDeviceId), request);
    }

    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean!=null){
            tvAdd.setVisibility(permissionBean.getPermissions().isAdd_location() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void getFail(int errorCode, String msg) {

    }

    @Override
    public void onAreasSuccess(AreasBean data) {
        if (data != null && data.getLocations() != null && data.getLocations().size() > 0) {
            mLocationList = data.getLocations();
            if (mLocationId > 0) {
                for (int i = 0; i < mLocationList.size(); i++) {
                    if (mLocationList.get(i).getId() == mLocationId) {
                        mLocationList.get(i).setCheck(true);
                        break;
                    }
                }
            }
            mPositionAdapter.setNewData(mLocationList);
        }
    }

    @Override
    public void onAreasFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void onAddAreaRoomSuccess(AreasBean data) {
        mPresenter.getAreaList();
    }

    @Override
    public void onAddAreaRoomFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public void onUpdateDeviceNameSuccess() {
        EventBus.getDefault().post(new LocationEvent(lId, lName));
        onBackPressed();
    }

    @Override
    public void onUpdateDeviceNameFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}