package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.view.titlebar.TitleBarManager;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.BrandDetailContract;
import com.yctc.zhiting.activity.contract.SceneDevicesContract;
import com.yctc.zhiting.activity.presenter.BrandDetailPresenter;
import com.yctc.zhiting.activity.presenter.SceneDevicesPresenter;
import com.yctc.zhiting.adapter.SceneDevicesAdapter;
import com.yctc.zhiting.entity.home.DeviceMultipleBean;
import com.yctc.zhiting.entity.home.RoomDeviceListBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.RoomListBean;
import com.yctc.zhiting.entity.scene.SceneDevicesBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.widget.LocationPopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 场景可控制设备列表
 */
public class SceneDeviceActivity extends MVPBaseActivity<SceneDevicesContract.View, SceneDevicesPresenter> implements  SceneDevicesContract.View {

    @BindView(R.id.llParent)
    LinearLayout llParent;
    @BindView(R.id.rvData)
    RecyclerView rvData;
    @BindView(R.id.layout_null)
    View layoutNull;
    @BindView(R.id.tvTodo)
    TextView tvTodo;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;


    private String title;
    private LocationPopupWindow locationPopupWindow;

    private List<LocationBean> locations;
    private List<SceneDevicesBean> data = new ArrayList<>();
    private SceneDevicesAdapter sceneDevicesAdapter;

    /**
     * 1. 创建场景条件列表来
     * 2. 创建场景任务列表来
     */
    private int from;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_device;
    }

    @Override
    protected void initUI() {
        super.initUI();
        rvData.setLayoutManager(new LinearLayoutManager(this));
        sceneDevicesAdapter = new SceneDevicesAdapter();
        rvData.setAdapter(sceneDevicesAdapter);
        sceneDevicesAdapter.setDeviceListener(new SceneDevicesAdapter.OnDeviceListener() {
            @Override
            public void onDevice(DeviceMultipleBean deviceMultipleBean, int position) {
                Bundle bundle = new Bundle();
                bundle.putInt(IntentConstant.ID, deviceMultipleBean.getId());
                bundle.putString(IntentConstant.NAME, deviceMultipleBean.getName());
                bundle.putString(IntentConstant.TYPE, deviceMultipleBean.getType());
                bundle.putString(IntentConstant.LOGO_URL, deviceMultipleBean.getLogo_url());
                bundle.putString(IntentConstant.RA_NAME, deviceMultipleBean.getLocation_name());
                bundle.putInt(IntentConstant.FROM, 0);
                if (from == 1) {
                    switchToActivity(SceneDeviceStatusControlActivity.class, bundle);
                }else if (from == 2){
                    switchToActivity(TaskDeviceControlActivity.class, bundle);
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        title = getIntent().getStringExtra(IntentConstant.TITLE);
        from = getIntent().getIntExtra(IntentConstant.FROM, 0);
        setTitleRightText(getResources().getString(R.string.scene_select));
        getRightTitleText().setTextColor(UiUtil.getColor(R.color.color_3F4663));
        getRightTitleText().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        getRightTitleText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationPopupWindow!=null){
                    locationPopupWindow.show(llParent);
                }
            }
        });
        setTitleCenter(title);
        mPresenter.getRoomList();
    }

    /**
     * 设备列表
     * @param roomListBean
     */
    @Override
    public void getDeviceListSuccess(RoomDeviceListBean roomListBean) {
        if (roomListBean != null){
            if (CollectionUtil.isNotEmpty(roomListBean.getDevices())){
                setNullView(false);
                // 把数据装进各个房间
                List<DeviceMultipleBean> excludeRoom = new ArrayList<>();
                // 先把没有归属房间装进去
                for (DeviceMultipleBean dmb : roomListBean.getDevices()){
                    if (dmb.getLocation_id() == 0){
                        excludeRoom.add(dmb);
                    }
                }
                if (CollectionUtil.isNotEmpty(excludeRoom)){
                    data.add(new SceneDevicesBean(0, "", excludeRoom));
                }

                // 把对应设备装进房间
                for (LocationBean locationBean : locations){
                    List<DeviceMultipleBean> devices = new ArrayList<>();
                    for (DeviceMultipleBean deviceMultipleBean : roomListBean.getDevices()){
                        if (locationBean.getId() == deviceMultipleBean.getLocation_id()){
                            devices.add(deviceMultipleBean);
                        }
                    }
                    if (CollectionUtil.isNotEmpty(devices)){
                        data.add(new SceneDevicesBean(locationBean.getId(), locationBean.getName(), devices));
                    }
                }

                if (CollectionUtil.isNotEmpty(data)) {
                    setNullView(false);
                    sceneDevicesAdapter.setNewData(data);
                }else {
                    setNullView(true);
                }
            }else {
                setNullView(true);
            }
        }else {
            setNullView(true);
        }
    }

    /**
     * 房间列表
     * @param roomListBean
     */
    @Override
    public void getRoomListSuccess(RoomListBean roomListBean) {
        if (roomListBean!=null){
            mPresenter.getDeviceList();
            if (CollectionUtil.isNotEmpty(roomListBean.getLocations())) {
                setNullView(false);
                locations = roomListBean.getLocations();
                List<LocationBean> locationData = new ArrayList<>();
                locationData.add(new LocationBean(0, getResources().getString(R.string.home_all)));
                locationData.addAll(locations);

                locationPopupWindow = new LocationPopupWindow(this, locationData);
                locationPopupWindow.setLocationSelectListener(new LocationPopupWindow.OnLocationSelectListener() {
                    @Override
                    public void onLocationSelect(int id, String name) {
                        resetData(id);
                        locationPopupWindow.dismiss();
                    }
                });
            }else {
                setNullView(true);
            }
        }else {
            setNullView(true);
        }

    }

    /**
     * 筛选数据
     * @param id
     */
    private void resetData(int id){
        if (id == 0){
            if (CollectionUtil.isNotEmpty(data)) {
                setNullView(false);
                sceneDevicesAdapter.setNewData(data);
            }else {
                setNullView(true);
            }
        }else {
            List<SceneDevicesBean> newData = new ArrayList<>();
            for (SceneDevicesBean sceneDevicesBean : data){
                if (sceneDevicesBean.getId() == id) {
                    newData.add(sceneDevicesBean);
                }
            }
            if (CollectionUtil.isNotEmpty(newData)) {
                setNullView(false);
                sceneDevicesAdapter.setNewData(newData);
            }else {
                setNullView(true);
            }
        }
    }

    @Override
    public void getDeviceFail(int errorCode, String msg) {

    }

    @Override
    public void requestFail(int errorCode, String msg) {

    }


    private void setNullView(boolean visible){
        tvTodo.setVisibility(View.INVISIBLE);
        rvData.setVisibility(visible ? View.GONE : View.VISIBLE);
        layoutNull.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

}