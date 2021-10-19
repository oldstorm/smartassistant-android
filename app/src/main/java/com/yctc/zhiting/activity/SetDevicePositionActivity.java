package com.yctc.zhiting.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.httputil.request.Request;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.SetDevicePositionContract;
import com.yctc.zhiting.activity.presenter.SetDevicePositionPresenter;
import com.yctc.zhiting.adapter.PositionAdapter;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.entity.mine.AreasBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.event.DeviceRefreshEvent;
import com.yctc.zhiting.event.HomeEvent;
import com.yctc.zhiting.request.AddRoomRequest;
import com.yctc.zhiting.request.ModifyDeviceRequest;
import com.yctc.zhiting.utils.IntentConstant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置设备位置
 */
public class SetDevicePositionActivity extends MVPBaseActivity<SetDevicePositionContract.View, SetDevicePositionPresenter> implements SetDevicePositionContract.View {

    @BindView(R.id.etDeviceName)
    EditText etDeviceName;
    @BindView(R.id.tvAddArea)
    TextView tvAddArea;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private PositionAdapter mPositionAdapter;
    private List<LocationBean> mLocationList = new ArrayList<>();
    private int mDeviceId;//设备id
    private int mLocationId;//房间id
    private String mDeviceName;//设备名称

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_device_posotion;
    }

    @Override
    protected void initUI() {
        super.initUI();
        //HttpConfig.addHeader("MTYyMjAxNjYzOHxOd3dBTkZSQ1ZFVktTMVJOVlV4VVNrRlJRVkV5UlU0MlYxYzFRVE5JV2twVlJVOUNTMVF5TjA5UE5EZERUVkJXTmpOR1EwUldRVUU9fNhz67_KPiTN6Ab74Yx-eP2L8Xhhz5TsE0cJkRySfD8N");
        setTitleCenter(UiUtil.getString(R.string.home_setting));
        initRecyclerView();
        mPresenter.getAreaList();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mDeviceId = intent.getIntExtra(IntentConstant.ID, 0);
        mLocationId = intent.getIntExtra(IntentConstant.RA_ID, 0);
        mDeviceName = intent.getStringExtra(IntentConstant.NAME);
        etDeviceName.setText(mDeviceName);
    }

    private void initRecyclerView() {
        mPositionAdapter = new PositionAdapter(mLocationList);
        recyclerView.setAdapter(mPositionAdapter);

        mPositionAdapter.setOnItemClickListener((adapter, view, position) -> {
            for (int i = 0; i < mLocationList.size(); i++)
                if (i != position) mLocationList.get(i).setCheck(false);
            LocationBean bean = mPositionAdapter.getItem(position);
            bean.setCheck(!bean.isCheck());
            adapter.notifyDataSetChanged();
            mLocationId = bean.isCheck() ? bean.getId() : 0;
        });
    }

    @OnClick({R.id.tvComplete, R.id.tvAddArea})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvComplete:
                complete();
                break;
            case R.id.tvAddArea:
                showAddAreaDialog();
                break;
        }
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
        mDeviceName = etDeviceName.getText().toString();
        if (TextUtils.isEmpty(mDeviceName)) {
            ToastUtil.show(UiUtil.getString(R.string.home_input_device_name_tip));
            return;
        }
        if (mDeviceName.length() > 20) {
            ToastUtil.show(UiUtil.getString(R.string.home_device_name_lenght_tip));
            return;
        }

        Request request = new ModifyDeviceRequest(mDeviceName, mLocationId);
        mPresenter.updateDeviceName(String.valueOf(mDeviceId), request);
    }

    @Override
    public void onAreasSuccess(AreasBean data) {
        if (data != null && data.getLocations() != null && data.getLocations().size() > 0) {
            mLocationList = data.getLocations();
            if (mLocationId > 0) {
                for (int i = 0; i < mLocationList.size(); i++) {
                    if (mLocationList.get(i).getLocationId() == mLocationId) {
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
        EventBus.getDefault().post(new DeviceRefreshEvent());
        switchToActivity(MainActivity.class);
    }

    @Override
    public void onUpdateDeviceNameFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}